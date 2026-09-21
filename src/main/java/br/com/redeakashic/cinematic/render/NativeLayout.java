package br.com.redeakashic.cinematic.render;
import br.com.redeakashic.cinematic.core.PixelLayout;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/** Live framebuffer dimensions; no contain transform, no stale export hitboxes. */
public final class NativeLayout {
    public final String profile;public int width,height,sidebar;public PixelLayout pixels;
    public NativeLayout(String profile){this.profile=profile;resize(1366,768);}
    public void resize(int w,int h){if(pixels!=null&&w==width&&h==height)return;pixels=new PixelLayout(w,h);width=w;height=h;sidebar=pixels.sidebar;}
    public static String choose(int w,int h){if(w<1||h<1)throw new IllegalArgumentException("dimensions");return "responsive";}
    public double scale(int w,int h){return 1;}
    public double localX(double x,int w,int h){return x;}
    public double localY(double y,int w,int h){return y;}
    public int hit(double x,double y){return pixels.hit(x,y);}
    public int n(String key){
        if(key.equals("count"))return 4;if(key.equals("width"))return width;if(key.equals("height"))return height;
        if(key.startsWith("button.")){String[] parts=key.split("\\.");int i=Integer.parseInt(parts[1]);
            switch(parts[2]){case "x":return (int)pixels.buttonX();case "y":return (int)pixels.buttonY(i);case "width":return (int)pixels.buttonWidth();case "height":return (int)pixels.buttonHeight();default:break;}}
        throw new IllegalArgumentException(key);
    }
    static InputStream open(String name)throws IOException{
        InputStream in=NativeLayout.class.getResourceAsStream("/assets/akashiccinematic/native/"+name);
        if(in==null)throw new IOException("Missing cinematic resource: "+name);return in;
    }
    static Properties properties(String name){Properties p=new Properties();try(InputStream in=open(name)){p.load(in);return p;}
        catch(IOException ex){throw new IllegalStateException("Metadata unavailable: "+name,ex);}}
}
