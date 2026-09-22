import java.io.*;
import java.util.*;
import java.util.jar.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import br.com.redeakashic.cinematic.bootstrap.SecondaryThemeTransformer;
public final class SecondaryTransformerProbe {
    private static int checks;
    private static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    private static byte[] read(JarFile jar,String name)throws Exception{
        JarEntry e=jar.getJarEntry(name);if(e==null)throw new FileNotFoundException(name);
        try(InputStream in=jar.getInputStream(e);ByteArrayOutputStream out=new ByteArrayOutputStream()){
            byte[] b=new byte[8192];for(int n;(n=in.read(b))!=-1;)out.write(b,0,n);return out.toByteArray();
        }
    }
    private static int hookCalls(byte[] data,String method){
        ClassNode c=new ClassNode();new ClassReader(data).accept(c,0);int count=0;
        for(Object o:c.methods)for(AbstractInsnNode n=((MethodNode)o).instructions.getFirst();n!=null;n=n.getNext())
            if(n instanceof MethodInsnNode){
                MethodInsnNode m=(MethodInsnNode)n;
                if(m.owner.equals("br/com/redeakashic/cinematic/client/SecondaryScreenTheme")&&m.name.equals(method))count++;
            }
        return count;
    }
    private static int optionFields(byte[] data){
        ClassNode c=new ClassNode();new ClassReader(data).accept(c,0);int count=0;
        for(Object o:c.methods)for(AbstractInsnNode n=((MethodNode)o).instructions.getFirst();n!=null;n=n.getNext())
            if(n instanceof FieldInsnNode){
                FieldInsnNode f=(FieldInsnNode)n;
                if(f.getOpcode()==Opcodes.GETSTATIC&&f.owner.equals("net/minecraft/client/gui/Gui")&&f.name.equals("field_110325_k"))count++;
            }
        return count;
    }
    public static void main(String[] args)throws Exception{
        try(JarFile jar=new JarFile(args[0])){
            SecondaryThemeTransformer t=new SecondaryThemeTransformer();
            byte[] screen=read(jar,"net/minecraft/client/gui/GuiScreen.class");
            byte[] button=read(jar,"net/minecraft/client/gui/GuiButton.class");
            byte[] slot=read(jar,"net/minecraft/client/gui/GuiSlot.class");
            int oldSlotFields=optionFields(slot);check(oldSlotFields>=2,"GuiSlot dirt refs found");
            byte[] ts=t.transform("net.minecraft.client.gui.GuiScreen","net.minecraft.client.gui.GuiScreen",screen);
            byte[] tb=t.transform("net.minecraft.client.gui.GuiButton","net.minecraft.client.gui.GuiButton",button);
            byte[] tl=t.transform("net.minecraft.client.gui.GuiSlot","net.minecraft.client.gui.GuiSlot",slot);
            check(!Arrays.equals(screen,ts),"GuiScreen changed");
            check(!Arrays.equals(button,tb),"GuiButton changed");
            check(!Arrays.equals(slot,tl),"GuiSlot changed");
            check(hookCalls(ts,"drawBackground")==1,"screen hook exact");
            check(hookCalls(tb,"buttonTexture")==1,"button hook exact");
            check(hookCalls(tl,"listTexture")==oldSlotFields,"every dirt ref replaced");
            check(optionFields(tl)==0,"no GuiSlot dirt refs remain");
            new ClassReader(ts);new ClassReader(tb);new ClassReader(tl);checks+=3;
            byte[] select=read(jar,"net/minecraft/client/gui/GuiSelectWorld.class");
            byte[] untouched=t.transform("net.minecraft.client.gui.GuiSelectWorld","net.minecraft.client.gui.GuiSelectWorld",select);
            check(Arrays.equals(select,untouched),"world-select action class untouched");
            ClassNode world=new ClassNode();new ClassReader(select).accept(world,0);int yesNo=0;
            for(Object o:world.methods)for(AbstractInsnNode n=((MethodNode)o).instructions.getFirst();n!=null;n=n.getNext())
                if(n instanceof TypeInsnNode&&n.getOpcode()==Opcodes.NEW&&((TypeInsnNode)n).desc.equals("net/minecraft/client/gui/GuiYesNo"))yesNo++;
            check(yesNo>=1,"delete-world confirmation remains in vanilla bytecode");
            System.out.println("RESULT transformer_checks="+checks+" slot_dirt_refs="+oldSlotFields+" failures=0");
        }
    }
}
