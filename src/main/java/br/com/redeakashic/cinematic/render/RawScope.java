package br.com.redeakashic.cinematic.render;
import org.lwjgl.opengl.*;
/** GL scope eligible for Angelica FFP redirection; do not exclude this package from transformers. */
public final class RawScope implements AutoCloseable {
    private final int mode,active,program,unpack;
    private boolean attrs,clients,projection,model,texture;
    public RawScope(int w,int h) {
        mode=GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        active=GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        program=GLContext.getCapabilities().OpenGL20?GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM):0;
        unpack=GLContext.getCapabilities().OpenGL21?GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING):0;
        try {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);attrs=true;
            GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT | GL11.GL_CLIENT_VERTEX_ARRAY_BIT);clients=true;
            if(GLContext.getCapabilities().OpenGL20)GL20.glUseProgram(0);
            if(GLContext.getCapabilities().OpenGL21)GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER,0);
            int units=Math.max(1,Math.min(32,GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS)));
            for(int i=units-1;i>=0;i--){GL13.glActiveTexture(GL13.GL_TEXTURE0+i);GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_TEXTURE_1D);GL11.glDisable(GL12.GL_TEXTURE_3D);GL11.glDisable(GL13.GL_TEXTURE_CUBE_MAP);}
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glDisable(GL11.GL_TEXTURE_GEN_S);GL11.glDisable(GL11.GL_TEXTURE_GEN_T);GL11.glDisable(GL11.GL_TEXTURE_GEN_R);GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
            GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glPushMatrix();texture=true;GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPushMatrix();projection=true;GL11.glLoadIdentity();
            GL11.glOrtho(0,w,h,0,-1,1);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPushMatrix();model=true;GL11.glLoadIdentity();
            GL11.glViewport(0,0,w,h);GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_ALPHA_TEST);GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_CULL_FACE);GL11.glDisable(GL11.GL_SCISSOR_TEST);GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL14.glBlendEquation(GL14.GL_FUNC_ADD);GL11.glColorMask(true,true,true,true);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL11.GL_MODULATE);
        }catch(RuntimeException|LinkageError ex){try{close();}catch(RuntimeException|LinkageError cleanup){ex.addSuppressed(cleanup);}throw ex;}
    }
    @Override public void close(){
        if(model){GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPopMatrix();model=false;}
        if(projection){GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPopMatrix();projection=false;}
        if(texture){GL13.glActiveTexture(GL13.GL_TEXTURE0);GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glPopMatrix();texture=false;}
        if(clients){GL11.glPopClientAttrib();clients=false;}
        if(attrs){GL11.glPopAttrib();attrs=false;}
        if(GLContext.getCapabilities().OpenGL20)GL20.glUseProgram(program);
        if(GLContext.getCapabilities().OpenGL21)GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER,unpack);
        GL13.glActiveTexture(active);GL11.glMatrixMode(mode);
    }
}
