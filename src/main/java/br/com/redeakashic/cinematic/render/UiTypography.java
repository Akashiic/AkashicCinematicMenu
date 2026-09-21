package br.com.redeakashic.cinematic.render;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/** Size-bucketed Latin-1 atlases. Glyphs are selected at >= requested pixel size, not enlarged UI bitmaps. */
public final class UiTypography implements AutoCloseable {
    private final Map<String,Face> faces=new HashMap<String,Face>();
    private static final int[] BODY={16,24,36,48},SERIF={24,48,72,96,144};
    private static final class Face {
        final double size,ascent,width,height;final double[][] glyph=new double[256][];final String path;NativeTexture texture;
        Face(String name){path="fonts/"+name;Properties p=NativeLayout.properties(path+".properties");size=Double.parseDouble(p.getProperty("size"));ascent=Double.parseDouble(p.getProperty("ascent"));width=Double.parseDouble(p.getProperty("width"));height=Double.parseDouble(p.getProperty("height"));
            for(int c=32;c<256;c++){String[] row=p.getProperty(Integer.toString(c)).split(",");glyph[c]=new double[row.length];for(int i=0;i<row.length;i++)glyph[c][i]=Double.parseDouble(row[i]);}}
        NativeTexture texture(){if(texture==null)texture=new NativeTexture(path+".png");return texture;}
    }
    private Face face(boolean serif,double size){int[] buckets=serif?SERIF:BODY;int choice=buckets[buckets.length-1];for(int n:buckets)if(n>=size){choice=n;break;}String name=(serif?"serif-":"body-")+choice;
        Face f=faces.get(name);if(f==null){f=new Face(name);faces.put(name,f);}return f;}
    private static int glyph(char c){if(c=='\u2014'||c=='\u2013')return '-';if(c=='\u2019')return '\'';return c>=32&&c<=255?c:'?';}
    public double width(String s,boolean serif,double size,double tracking){size=Math.max(serif?12:9,size);Face f=face(serif,size);double total=0;for(int i=0;i<s.length();i++)total+=f.glyph[glyph(s.charAt(i))][6]*size/f.size+(i+1<s.length()?tracking:0);return total;}
    public void draw(String text,double x,double y,double size,double maxWidth,boolean serif,double tracking,int rgb,double alpha){
        if(text==null||text.isEmpty()||alpha<=0)return;
        if(!Double.isFinite(size)||size<=0||maxWidth<=0)throw new IllegalArgumentException("text dimensions");
        size=Math.max(serif?12:9,size);
        double measured=width(text,serif,size,tracking);
        if(measured>maxWidth && size*maxWidth/measured<(serif?12:9)){
            while(text.length()>1&&width(text+"...",serif,size,tracking)>maxWidth)text=text.substring(0,text.length()-1);
            text+="...";measured=width(text,serif,size,tracking);
        }
        if(measured>maxWidth){double fit=maxWidth/measured;size*=fit;tracking*=fit;}
        Face f=face(serif,size);double scale=size/f.size,baseline=Math.round(y+f.ascent*scale);x=Math.round(x);
        for(int i=0;i<text.length();i++){double[] g=f.glyph[glyph(text.charAt(i))];
            f.texture().draw(Math.round(x+g[4]*scale),Math.round(baseline+g[5]*scale),g[2]*scale,g[3]*scale,g[0]/f.width,g[1]/f.height,(g[0]+g[2])/f.width,(g[1]+g[3])/f.height,alpha,rgb);
            x+=g[6]*scale+tracking;
        }
    }
    public void centered(String text,double center,double y,double size,double maxWidth,boolean serif,double tracking,int color,double alpha){
        double measured=Math.min(maxWidth,width(text,serif,size,tracking));draw(text,center-measured/2,y,size,maxWidth,serif,tracking,color,alpha);
    }
    @Override public void close(){for(Face f:faces.values())if(f.texture!=null)f.texture.close();faces.clear();}
}
