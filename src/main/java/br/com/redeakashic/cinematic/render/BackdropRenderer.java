package br.com.redeakashic.cinematic.render;
/** One top-anchored cover mapping for ALL layers; no second full-frame screenshot over the scenery. */
public final class BackdropRenderer implements AutoCloseable {
    private final NativeTexture art=new NativeTexture("backdrop.jpg");private NativeTexture blurred;
    private double u0,v0,u1=1,v1=1;private int width,height;
    public void draw(int w,int h,double alpha){draw(w,h,alpha,0,0);}
    public void draw(int w,int h,double alpha,double px,double py){
        if(w<1||h<1)return;width=w;height=h;
        double scale=Math.max((double)w/art.width,(double)h/art.height),us=w/(art.width*scale),vs=h/(art.height*scale);
        u0=(1-us)*.5;v0=(1-vs)*.015;u1=u0+us;v1=v0+vs;
        // Motion uses only actual spare UV room. Halo stays top-anchored, with no exposed borders.
        double dx=Math.max(-1,Math.min(1,px))*Math.min(.003,Math.min(u0,1-u1));
        double dy=Math.max(-1,Math.min(1,py))*Math.min(.002,Math.min(v0,1-v1));
        u0+=dx;u1+=dx;v0+=dy;v1+=dy;art.draw(0,0,w,h,u0,v0,u1,v1,alpha,0xFFFFFF);
    }
    public void glass(double x,double y,double w,double h,double alpha){
        if(blurred==null)blurred=new NativeTexture("backdrop-blur.jpg");
        blurred.draw(x,y,w,h,u0+(u1-u0)*x/width,v0+(v1-v0)*y/height,u0+(u1-u0)*(x+w)/width,v0+(v1-v0)*(y+h)/height,alpha,0xFFFFFF);
    }
    @Override public void close(){art.close();if(blurred!=null){blurred.close();blurred=null;}}
}
