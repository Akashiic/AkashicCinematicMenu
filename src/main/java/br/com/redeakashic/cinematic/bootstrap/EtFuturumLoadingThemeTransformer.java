package br.com.redeakashic.cinematic.bootstrap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
/** Et Futurum replaces vanilla loading/terrain drawing; theme only its dedicated background pass. */
public final class EtFuturumLoadingThemeTransformer implements IClassTransformer {
    private static final String TARGET="ganymedes01.etfuturum.client.loading.LoadingScreenBackgroundRenderer";
    private static final String HOOK="br/com/redeakashic/cinematic/client/TransitionTheme";
    @Override public byte[] transform(String name,String mapped,byte[] bytes){
        if(bytes==null||(!TARGET.equals(name)&&!TARGET.equals(mapped))||Boolean.getBoolean("akashic.cinematic.disabled"))return bytes;
        try{
            ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);MethodNode method=null;MethodInsnNode anchor=null;int matches=0;
            for(Object value:node.methods){MethodNode m=(MethodNode)value;if(!m.name.equals("render")||!m.desc.equals("(Lnet/minecraft/client/Minecraft;II)V"))continue;
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)n;
                    if(call.owner.equals(HOOK))return bytes;
                    if(call.owner.equals(node.name)&&call.name.equals("renderDefaultBackground")&&call.desc.equals(m.desc)){method=m;anchor=call;matches++;}
                }
            }
            if(matches!=1||method==null){System.err.println("[AkashicCinematic] Et Futurum loading layout unsupported; original background retained");return bytes;}
            method.instructions.insert(anchor,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"etfuturum","()V",false));
            ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(writer);
            System.out.println("[AkashicCinematic] Et Futurum background pass themed; progress and chunk-map untouched");return writer.toByteArray();
        }catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] Et Futurum background hook skipped: "+ex);return bytes;}
    }
}
