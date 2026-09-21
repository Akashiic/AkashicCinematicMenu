package br.com.redeakashic.cinematic.render;
import br.com.redeakashic.cinematic.core.DepthCamera;
/** Static splash stays lightweight; the menu acquires three actual RGBA depth planes lazily. */
public final class BackdropRenderer implements AutoCloseable {
    private final NativeTexture art=new NativeTexture("backdrop.jpg");private NativeTexture blurred;
    private final NativeTexture[] planes=new NativeTexture[4],glass=new NativeTexture[4];
    private final DepthCamera camera=new DepthCamera();private boolean ready,failed,layered;
    private double u0,v0,u1=1,v1=1;private int width,height;
    public static int depthDraws,fallbacks;
    private boolean prepare(){
        if(ready)return true;if(failed)return false;
        try{String[] n={"sky","architecture","angel","occluder"};for(int i=0;i<4;i++){
            planes[i]=new NativeTexture("depth/"+n[i]+".png");glass[i]=new NativeTexture("depth/"+n[i]+"-blur.png");
            if(planes[i].width!=DepthCamera.TEXTURE_W||planes[i].height!=DepthCamera.TEXTURE_H||glass[i].width!=planes[i].width||glass[i].height!=planes[i].height)throw new IllegalStateException("Depth layer dimensions");
        }ready=true;return true;}catch(RuntimeException|LinkageError e){releasePlanes();failed=true;fallbacks++;System.err.println("[AkashicCinematic] Depth assets unavailable; static approved scene retained: "+e);return false;}
    }
    public void draw(int w,int h,double a){draw(w,h,a,0,0);}
    public void draw(int w,int h,double a,double px,double py){
        if(w<1||h<1)return;layered=false;width=w;height=h;
        double s=Math.max((double)w/art.width,(double)h/art.height),us=w/(art.width*s),vs=h/(art.height*s);
        u0=(1-us)*.5;v0=(1-vs)*.015;u1=u0+us;v1=v0+vs;art.draw(0,0,w,h,u0,v0,u1,v1,a,0xFFFFFF);
    }
    public void drawDepth(int w,int h,double a,double px,double py,boolean motion){
        if(!motion||!prepare()){draw(w,h,a);return;}width=w;height=h;layered=true;camera.update(w,h,px,py);
        for(int i=0;i<4;i++)planes[i].draw(0,0,w,h,camera.u(i==3?1:i,0),camera.v(i==3?1:i,0),camera.u(i==3?1:i,1),camera.v(i==3?1:i,1),a,0xFFFFFF);depthDraws++;
    }
    public void glass(double x,double y,double w,double h,double a){
        if(layered){for(int i=0;i<4;i++)glass[i].draw(x,y,w,h,camera.u(i==3?1:i,x/width),camera.v(i==3?1:i,y/height),camera.u(i==3?1:i,(x+w)/width),camera.v(i==3?1:i,(y+h)/height),a,0xFFFFFF);return;}
        if(blurred==null)blurred=new NativeTexture("backdrop-blur.jpg");
        blurred.draw(x,y,w,h,u0+(u1-u0)*x/width,v0+(v1-v0)*y/height,u0+(u1-u0)*(x+w)/width,v0+(v1-v0)*(y+h)/height,a,0xFFFFFF);
    }
    private void releasePlanes(){for(int i=0;i<4;i++){if(planes[i]!=null){planes[i].close();planes[i]=null;}if(glass[i]!=null){glass[i].close();glass[i]=null;}}ready=false;}
    @Override public void close(){releasePlanes();art.close();if(blurred!=null){blurred.close();blurred=null;}}
}
