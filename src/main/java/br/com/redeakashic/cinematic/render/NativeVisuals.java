package br.com.redeakashic.cinematic.render;
import br.com.redeakashic.cinematic.core.LoadState;
import org.lwjgl.opengl.GL11;
/** Native compositor; textures carry the approved art, actual actions/progress remain code. */
public final class NativeVisuals implements AutoCloseable {
    public final NativeLayout layout;
    private NativeTexture panel,scene,boot;
    private final NativeTexture[] buttons=new NativeTexture[4],hover=new NativeTexture[4];
    private BitmapText body,serif;
    public NativeVisuals(String profile){layout=new NativeLayout(profile);}
    private NativeTexture tex(String n){return new NativeTexture(layout.profile+"/"+n);}
    public void prepareMenu(){
        if(panel!=null)return;
        try{panel=tex("panel.png");scene=tex("scene.jpg");for(int i=0;i<4;i++){buttons[i]=tex("button-"+i+".png");hover[i]=tex("button-"+i+"-hover.png");}}
        catch(RuntimeException|LinkageError ex){close();throw ex;}
    }
    public void prepareBoot(){
        if(boot!=null)return;
        try{boot=tex("boot.png");body=new BitmapText("body");serif=new BitmapText("serif");}
        catch(RuntimeException|LinkageError ex){close();throw ex;}
    }
    public static void rect(double x,double y,double w,double h,int rgb,double a){
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glColor4d(((rgb>>16)&255)/255.0,((rgb>>8)&255)/255.0,(rgb&255)/255.0,Math.max(0,Math.min(1,a)));
        GL11.glBegin(GL11.GL_QUADS);GL11.glVertex2d(x,y);GL11.glVertex2d(x+w,y);GL11.glVertex2d(x+w,y+h);GL11.glVertex2d(x,y+h);GL11.glEnd();
    }
    public void transform(int w,int h){
        double s=layout.scale(w,h);rect(0,0,w,h,0x070D09,1);
        GL11.glTranslated((w-layout.width*s)/2,(h-layout.height*s)/2,0);GL11.glScaled(s,s,1);
    }
    public void menu(double age,double px,double py,double visibility,double[] highlights,boolean motion,boolean particles){
        prepareMenu();int s=layout.sidebar,w=layout.width,h=layout.height;
        // UV crop stays inside the right scenery; navigation/hitboxes never move with the camera.
        double inset=motion?.006:0,dx=motion?Math.max(-1,Math.min(1,px))*.003:0,dy=motion?Math.max(-1,Math.min(1,py))*.003:0;
        scene.draw(s,0,w-s,h,inset+dx,inset+dy,1-inset+dx,1-inset+dy,1,0xFFFFFF);
        if(motion&&particles)for(int i=0;i<20;i++){
            double x=s+((i*.61803398875)%1)*(w-s),y=((i*.381966+age*.006)%1)*h;
            rect(x,y,1+(i%2)*.4,1+(i%2)*.4,0xC5D7B7,.10+(i%3)*.035);
        }
        panel.draw(0,0,s,h,1);
        for(int i=0;i<4;i++){
            String k="button."+i+".";double x=layout.n(k+"x"),y=layout.n(k+"y"),bw=layout.n(k+"width"),bh=layout.n(k+"height");
            buttons[i].draw(x,y,bw,bh,visibility);if(highlights[i]>.001)hover[i].draw(x,y,bw,bh,visibility*highlights[i]);
        }
    }
    public void startup(LoadState state,double elapsed,double alpha,boolean animate){
        prepareBoot();boot.draw(0,0,layout.width,layout.height,alpha);
        int text=0xE3EBD7,muted=0xB6C4B0;double titleSize=layout.profile.equals("compact")?18:Math.min(27,layout.width*.0165);
        double bodySize=layout.profile.equals("compact")?9:10;
        double titleWidth=layout.n("percent.x")-layout.n("title.x")-18;
        serif.draw(state.title,layout.n("title.x"),layout.n("title.y"),titleSize,titleWidth,text,alpha);
        body.draw(state.detail,layout.n("detail.x"),layout.n("detail.y"),bodySize,titleWidth,muted,alpha);
        serif.drawFit(state.percent(),layout.n("percent.x"),layout.n("percent.y"),layout.profile.equals("compact")?25:33,layout.n("percent.width")+2,0xDDECD0,alpha);
        body.draw(state.total==0?"SEM TOTAL":"DA ETAPA",layout.n("percent.x"),layout.n("percent.y")+(layout.profile.equals("compact")?39:47),8,layout.n("percent.width"),muted,alpha);
        double x=layout.n("progress.x"),y=layout.n("progress.y"),w=layout.n("progress.width");
        if(state.total>0)rect(x,y,w*state.fraction(),2,0xBBD79C,alpha);
        else {double pos=animate?(Math.sin(elapsed*1.2)*.5+.5):.5;rect(x+(w-w*.18)*pos,y,w*.18,2,0xBBD79C,alpha*.65);}
        body.draw(state.branch,layout.n("branch.x"),layout.n("branch.y"),bodySize-1,layout.n("branch.width"),0xC0CCAF,alpha);
        String elapsedText=String.format(java.util.Locale.ROOT,"%02d:%02d",(int)elapsed/60,(int)elapsed%60);
        body.draw(elapsedText,layout.n("time.x"),layout.n("time.y"),9,layout.n("time.width"),muted,alpha);
        if(state.total>0)body.drawFit(state.step+" / "+state.total,layout.n("steps.x")-24,layout.n("steps.y"),9,layout.n("steps.width")+24,muted,alpha);
    }
    public void releaseBoot(){
        if(boot!=null){boot.close();boot=null;}if(body!=null){body.close();body=null;}if(serif!=null){serif.close();serif=null;}
    }
    @Override public void close(){
        if(panel!=null){panel.close();panel=null;}if(scene!=null){scene.close();scene=null;}if(boot!=null){boot.close();boot=null;}
        if(body!=null){body.close();body=null;}if(serif!=null){serif.close();serif=null;}
        for(int i=0;i<4;i++){if(buttons[i]!=null){buttons[i].close();buttons[i]=null;}if(hover[i]!=null){hover[i].close();hover[i]=null;}}
    }
}
