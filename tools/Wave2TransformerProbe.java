import java.io.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import br.com.redeakashic.cinematic.bootstrap.SecondaryThemeTransformer;
public final class Wave2TransformerProbe {
    private static int checks;
    private static final String HOOK="br/com/redeakashic/cinematic/client/SecondaryScreenTheme";
    private static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    private static byte[] read(ZipFile z,String name)throws Exception{
        ZipEntry e=z.getEntry(name);if(e==null)throw new FileNotFoundException(name);
        try(InputStream in=z.getInputStream(e)){
            ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;
            while((n=in.read(b))!=-1)out.write(b,0,n);return out.toByteArray();
        }
    }
    private static ClassNode parse(byte[] bytes){
        ClassNode n=new ClassNode();new ClassReader(bytes).accept(n,0);return n;
    }
    private static int hooks(ClassNode c,String method){
        int count=0;
        for(Object value:c.methods){MethodNode m=(MethodNode)value;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                if(n instanceof MethodInsnNode){MethodInsnNode x=(MethodInsnNode)n;
                    if(x.owner.equals(HOOK)&&x.name.equals(method))count++;
                }
            }
        }return count;
    }
    private static void verify(ClassNode c)throws Exception{
        for(Object value:c.methods){MethodNode m=(MethodNode)value;
            new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);
        }
    }
    public static void main(String[] args)throws Exception{
        SecondaryThemeTransformer t=new SecondaryThemeTransformer();
        try(ZipFile z=new ZipFile(args[0])){
            String[] classes={"net/minecraft/client/gui/GuiScreen.class","net/minecraft/client/gui/GuiButton.class","net/minecraft/client/gui/GuiSlot.class"};
            String[] names={"net.minecraft.client.gui.GuiScreen","net.minecraft.client.gui.GuiButton","net.minecraft.client.gui.GuiSlot"};
            String[] expected={"drawBackground","buttonTexture","listTexture"};
            int[] counts={1,1,2};
            for(int i=0;i<classes.length;i++){
                byte[] original=read(z,classes[i]);
                byte[] changed=t.transform(names[i],names[i],original);
                check(changed!=original,"transformed "+names[i]);
                ClassNode node=parse(changed);verify(node);
                check(hooks(node,expected[i])==counts[i],"hook count "+names[i]);
                byte[] again=t.transform(names[i],names[i],changed);
                check(again==changed,"idempotent "+names[i]);
            }
        }
        byte[] garbage={1,2,3,4};
        check(t.transform("net.minecraft.client.gui.GuiButton","net.minecraft.client.gui.GuiButton",garbage)==garbage,"malformed fail-open");
        System.setProperty("akashic.cinematic.disabled","true");
        try(ZipFile z=new ZipFile(args[0])){
            byte[] original=read(z,"net/minecraft/client/gui/GuiScreen.class");
            check(t.transform("net.minecraft.client.gui.GuiScreen","net.minecraft.client.gui.GuiScreen",original)==original,"kill switch");
        }finally{System.clearProperty("akashic.cinematic.disabled");}
        try(ZipFile app=new ZipFile(args[1])){
            ClassNode menu=parse(read(app,"br/com/redeakashic/cinematic/client/CinematicMenu.class"));
            int shutdown=0,quitDialog=0;
            for(Object value:menu.methods){MethodNode m=(MethodNode)value;
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n instanceof MethodInsnNode){MethodInsnNode x=(MethodInsnNode)n;
                        if(x.owner.equals("net/minecraft/client/Minecraft")&&x.name.equals("func_71400_g"))shutdown++;
                        if(x.owner.equals("net/minecraft/client/gui/GuiYesNo")&&x.name.equals("<init>"))quitDialog++;
                    }
                }
            }
            check(shutdown==1,"direct main-menu shutdown");
            check(quitDialog==0,"no main-menu quit dialog construction");
        }
        System.out.println("RESULT transformer_checks="+checks+" failures=0");
    }
}
