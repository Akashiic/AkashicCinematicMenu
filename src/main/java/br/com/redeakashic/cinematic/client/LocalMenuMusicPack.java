package br.com.redeakashic.cinematic.client;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.*;
import net.minecraft.util.ResourceLocation;
/** Single local, user-supplied Ogg/Vorbis recording. No URL fetching or bundled commercial track. */
public final class LocalMenuMusicPack implements IResourcePack {
    private static final String DOMAIN="akashiccinematic",AUDIO="sounds/bgm/menu.ogg";
    private static final byte[] EVENT=("{\"bgm.menu\":{\"category\":\"music\",\"sounds\":[{\"name\":\"akashiccinematic:bgm/menu\",\"stream\":true}]}}").getBytes(StandardCharsets.UTF_8);
    private final File folder,file;private volatile boolean ready;private volatile String state="MISSING";
    public LocalMenuMusicPack(File directory){folder=directory;file=new File(directory,"menu.ogg");refresh();}
    public boolean ready(){return ready;}
    public String status(){return state;}
    public String path(){return file.getAbsolutePath();}
    public void refresh(){
        ready=false;state="MISSING";
        if(!file.isFile())return;
        try{
            if(!file.getCanonicalFile().getParentFile().equals(folder.getCanonicalFile())||file.length()<64||file.length()>134217728L){state="REJECTED_PATH_OR_SIZE";return;}
            byte[] header=new byte[65536];int size;
            try(InputStream in=new FileInputStream(file)){size=in.read(header);}
            if(size<32||header[0]!='O'||header[1]!='g'||header[2]!='g'||header[3]!='S'){state="NOT_OGG";return;}
            boolean vorbis=false;for(int i=0;i+7<size;i++)if(header[i]==1&&header[i+1]=='v'&&header[i+2]=='o'&&header[i+3]=='r'&&header[i+4]=='b'&&header[i+5]=='i'&&header[i+6]=='s'){vorbis=true;break;}
            if(!vorbis){state="NOT_VORBIS";return;}ready=true;state="READY";
        }catch(IOException|SecurityException ex){state="READ_FAILED";}
    }
    @Override public InputStream func_110590_a(ResourceLocation location)throws IOException{
        if(!DOMAIN.equals(location.func_110624_b()))throw new FileNotFoundException(location.toString());
        if(location.func_110623_a().equals("sounds.json"))return new ByteArrayInputStream(ready?EVENT:"{}".getBytes(StandardCharsets.UTF_8));
        if(location.func_110623_a().equals(AUDIO)&&ready)return new FileInputStream(file);
        throw new FileNotFoundException(location.toString());
    }
    @Override public boolean func_110589_b(ResourceLocation location){
        return DOMAIN.equals(location.func_110624_b())&&(location.func_110623_a().equals("sounds.json")||(ready&&location.func_110623_a().equals(AUDIO)));
    }
    @Override public Set<String> func_110587_b(){refresh();return Collections.singleton(DOMAIN);}
    @Override public IMetadataSection func_135058_a(IMetadataSerializer serializer,String section){return null;}
    @Override public BufferedImage func_110586_a()throws IOException{throw new FileNotFoundException("No icon");}
    @Override public String func_130077_b(){return "Akashic local menu soundtrack";}
}
