import java.nio.file.*;
import java.io.*;
import br.com.redeakashic.cinematic.client.LocalMenuMusicPack;
import net.minecraft.util.ResourceLocation;
public final class LocalMusicPackProbe {
    private static int checks;static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);Files.createDirectory(root);Path folder=root.resolve("bgm");Files.createDirectory(folder);
        LocalMenuMusicPack pack=new LocalMenuMusicPack(folder.toFile());
        check(!pack.ready(),"missing file silent fallback");
        ResourceLocation json=new ResourceLocation("akashiccinematic","sounds.json"),track=new ResourceLocation("akashiccinematic","sounds/bgm/menu.ogg");
        check(pack.func_110589_b(json)&&!pack.func_110589_b(track),"missing event manifest still readable");
        Files.write(folder.resolve("menu.ogg"),new byte[100]);pack.refresh();check(!pack.ready(),"bad signature rejected");
        Files.copy(Paths.get(args[1]),folder.resolve("menu.ogg"),StandardCopyOption.REPLACE_EXISTING);pack.refresh();check(pack.ready(),"supplied Vorbis accepted");
        check(pack.func_110589_b(track),"exact track key accepted");
        check(!pack.func_110589_b(new ResourceLocation("minecraft","sounds/bgm/menu.ogg")),"vanilla domain not changed");
        check(!pack.func_110589_b(new ResourceLocation("akashiccinematic","../menu.ogg")),"unrelated path denied");
        try(InputStream in=pack.func_110590_a(json)){byte[] b=new byte[512];int n=in.read(b);String s=new String(b,0,n,"UTF-8");check(s.contains("\"stream\":true")&&s.contains("\"category\":\"music\""),"streaming MUSIC category");}
        try(InputStream in=pack.func_110590_a(track)){check(in.read()=='O'&&in.read()=='g'&&in.read()=='g'&&in.read()=='S',"real asset readable");}
        Files.move(folder.resolve("menu.ogg"),folder.resolve("removed.ogg"));pack.refresh();check(!pack.ready(),"removal observed on reload");
        try(InputStream in=pack.func_110590_a(json)){check(in.read()=='{'&&in.read()=='}',"empty manifest releases replacement");}
        System.out.println("RESULT local_pack_checks="+checks+" failures=0");
    }
}
