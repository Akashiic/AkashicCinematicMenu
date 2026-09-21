package br.com.redeakashic.cinematic.render;
import java.io.File;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;
import javax.imageio.ImageIO;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import br.com.redeakashic.cinematic.core.LoadState;
/** Real driver, private pbuffer: no Minecraft, no server login, no focus stealing. */
public final class CeremonialGraphicsProbe {
    private static int checks;private static final ByteBuffer scratch=BufferUtils.createByteBuffer(2560*1440*4);
    private static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    private static byte[] pixels(int w,int h){ByteBuffer b=scratch;b.clear();b.limit(w*h*4);GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT,1);GL11.glReadPixels(0,0,w,h,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,b);byte[] data=new byte[b.remaining()];b.get(data);return data;}
    private static void save(File dir,String name,int w,int h)throws Exception{byte[] a=pixels(w,h);BufferedImage im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        for(int y=0;y<h;y++)for(int x=0;x<w;x++){int i=((h-y-1)*w+x)*4;im.setRGB(x,y,((a[i]&255)<<16)|((a[i+1]&255)<<8)|(a[i+2]&255));}ImageIO.write(im,"png",new File(dir,name+".png"));}
    private static String state(){return GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)+":"+GL11.glGetInteger(GL11.GL_MATRIX_MODE)+":"+GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)+":"+GL11.glIsEnabled(GL11.GL_DEPTH_TEST)+":"+GL11.glIsEnabled(GL11.GL_BLEND)+":"+GL11.glGetInteger(GL11.GL_PROJECTION_STACK_DEPTH)+":"+GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);}
    @SuppressWarnings("try") private static void menu(NativeVisuals v,int w,int h,double age,double x,double y,double hover,double press,boolean motion){try(RawScope scope=new RawScope(w,h)){
        v.transform(w,h);v.playPressAge=press;v.menu(age,x,y,1,new double[]{hover,0,0,0},motion,false);
    }}
    @SuppressWarnings("try") public static void main(String[] args)throws Exception{
        File out=new File(args[0]);out.mkdirs();check((Pbuffer.getCapabilities()&Pbuffer.PBUFFER_SUPPORTED)!=0,"pbuffer available");Pbuffer p=new Pbuffer(2560,1440,new PixelFormat().withAlphaBits(8),null,null);p.makeCurrent();
        System.out.println("VENDOR="+GL11.glGetString(GL11.GL_VENDOR));System.out.println("RENDERER="+GL11.glGetString(GL11.GL_RENDERER));System.out.println("GL="+GL11.glGetString(GL11.GL_VERSION));
        NativeVisuals v=null;
        try{v=new NativeVisuals("responsive");GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_BLEND);GL13.glActiveTexture(GL13.GL_TEXTURE1);GL11.glMatrixMode(GL11.GL_PROJECTION);String initial=state();
            int[][] sizes={{854,480},{1024,768},{1366,768},{1920,1001},{2560,1440}};
            for(int[] s:sizes){int w=s[0],h=s[1];menu(v,w,h,2.8,0,0,0,-1,true);GL11.glFinish();check(state().equals(initial),"GL state "+w);check(GL11.glGetError()==0,"GL errors "+w);save(out,"menu-"+w+"x"+h,w,h);
                for(int i=0;i<4;i++){String k="button."+i+".";check(v.layout.hit(v.layout.n(k+"x")+2,v.layout.n(k+"y")+2)==i,"live hit "+w+"/"+i);}}
            menu(v,1366,768,.66,1,-1,1,.11,true);save(out,"hero-hover-press",1366,768);
            menu(v,1366,768,3,-1,-1,0,-1,true);save(out,"parallax-upper-left",1366,768);
            menu(v,1366,768,3,1,1,0,-1,true);save(out,"parallax-lower-right",1366,768);
            menu(v,854,480,0,0,0,.7,.11,false);byte[] still=pixels(854,480);menu(v,854,480,1500,1,-1,.7,.19,false);check(Arrays.equals(still,pixels(854,480)),"reduced motion pixel stable");save(out,"reduced-motion",854,480);
            try(RawScope scope=new RawScope(1366,768)){v.transform(1366,768);v.startup(new LoadState("Preparando o ambiente","TESTE ISOLADO / desenho nativo","Dados de ensaio, não progresso do Forge",3,8),22,1,false);}save(out,"startup-preserved",1366,768);
            List<Double> times=new ArrayList<Double>();
            for(int i=0;i<120;i++){long start=System.nanoTime();menu(v,1366,768,i/60.0,Math.sin(i*.08),Math.cos(i*.07),.5,-1,true);GL11.glFinish();if(i>5)times.add((System.nanoTime()-start)/1e6);check(state().equals(initial),"frame-state "+i);check(GL11.glGetError()==0,"frame-error "+i);}
            Collections.sort(times);System.out.println("WARM_DRAW_MS median="+times.get(times.size()/2)+" p95="+times.get((int)(times.size()*.95)));
            List<Double> flat=new ArrayList<Double>(),depth=new ArrayList<Double>();
            for(int i=0;i<80;i++){v.depthEnabled=(i%2==0);long start=System.nanoTime();menu(v,1366,768,i*.04,.4,.4,.3,-1,true);GL11.glFinish();if(i>5)(v.depthEnabled?depth:flat).add((System.nanoTime()-start)/1e6);}
            Collections.sort(flat);Collections.sort(depth);System.out.println("PAIRED_MEDIAN_MS static="+flat.get(flat.size()/2)+" layered="+depth.get(depth.size()/2));v.depthEnabled=true;
            for(int i=0;i<42;i++){double t=i/42.0;menu(v,960,540,t*6.8,Math.sin(t*2*Math.PI),Math.cos(t*2*Math.PI)*.7,.4,-1,true);save(out,String.format(java.util.Locale.ROOT,"motion-%03d",i),960,540);}
            check(BackdropRenderer.depthDraws>100,"actual depth path used");check(BackdropRenderer.fallbacks==0,"no hidden asset fallback");
            try(RawScope scope=new RawScope(1366,768)){v.close();v=null;check(NativeTexture.live==0,"all owned textures released");}
            check(GL11.glGetError()==0,"final GL state");System.out.println("RESULT checks="+checks+" failures=0");
        }finally{if(v!=null){try(RawScope scope=new RawScope(1366,768)){v.close();}}p.destroy();}
    }
}
