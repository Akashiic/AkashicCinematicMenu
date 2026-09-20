package br.com.redeakashic.cinematic.render;
import java.util.Properties;
/** Raster atlas, no TTF/OTF, native font manager or AWT font discovery during startup. */
final class BitmapText implements AutoCloseable {
    private final NativeTexture atlas;
    private final double size;
    private final double[] advance=new double[256];
    BitmapText(String name){
        Properties p=NativeLayout.properties(name+".properties");size=Double.parseDouble(p.getProperty("size"));
        for(int i=32;i<256;i++)advance[i]=Double.parseDouble(p.getProperty(Integer.toString(i)));
        atlas=new NativeTexture(name+".png");
    }
    private int glyph(char c){return c>=32&&c<=255?c:'?';}
    private double width(String s,double factor){double r=0;for(int i=0;i<s.length();i++)r+=advance[glyph(s.charAt(i))]*factor;return r;}
    void draw(String text,double x,double y,double fontSize,double maxWidth,int rgb,double alpha){
        String s=text==null?"":text;double f=fontSize/size;
        if(width(s,f)>maxWidth){while(s.length()>0&&width(s+"...",f)>maxWidth)s=s.substring(0,s.length()-1);s+="...";}
        for(int i=0;i<s.length();i++){
            int c=glyph(s.charAt(i)),k=c-32;double tx=(k%16)*64,ty=(k/16)*64;
            atlas.draw(x-2*f,y-2*f,64*f,64*f,tx/1024,ty/896,(tx+64)/1024,(ty+64)/896,alpha,rgb);
            x+=advance[c]*f;
        }
    }
    void drawFit(String text,double x,double y,double fontSize,double maxWidth,int rgb,double alpha){
        String s=text==null?"":text;double measured=width(s,fontSize/size);
        double fit=measured>maxWidth?fontSize*maxWidth/measured:fontSize;
        draw(s,x,y,fit,maxWidth+.01,rgb,alpha);
    }
    @Override public void close(){atlas.close();}
}
