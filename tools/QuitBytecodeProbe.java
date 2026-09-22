import java.io.*;
import java.util.jar.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
public final class QuitBytecodeProbe {
    private static int checks;
    private static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    private static byte[] read(JarFile jar,String name)throws Exception{
        JarEntry e=jar.getJarEntry(name);if(e==null)throw new FileNotFoundException(name);
        try(InputStream in=jar.getInputStream(e);ByteArrayOutputStream out=new ByteArrayOutputStream()){
            byte[] b=new byte[8192];for(int n;(n=in.read(b))!=-1;)out.write(b,0,n);return out.toByteArray();
        }
    }
    public static void main(String[] args)throws Exception{
        try(JarFile jar=new JarFile(args[0])){
            byte[] data=read(jar,"br/com/redeakashic/cinematic/client/CinematicMenu.class");
            ClassNode c=new ClassNode();new ClassReader(data).accept(c,0);
            int shutdown=0,yesNo=0,select=0,options=0,confirmMethods=0;
            for(Object o:c.methods){
                MethodNode m=(MethodNode)o;
                if(m.name.equals("func_73878_a")||m.name.equals("confirmClicked"))confirmMethods++;
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n instanceof TypeInsnNode&&n.getOpcode()==Opcodes.NEW){
                        String x=((TypeInsnNode)n).desc;
                        if(x.equals("net/minecraft/client/gui/GuiYesNo"))yesNo++;
                        if(x.equals("net/minecraft/client/gui/GuiSelectWorld"))select++;
                        if(x.equals("net/minecraft/client/gui/GuiOptions"))options++;
                    }else if(n instanceof MethodInsnNode){
                        MethodInsnNode call=(MethodInsnNode)n;
                        if(call.owner.equals("net/minecraft/client/Minecraft")&&call.name.equals("func_71400_g")&&call.desc.equals("()V"))shutdown++;
                    }
                }
            }
            check(yesNo==0,"no quit confirmation allocation");
            check(confirmMethods==0,"no quit confirmation callback");
            check(shutdown==1,"exact one direct shutdown dispatch");
            check(select==1,"singleplayer route preserved");
            check(options==1,"options route preserved");
            System.out.println("RESULT quit_checks="+checks+" shutdown_calls="+shutdown+" failures=0");
        }
    }
}
