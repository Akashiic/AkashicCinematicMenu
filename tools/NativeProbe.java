package br.com.redeakashic.cinematic.render;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import javax.imageio.ImageIO;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import br.com.redeakashic.cinematic.core.LoadState;
/** Isolated real OpenGL probe: does not start Minecraft or connect to any server. */
public final class NativeProbe {
    private static int checks;
    static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);}
    static String state(){
        return GL11.glGetInteger(GL11.GL_MATRIX_MODE)+":"+GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)+":"+
        GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)+":"+GL11.glIsEnabled(GL11.GL_BLEND)+":"+
        GL11.glGetInteger(GL14.GL_BLEND_EQUATION)+":"+GL11.glGetInteger(GL11.GL_PROJECTION_STACK_DEPTH)+":"+
        GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH)+":"+GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
    }
    static void capture(File f,int w,int h)throws Exception{
        ByteBuffer b=BufferUtils.createByteBuffer(w*h*4);GL11.glReadPixels(0,0,w,h,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,b);
        BufferedImage im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        for(int y=0;y<h;y++)for(int x=0;x<w;x++){int k=((h-y-1)*w+x)*4;im.setRGB(x,y,((b.get(k)&255)<<16)|((b.get(k+1)&255)<<8)|(b.get(k+2)&255));}
        ImageIO.write(im,"png",f);
    }
    static double percentile(List<Double> d,double q){Collections.sort(d);return d.get(Math.min(d.size()-1,(int)Math.floor(q*d.size())));}
    @SuppressWarnings("try")
    public static void main(String[] args)throws Exception{
        File out=new File(args[0]);out.mkdirs();int w=1366,h=768;
        Display.setDisplayMode(new DisplayMode(w,h));Display.setTitle("Akashic Cinematic N01 - teste isolado; fecha automaticamente");Display.create(new PixelFormat());Display.setVSyncEnabled(false);
        System.out.println("GL_VENDOR="+GL11.glGetString(GL11.GL_VENDOR));System.out.println("GL_RENDERER="+GL11.glGetString(GL11.GL_RENDERER));System.out.println("GL_VERSION="+GL11.glGetString(GL11.GL_VERSION));
        List<Double> menuTimes=new ArrayList<Double>(),bootTimes=new ArrayList<Double>();
        try{
            NativeVisuals v=new NativeVisuals("hd");
            GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glDepthMask(true);GL11.glDisable(GL11.GL_BLEND);GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);GL13.glActiveTexture(GL13.GL_TEXTURE1);GL11.glMatrixMode(GL11.GL_PROJECTION);
            String before=state();
            for(int i=0;i<240;i++){
                long t=System.nanoTime();
                try(RawScope scope=new RawScope(w,h)){
                    v.transform(w,h);
                    if(i<120)v.menu(i/60.0,0,0,1,new double[4],false,false);
                    else v.startup(new LoadState("Carregando recursos","TESTE ISOLADO / renderizador nativo","FORGE / dados de ensaio; sem Minecraft",53,100),(i-120)/60.0,1,false);
                }
                GL11.glFinish();double dt=(System.nanoTime()-t)/1.0e6;
                if(i>5&&i<120)menuTimes.add(dt);if(i>125)bootTimes.add(dt);
                check(before.equals(state()),"GL state restoration frame "+i);check(GL11.glGetError()==GL11.GL_NO_ERROR,"GL error frame "+i);
                if(i==30)capture(new File(out,"native-menu-hd.png"),w,h);
                if(i==160)capture(new File(out,"native-startup-hd.png"),w,h);
                Display.update();Display.sync(60);
            }
            try(RawScope scope=new RawScope(w,h)){v.close();check(NativeTexture.live==0,"All texture handles released");}
            for(String p:new String[]{"hd","compact","classic","wide"}){
                NativeLayout l=new NativeLayout(p);check(l.n("count")==4,p+" four actions");
                int pw=l.width,ph=l.height;Display.setDisplayMode(new DisplayMode(pw,ph));
                try(RawScope scope=new RawScope(pw,ph)){
                    NativeVisuals nv=new NativeVisuals(p);nv.transform(pw,ph);nv.menu(0,0,0,1,new double[]{0,1,0,0},false,false);
                    capture(new File(out,"menu-"+p+".png"),pw,ph);
                    nv.startup(new LoadState("Etapa concluída","TESTE ISOLADO / rótulo de conclusão","Somente esta etapa chegou ao total; não é prontidão global.",100,100),45,1,false);
                    capture(new File(out,"startup-"+p+".png"),pw,ph);nv.close();check(NativeTexture.live==0,"release "+p);
                }check(GL11.glGetError()==GL11.GL_NO_ERROR,"profile GL "+p);

                for(int i=0;i<4;i++){String k="button."+i+".";check(l.hit(l.n(k+"x")+l.n(k+"width")/2.0,l.n(k+"y")+l.n(k+"height")/2.0)==i,p+" hit "+i);}
            }
            check(Double.isNaN(LoadState.unknown().fraction()),"Unknown total");check(new LoadState("x","y","z",99,5).step==5,"Stage clamp");
            System.out.println("RESULT checks="+checks+" failures=0");
            System.out.println("MENU_GPU_ROUNDTRIP_MS median="+percentile(menuTimes,.5)+" p95="+percentile(menuTimes,.95));
            System.out.println("BOOT_GPU_ROUNDTRIP_MS median="+percentile(bootTimes,.5)+" p95="+percentile(bootTimes,.95));
        }finally{Display.destroy();}
    }
}
