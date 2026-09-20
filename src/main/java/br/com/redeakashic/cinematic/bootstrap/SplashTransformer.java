package br.com.redeakashic.cinematic.bootstrap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
/** Fail-open narrow splice: no replacement of Forge loop, synchronization or GL ownership. */
public final class SplashTransformer implements IClassTransformer {
    private static final String TARGET="cpw.mods.fml.client.SplashProgress$3";
    private static final String HOOK="br/com/redeakashic/cinematic/bootstrap/SplashHooks";
    @Override public byte[] transform(String name,String transformedName,byte[] bytes){
        if(bytes==null||(!TARGET.equals(name)&&!TARGET.equals(transformedName))||Boolean.getBoolean("akashic.cinematic.disabled")||Boolean.getBoolean("akashic.cinematic.disableSplash"))return bytes;
        try{
            ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);MethodNode run=null;
            for(Object v:c.methods){MethodNode m=(MethodNode)v;if(m.name.equals("run")&&m.desc.equals("()V"))run=m;}
            if(run==null)return bytes;
            List<MethodInsnNode> updates=new ArrayList<MethodInsnNode>(),clears=new ArrayList<MethodInsnNode>();
            MethodInsnNode acquire=null;
            for(AbstractInsnNode n=run.instructions.getFirst();n!=null;n=n.getNext())if(n instanceof MethodInsnNode){
                MethodInsnNode m=(MethodInsnNode)n;if(m.owner.equals(HOOK))return bytes;
                if(m.owner.equals("org/lwjgl/opengl/Display")&&m.name.equals("update")&&m.desc.equals("()V"))updates.add(m);
                if(m.owner.equals("cpw/mods/fml/client/SplashProgress$3")&&m.name.equals("clearGL")&&m.desc.equals("()V"))clears.add(m);
                if(m.owner.equals("java/util/concurrent/Semaphore")&&m.name.equals("acquireUninterruptibly"))acquire=m;
            }
            if(updates.size()!=1||clears.size()!=2||acquire==null){System.err.println("[AkashicCinematic] Unsupported splash structure: original retained");return bytes;}
            AbstractInsnNode anchor=acquire.getPrevious();while(anchor!=null&&anchor.getOpcode()<0)anchor=anchor.getPrevious();
            if(!(anchor instanceof FieldInsnNode))return bytes;
            FieldInsnNode f=(FieldInsnNode)anchor;
            if(!f.owner.equals("cpw/mods/fml/client/SplashProgress")||!f.name.equals("mutex"))return bytes;
            run.instructions.insertBefore(anchor,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"frame","()V",false));
            // Only the final clearGL releases our textures; pause/resume retains them.
            run.instructions.insertBefore(clears.get(1),new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"finish","()V",false));
            ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(out);
            System.out.println("[AkashicCinematic] Forge splash overlay hooks installed; original loop retained");return out.toByteArray();
        }catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] Splash patch rejected: "+ex);return bytes;}
    }
}
