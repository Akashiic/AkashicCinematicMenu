package br.com.redeakashic.cinematic.render;
import java.io.File;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
public final class SecondaryFrameProbe {
    private static int checks;
    private static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    private static String state(){return GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)+":"+GL11.glGetInteger(GL11.GL_MATRIX_MODE)+":"+GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)+":"+GL11.glIsEnabled(GL11.GL_DEPTH_TEST)+":"+GL11.glIsEnabled(GL11.GL_BLEND);}
    private static void save(File out,String name,int w,int h)throws Exception{
        ByteBuffer b=BufferUtils.createByteBuffer(w*h*4);GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT,1);GL11.glReadPixels(0,0,w,h,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,b);
        BufferedImage im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        for(int y=0;y<h;y++)for(int x=0;x<w;x++){int i=((h-y-1)*w+x)*4;im.setRGB(x,y,((b.get(i)&255)<<16)|((b.get(i+1)&255)<<8)|(b.get(i+2)&255));}
        ImageIO.write(im,"png",new File(out,name+".png"));
    }
    @SuppressWarnings("try") public static void main(String[] args)throws Exception{
        File out=new File(args[0]);out.mkdirs();Pbuffer p=new Pbuffer(2560,1440,new PixelFormat().withAlphaBits(8),null,null);p.makeCurrent();
        SecondaryFrameRenderer renderer=new SecondaryFrameRenderer();
        try{
            GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_BLEND);GL13.glActiveTexture(GL13.GL_TEXTURE1);GL11.glMatrixMode(GL11.GL_PROJECTION);String before=state();
            int[][] sizes={{854,480},{1366,768},{1920,1001},{2560,1440}};
            String[] labels={"CONFIGURAÇÕES","MUNDOS","IDIOMA","TEXTURAS"};
            for(int i=0;i<sizes.length;i++){
                int w=sizes[i][0],h=sizes[i][1];
                try(RawScope scope=new RawScope(w,h)){renderer.draw(w,h,labels[i]);}
                GL11.glFinish();check(state().equals(before),"state "+w);check(GL11.glGetError()==0,"GL "+w);save(out,"secondary-"+w+"x"+h,w,h);
            }
            try(RawScope scope=new RawScope(854,480)){renderer.close();}
            check(NativeTexture.live==0,"textures released");check(GL11.glGetError()==0,"final GL");
            System.out.println("RESULT secondary_frame_checks="+checks+" failures=0");
        }finally{p.destroy();}
    }
}
