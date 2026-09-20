package br.com.redeakashic.cinematic.bootstrap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
/** Only calls after successful creation of the Minecraft display, never modifies its creation. */
public final class FirstContextTransformer implements IClassTransformer {
    private static final String HOOK="br/com/redeakashic/cinematic/earlygl/FirstFrame";
    @Override public byte[] transform(String name,String transformedName,byte[] input){
        if(input==null||!("net.minecraft.client.Minecraft".equals(name)||"net.minecraft.client.Minecraft".equals(transformedName)))return input;
        if(Boolean.getBoolean("akashic.cinematic.disabled")||Boolean.getBoolean("akashic.cinematic.disableSplash"))return input;
        try{
            ClassNode c=new ClassNode();new ClassReader(input).accept(c,0);MethodNode target=null;int methods=0;
            for(Object value:c.methods){MethodNode m=(MethodNode)value;
                if(!m.desc.equals("()V"))continue;
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)n;
                    if(call.owner.equals(HOOK))return input;
                    if(call.owner.equals("net/minecraft/client/renderer/OpenGlHelper")&&call.desc.equals("()V")&&(call.name.equals("func_77474_a")||call.name.equals("initializeTextures"))){target=m;methods++;}
                }
            }
            if(methods!=1||target==null)return input;
            List<MethodInsnNode> anchors=new ArrayList<MethodInsnNode>();int forge=0;
            for(AbstractInsnNode n=target.instructions.getFirst();n!=null;n=n.getNext())if(n instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)n;
                if(call.owner.equals("net/minecraft/client/renderer/OpenGlHelper"))break;
                if(call.getOpcode()==Opcodes.INVOKESTATIC&&call.desc.equals("()V")&&((call.owner.equals("net/minecraftforge/client/ForgeHooksClient")&&call.name.equals("createDisplay"))||(call.owner.equals("org/lwjgl/opengl/Display")&&call.name.equals("create")))){anchors.add(call);if(call.name.equals("createDisplay"))forge++;}
            }
            if(forge!=1||anchors.size()>2){System.err.println("[AkashicCinematic] First-context anchor unsupported; later startup path retained");return input;}
            for(MethodInsnNode anchor:anchors)target.instructions.insert(anchor,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"present","()V",false));
            ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(out);System.out.println("[AkashicCinematic] First-context presentation hooks installed: "+anchors.size());return out.toByteArray();
        }catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] First-context patch rejected: "+ex);return input;}
    }
}
