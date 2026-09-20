package br.com.redeakashic.cinematic.bootstrap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
/** Cover graphics-ready startup, before FML creates the progress renderer. */
public final class EarlyWindowTransformer implements IClassTransformer {
    private static final String TARGET="net.minecraft.client.Minecraft";
    private static final String HOOK="br/com/redeakashic/cinematic/bootstrap/EarlyWindowHooks";
    @Override public byte[] transform(String name,String transformedName,byte[] input){
        if(input==null||(!TARGET.equals(name)&&!TARGET.equals(transformedName)))return input;
        if(Boolean.getBoolean("akashic.cinematic.disabled")||Boolean.getBoolean("akashic.cinematic.disableSplash"))return input;
        try{
            ClassNode node=new ClassNode();new ClassReader(input).accept(node,0);
            MethodNode target=null;MethodInsnNode anchor=null;int count=0;
            for(Object object:node.methods){MethodNode m=(MethodNode)object;
                if(!m.desc.equals("()V"))continue;
                for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;
                    if(call.owner.equals(HOOK))return input;
                    if(call.getOpcode()==Opcodes.INVOKESTATIC&&call.owner.equals("net/minecraft/client/renderer/OpenGlHelper")&&call.desc.equals("()V")&&(call.name.equals("func_77474_a")||call.name.equals("initializeTextures"))){count++;target=m;anchor=call;}
                }
            }
            if(count!=1||target==null){System.err.println("[AkashicCinematic] Early-window anchor unsupported: "+count+"; original retained");return input;}
            target.instructions.insert(anchor,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"graphicsReady","()V",false));
            ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(out);
            System.out.println("[AkashicCinematic] Graphics-ready hook installed after OpenGlHelper in "+target.name);
            return out.toByteArray();
        }catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] Early-window splice rejected: "+ex);return input;}
    }
}
