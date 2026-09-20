package br.com.redeakashic.cinematic.earlygl;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
/** Raw programmable GL only, before Angelica's FFP emulation exists. Excluded ONLY for this package. */
public final class FirstFrame {
    public static volatile long startedNanos,presentedEpochMillis;
    public static volatile boolean attempted,presented,failed;
    private FirstFrame(){}
    private static int shader(int type,String source){
        int s=GL20.glCreateShader(type);if(s==0)throw new IllegalStateException("No shader handle");
        GL20.glShaderSource(s,source);GL20.glCompileShader(s);
        if(GL20.glGetShaderi(s,GL20.GL_COMPILE_STATUS)==0){String why=GL20.glGetShaderInfoLog(s,512);GL20.glDeleteShader(s);throw new IllegalStateException(why);}
        return s;
    }
    public static void present(){
        if(attempted||Boolean.getBoolean("akashic.cinematic.disabled")||Boolean.getBoolean("akashic.cinematic.disableSplash"))return;
        attempted=true;startedNanos=System.nanoTime();
        if(!Display.isCreated()||!GLContext.getCapabilities().OpenGL30){System.out.println("[AkashicCinematic] Cold frame deferred to compatible graphics-ready renderer");return;}
        int w=Display.getWidth(),h=Display.getHeight();if(w<1||h<1)return;
        System.out.println("[AkashicCinematic] FIRST_CONTEXT_READY epoch="+System.currentTimeMillis());
        try{draw(w,h);Display.update();presented=true;presentedEpochMillis=System.currentTimeMillis();
            System.out.println("[AkashicCinematic] FIRST_CONTEXT_FRAME epoch="+presentedEpochMillis+" costMs="+((System.nanoTime()-startedNanos)/1_000_000L));
        }catch(Exception|LinkageError ex){failed=true;System.err.println("[AkashicCinematic] Cold frame unavailable; later renderer retained: "+ex);}
    }
    private static void draw(int w,int h)throws Exception{
        String profile=(double)w/h<1.52?"classic":(double)w/h>2.08?"wide":w<1100||h<600?"compact":"hd";
        BufferedImage im;
        try(InputStream in=FirstFrame.class.getResourceAsStream("/assets/akashiccinematic/native/"+profile+"/early.png")){
            if(in==null)throw new IllegalStateException("First-frame asset missing");im=ImageIO.read(in);
        }
        if(im==null||im.getWidth()>GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE)||im.getHeight()>GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE))throw new IllegalStateException("First-frame texture unsupported");
        IntBuffer viewport=BufferUtils.createIntBuffer(4);GL11.glGetInteger(GL11.GL_VIEWPORT,viewport);
        ByteBuffer mask=BufferUtils.createByteBuffer(4);GL11.glGetBoolean(GL11.GL_COLOR_WRITEMASK,mask);
        int program=GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM),vao=GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING),vbo=GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        int active=GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);GL13.glActiveTexture(GL13.GL_TEXTURE0);int tex=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int pbo=GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING);
        int[] params={GL11.GL_UNPACK_ALIGNMENT,GL11.GL_UNPACK_ROW_LENGTH,GL11.GL_UNPACK_SKIP_PIXELS,GL11.GL_UNPACK_SKIP_ROWS};int[] stored=new int[params.length];for(int i=0;i<params.length;i++)stored[i]=GL11.glGetInteger(params[i]);
        int[] caps={GL11.GL_DEPTH_TEST,GL11.GL_BLEND,GL11.GL_SCISSOR_TEST,GL11.GL_STENCIL_TEST,GL11.GL_CULL_FACE,GL30.GL_RASTERIZER_DISCARD,GL30.GL_FRAMEBUFFER_SRGB};boolean[] enabled=new boolean[caps.length];for(int i=0;i<caps.length;i++)enabled[i]=GL11.glIsEnabled(caps[i]);
        int vertex=0,fragment=0,ownProgram=0,ownVao=0,ownVbo=0,ownTexture=0;
        try{
            vertex=shader(GL20.GL_VERTEX_SHADER,"#version 130\nin vec2 pos;in vec2 uv;out vec2 texUV;void main(){texUV=uv;gl_Position=vec4(pos,0.0,1.0);}");
            fragment=shader(GL20.GL_FRAGMENT_SHADER,"#version 130\nuniform sampler2D image;in vec2 texUV;out vec4 color;void main(){color=vec4(texture(image,texUV).rgb,1.0);}");
            ownProgram=GL20.glCreateProgram();GL20.glAttachShader(ownProgram,vertex);GL20.glAttachShader(ownProgram,fragment);GL20.glBindAttribLocation(ownProgram,0,"pos");GL20.glBindAttribLocation(ownProgram,1,"uv");GL20.glLinkProgram(ownProgram);
            if(GL20.glGetProgrami(ownProgram,GL20.GL_LINK_STATUS)==0)throw new IllegalStateException(GL20.glGetProgramInfoLog(ownProgram,512));
            ownVao=GL30.glGenVertexArrays();GL30.glBindVertexArray(ownVao);ownVbo=GL15.glGenBuffers();GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,ownVbo);
            FloatBuffer quad=BufferUtils.createFloatBuffer(16);quad.put(new float[]{-1,1,0,0,-1,-1,0,1,1,1,1,0,1,-1,1,1}).flip();
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER,quad,GL15.GL_STATIC_DRAW);GL20.glEnableVertexAttribArray(0);GL20.glEnableVertexAttribArray(1);GL20.glVertexAttribPointer(0,2,GL11.GL_FLOAT,false,16,0L);GL20.glVertexAttribPointer(1,2,GL11.GL_FLOAT,false,16,8L);
            ByteBuffer pixels=BufferUtils.createByteBuffer(im.getWidth()*im.getHeight()*4);
            for(int y=0;y<im.getHeight();y++)for(int x=0;x<im.getWidth();x++){int value=im.getRGB(x,y);pixels.put((byte)(value>>16)).put((byte)(value>>8)).put((byte)value).put((byte)255);}pixels.flip();
            ownTexture=GL11.glGenTextures();GL11.glBindTexture(GL11.GL_TEXTURE_2D,ownTexture);GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER,0);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);for(int i=1;i<params.length;i++)GL11.glPixelStorei(params[i],0);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR);GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,GL12.GL_CLAMP_TO_EDGE);GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL11.GL_RGBA8,im.getWidth(),im.getHeight(),0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,pixels);
            for(int cap:caps)GL11.glDisable(cap);GL11.glViewport(0,0,w,h);GL11.glColorMask(true,true,true,true);
            GL20.glUseProgram(ownProgram);GL20.glUniform1i(GL20.glGetUniformLocation(ownProgram,"image"),0);GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP,0,4);GL11.glFlush();
        }finally{
            GL20.glUseProgram(program);GL30.glBindVertexArray(vao);GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vbo);
            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER,pbo);for(int i=0;i<params.length;i++)GL11.glPixelStorei(params[i],stored[i]);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,tex);GL13.glActiveTexture(active);
            for(int i=0;i<caps.length;i++)if(enabled[i])GL11.glEnable(caps[i]);else GL11.glDisable(caps[i]);
            GL11.glViewport(viewport.get(0),viewport.get(1),viewport.get(2),viewport.get(3));GL11.glColorMask(mask.get(0)!=0,mask.get(1)!=0,mask.get(2)!=0,mask.get(3)!=0);
            if(ownTexture!=0)GL11.glDeleteTextures(ownTexture);if(ownVbo!=0)GL15.glDeleteBuffers(ownVbo);if(ownVao!=0)GL30.glDeleteVertexArrays(ownVao);
            if(ownProgram!=0)GL20.glDeleteProgram(ownProgram);if(vertex!=0)GL20.glDeleteShader(vertex);if(fragment!=0)GL20.glDeleteShader(fragment);im.flush();
        }
    }
}
