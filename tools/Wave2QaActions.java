package br.com.redeakashic.cinematicqa;
import java.lang.reflect.*;
import java.nio.file.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.*;
/** QA-only actions. No world creation, deletion, server login or destructive confirmation. */
public final class Wave2QaActions {
    private static GuiScreen root;
    public static int dummyConfirmCallbacks;
    private Wave2QaActions(){}
    private static Object field(Object o,String name)throws Exception{
        Class<?> c=o instanceof Class?(Class<?>)o:o.getClass();Field f;
        while(true){try{f=c.getDeclaredField(name);break;}catch(NoSuchFieldException ex){c=c.getSuperclass();if(c==null)throw ex;}}
        f.setAccessible(true);return f.get(o instanceof Class?null:o);
    }
    private static void key(GuiScreen g,int key)throws Exception{
        Method m=GuiScreen.class.getDeclaredMethod("func_73869_a",char.class,int.class);m.setAccessible(true);m.invoke(g,'\0',key);
    }
    public static String status(Minecraft mc)throws Exception{
        Class<?> theme=Class.forName("br.com.redeakashic.cinematic.client.SecondaryScreenTheme");
        Class<?> bgm=Class.forName("br.com.redeakashic.cinematic.client.MenuBgm");
        Object last=field(bgm,"lastSound");
        Object button=theme.getMethod("buttonTexture").invoke(null);
        Object list=theme.getMethod("listTexture").invoke(null);
        return "\nsecondaryFrames="+field(theme,"frames")+"\nsecondarySection="+field(theme,"lastSection")+
               "\nsecondaryButtonTexture="+button+"\nsecondaryListTexture="+list+
               "\nbgmStarts="+field(bgm,"starts")+"\nbgmStatus="+field(bgm,"status")+
               "\nbgmPlaying="+(last!=null&&mc.func_147118_V().func_147692_c((ISound)last))+
               "\nbgmId="+System.identityHashCode(last)+"\ndummyConfirmCallbacks="+dummyConfirmCallbacks;
    }
    private static GuiScreen base(GuiScreen current){
        if(root==null&&current!=null&&current.getClass().getName().endsWith("CinematicMenu"))root=current;
        if(root==null)throw new IllegalStateException("Cinematic root not captured");
        return root;
    }
    private static GuiScreen open(String name,Minecraft mc,GuiScreen current){
        GuiScreen r=base(current);
        if(name.equals("root"))return r;
        if(name.equals("options"))return new GuiOptions(r,mc.field_71474_y);
        if(name.equals("singleplayer"))return new GuiSelectWorld(r);
        if(name.equals("createworld"))return new GuiCreateWorld(r);
        if(name.equals("language"))return new GuiLanguage(r,mc.field_71474_y,mc.func_135016_M());
        if(name.equals("resourcepacks"))return new GuiScreenResourcePacks(r);
        if(name.equals("controls"))return new GuiControls(r,mc.field_71474_y);
        if(name.equals("video"))return new GuiVideoSettings(r,mc.field_71474_y);
        if(name.equals("sound"))return new GuiScreenOptionsSounds(r,mc.field_71474_y);
        if(name.equals("chat"))return new ScreenChatOptions(r,mc.field_71474_y);
        if(name.equals("snooper"))return new GuiSnooper(r,mc.field_71474_y);
        if(name.equals("rename"))return new GuiRenameWorld(r,"qa-nonexistent-world");
        if(name.equals("flatworld")){
            GuiCreateWorld create=new GuiCreateWorld(r);
            return new GuiCreateFlatWorld(create,"3;minecraft:bedrock,2*minecraft:dirt,minecraft:grass;1;");
        }
        if(name.equals("flatpresets")){
            GuiCreateWorld create=new GuiCreateWorld(r);
            return new GuiFlatPresets(new GuiCreateFlatWorld(create,"3;minecraft:bedrock,2*minecraft:dirt,minecraft:grass;1;"));
        }
        if(name.equals("yesno"))return new GuiYesNo((result,id)->{dummyConfirmCallbacks++;mc.func_147108_a(r);},
                "TESTE LOCAL","Confirmações destrutivas continuam nativas.",9901);
        throw new IllegalArgumentException("unknown screen "+name);
    }
    public static boolean command(String[] a,Minecraft mc,GuiScreen current,Path qa)throws Exception{
        if(a[1].equals("wave2screen")){
            if(mc.field_71441_e!=null)throw new IllegalStateException("world");
            mc.func_147108_a(open(a[2],mc,current));return true;
        }
        if(a[1].equals("wave2button")){
            if(current==null||!current.getClass().getName().equals("rtg.client.gui.WorldTypeMessageGUI"))throw new IllegalStateException("RTG wrapper only");
            int id=Integer.parseInt(a[2]);if(id!=0)throw new IllegalArgumentException("Only RTG OK allowed");
            Method m=GuiScreen.class.getDeclaredMethod("func_146284_a",GuiButton.class);m.setAccessible(true);
            GuiButton target=null;for(Object x:(java.util.List<?>)field(current,"field_146292_n")){GuiButton b=(GuiButton)x;if(b.field_146127_k==id)target=b;}
            if(target==null||!target.field_146124_l||!target.field_146125_m)throw new IllegalStateException("RTG OK missing");
            m.invoke(current,target);return true;
        }
        if(a[1].equals("wave2cancel")){
            if(!(current instanceof GuiYesNo))throw new IllegalStateException("not yesno");
            Method m=GuiScreen.class.getDeclaredMethod("func_146284_a",GuiButton.class);m.setAccessible(true);
            GuiButton cancel=null;for(Object x:(java.util.List<?>)field(current,"field_146292_n")){
                GuiButton b=(GuiButton)x;if(b.field_146127_k==1)cancel=b;
            }
            if(cancel==null)throw new IllegalStateException("cancel missing");m.invoke(current,cancel);return true;
        }
        if(a[1].equals("wave2quit")){
            GuiScreen r=base(current);mc.func_147108_a(r);
            int focus=(Integer)field(r,"focus");int guard=0;
            while(focus!=3&&guard++<5){key(r,208);focus=(Integer)field(r,"focus");}
            if(focus!=3)throw new IllegalStateException("quit focus");key(r,28);return true;
        }
        return false;
    }
}
