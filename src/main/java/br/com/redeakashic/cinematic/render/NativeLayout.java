package br.com.redeakashic.cinematic.render;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/** Immutable, build-exported hitboxes. Same transform for pixels and input. */
public final class NativeLayout {
    public final String profile;
    public final int width,height,sidebar;
    private final Properties data;
    public NativeLayout(String profile) {
        if (!profile.matches("hd|compact|classic|wide")) throw new IllegalArgumentException("profile");
        this.profile=profile; data=properties(profile+"/layout.properties");
        width=n("width");height=n("height");sidebar=n("sidebar");
        if(width<1||height<1||n("count")!=4||sidebar<=0||sidebar>=width) throw new IllegalStateException("Invalid layout");
        for(int i=0;i<4;i++) {
            int x=n("button."+i+".x"),y=n("button."+i+".y"),w=n("button."+i+".width"),h=n("button."+i+".height");
            if(x<0||y<0||w<1||h<1||x+w>sidebar+1||y+h>height) throw new IllegalStateException("Bad hitbox");
        }
    }
    public static String choose(int w,int h) {
        if(w<1||h<1) throw new IllegalArgumentException("dimensions");
        double ratio=(double)w/h;
        if(ratio<1.52)return "classic";
        if(ratio>2.08)return "wide";
        return w<1100||h<600?"compact":"hd";
    }
    public int n(String key){String v=data.getProperty(key);if(v==null)throw new IllegalArgumentException(key);return Integer.parseInt(v);}
    public double scale(int w,int h){return Math.min((double)w/width,(double)h/height);}
    public double localX(double x,int w,int h){double s=scale(w,h);return (x-(w-width*s)/2)/s;}
    public double localY(double y,int w,int h){double s=scale(w,h);return (y-(h-height*s)/2)/s;}
    public int hit(double x,double y){
        if(!Double.isFinite(x)||!Double.isFinite(y))return -1;
        for(int i=0;i<4;i++){String k="button."+i+".";
            if(x>=n(k+"x")&&x<n(k+"x")+n(k+"width")&&y>=n(k+"y")&&y<n(k+"y")+n(k+"height"))return i;
        }return -1;
    }
    static InputStream open(String name)throws IOException{
        InputStream in=NativeLayout.class.getResourceAsStream("/assets/akashiccinematic/native/"+name);
        if(in==null)throw new IOException("Missing approved asset: "+name);return in;
    }
    static Properties properties(String name){
        Properties p=new Properties();try(InputStream in=open(name)){p.load(in);return p;}
        catch(IOException ex){throw new IllegalStateException("Asset metadata unavailable",ex);}
    }
}
