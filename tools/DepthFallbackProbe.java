package br.com.redeakashic.cinematic.render;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
public final class DepthFallbackProbe {
    private static byte[] capture(){ByteBuffer b=BufferUtils.createByteBuffer(854*480*4);GL11.glReadPixels(0,0,854,480,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,b);byte[] data=new byte[b.remaining()];b.get(data);return data;}
    @SuppressWarnings("try") public static void main(String[] args)throws Exception{
        Pbuffer p=new Pbuffer(854,480,new PixelFormat(),null,null);p.makeCurrent();NativeVisuals v=new NativeVisuals("responsive");
        try{v.heroAnimation=false;
            try(RawScope scope=new RawScope(854,480)){v.transform(854,480);v.menu(0,1,1,1,new double[4],true,false);}byte[] fallback=capture();
            v.depthEnabled=false;try(RawScope scope=new RawScope(854,480)){v.transform(854,480);v.menu(20,0,0,1,new double[4],true,false);}byte[] regular=capture();
            if(!Arrays.equals(fallback,regular))throw new AssertionError("Fallback differs from approved flat path");
            if(BackdropRenderer.fallbacks!=1||BackdropRenderer.depthDraws!=0)throw new AssertionError("Missing layer did not cause one safe fallback");
            try(RawScope scope=new RawScope(854,480)){v.close();}if(NativeTexture.live!=0)throw new AssertionError("Partial load leaked texture handles");
            if(GL11.glGetError()!=0)throw new AssertionError("GL error");System.out.println("RESULT missing-layer-fallback pixel_equal=true textures_released=true errors=0");
        }finally{p.destroy();}
    }
}
