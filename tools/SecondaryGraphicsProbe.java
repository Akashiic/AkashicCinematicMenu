package br.com.redeakashic.cinematic.render;
import java.io.File;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;
import javax.imageio.ImageIO;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
/** Real OpenGL pbuffer probe for the shared secondary-screen compositor. */
public final class SecondaryGraphicsProbe {
    private static int checks;
    private static final ByteBuffer buffer=BufferUtils.createByteBuffer(2560*1440*4);
    private static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    private static byte[] pixels(int w,int h){
        buffer.clear();buffer.limit(w*h*4);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT,1);
        GL11.glReadPixels(0,0,w,h,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,buffer);
        byte[] data=new byte[w*h*4];buffer.get(data);return data;
    }
    private static void save(File dir,String name,int w,int h)throws Exception{
        byte[] a=pixels(w,h);BufferedImage im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        for(int y=0;y<h;y++)for(int x=0;x<w;x++){int i=((h-y-1)*w+x)*4;im.setRGB(x,y,((a[i]&255)<<16)|((a[i+1]&255)<<8)|(a[i+2]&255));}
        ImageIO.write(im,"png",new File(dir,name+".png"));
    }
    private static String state(){
        return GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)+":"+GL11.glGetInteger(GL11.GL_MATRIX_MODE)+":"+
               GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)+":"+GL11.glIsEnabled(GL11.GL_DEPTH_TEST)+":"+
               GL11.glIsEnabled(GL11.GL_BLEND)+":"+GL11.glGetInteger(GL11.GL_PROJECTION_STACK_DEPTH)+":"+
               GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
    }
    @SuppressWarnings("try")
    private static void draw(SecondaryFrameRenderer renderer,int w,int h,String section){
        try(RawScope scope=new RawScope(w,h)){renderer.draw(w,h,section);}
    }
    public static void main(String[] args)throws Exception{
        File out=new File(args[0]);out.mkdirs();
        check((Pbuffer.getCapabilities()&Pbuffer.PBUFFER_SUPPORTED)!=0,"pbuffer");
        Pbuffer p=new Pbuffer(2560,1440,new PixelFormat().withAlphaBits(8),null,null);p.makeCurrent();
        System.out.println("VENDOR="+GL11.glGetString(GL11.GL_VENDOR));
        System.out.println("RENDERER="+GL11.glGetString(GL11.GL_RENDERER));
        SecondaryFrameRenderer renderer=null;
        try{
            renderer=new SecondaryFrameRenderer();
            GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_BLEND);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);GL11.glMatrixMode(GL11.GL_PROJECTION);
            String initial=state();
            int[][] sizes={{854,480},{1024,768},{1366,768},{1920,1080},{2560,1440}};
            String[] labels={"CONFIGURAÇÕES","MUNDOS","IDIOMA","TEXTURAS","CONTROLES"};
            for(int i=0;i<sizes.length;i++){
                int w=sizes[i][0],h=sizes[i][1];
                draw(renderer,w,h,labels[i]);GL11.glFinish();
                check(state().equals(initial),"state "+w);
                check(GL11.glGetError()==0,"GL "+w);
                save(out,"secondary-"+w+"x"+h,w,h);
            }
            draw(renderer,1366,768,"CONFIGURAÇÕES");byte[] first=pixels(1366,768);draw(renderer,1366,768,"CONFIGURAÇÕES");
            check(Arrays.equals(first,pixels(1366,768)),"static frame deterministic");
            try(RawScope scope=new RawScope(1366,768)){renderer.close();renderer=null;check(NativeTexture.live==0,"textures released");}
            check(GL11.glGetError()==0,"final GL");
            System.out.println("RESULT secondary_graphics_checks="+checks+" failures=0");
        }finally{
            if(renderer!=null)try(RawScope scope=new RawScope(1366,768)){renderer.close();}
            p.destroy();
        }
    }
}
