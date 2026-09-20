package br.com.redeakashic.cinematic.render;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
/** Per-context owner. No global texture IDs shared across splash/main threads. */
final class NativeTexture implements AutoCloseable {
    private int id;
    static int live;
    final int width,height;
    NativeTexture(String path){
        try(InputStream stream=NativeLayout.open(path)){
            BufferedImage image=ImageIO.read(stream);
            if(image==null)throw new IOException("Unreadable PNG/JPEG "+path);
            width=image.getWidth();height=image.getHeight();
            int max=GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
            if(width>Math.min(4096,max)||height>Math.min(4096,max)||width<1||height<1)throw new IOException("Texture dimensions unsupported");
            ByteBuffer buf=BufferUtils.createByteBuffer(width*height*4);
            for(int y=0;y<height;y++)for(int x=0;x<width;x++){
                int c=image.getRGB(x,y);buf.put((byte)(c>>16)).put((byte)(c>>8)).put((byte)c).put((byte)(c>>24));
            }buf.flip();image.flush();id=GL11.glGenTextures();if(id==0)throw new IOException("Texture allocation returned zero");live++;GL11.glBindTexture(GL11.GL_TEXTURE_2D,id);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,GL12.GL_CLAMP_TO_EDGE);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH,0);GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS,0);GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS,0);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL11.GL_RGBA8,width,height,0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,buf);
        }catch(IOException|RuntimeException|LinkageError ex){close();throw new IllegalStateException("Texture init failed: "+path,ex);}
    }
    void draw(double x,double y,double w,double h,double alpha){draw(x,y,w,h,0,0,1,1,alpha,0xFFFFFF);}
    void draw(double x,double y,double w,double h,double u0,double v0,double u1,double v1,double alpha,int rgb){
        if(id==0)throw new IllegalStateException("Texture disposed");
        GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glBindTexture(GL11.GL_TEXTURE_2D,id);
        GL11.glColor4d(((rgb>>16)&255)/255.0,((rgb>>8)&255)/255.0,(rgb&255)/255.0,Math.max(0,Math.min(1,alpha)));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(u0,v0);GL11.glVertex2d(x,y);GL11.glTexCoord2d(u1,v0);GL11.glVertex2d(x+w,y);
        GL11.glTexCoord2d(u1,v1);GL11.glVertex2d(x+w,y+h);GL11.glTexCoord2d(u0,v1);GL11.glVertex2d(x,y+h);GL11.glEnd();
    }
    @Override public void close(){if(id!=0){GL11.glDeleteTextures(id);id=0;live--;}}
}
