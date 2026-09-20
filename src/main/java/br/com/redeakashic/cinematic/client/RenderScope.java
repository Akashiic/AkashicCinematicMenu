package br.com.redeakashic.cinematic.client;

import org.lwjgl.opengl.GL11;

/** Compatibility-profile scope. No FBO, shader program or viewport is changed by alpha. */
final class RenderScope implements AutoCloseable {
    private final int previousMode;
    private boolean attributes, clientAttributes, matrix;
    RenderScope() {
        previousMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        try {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS); attributes = true;
            GL11.glPushClientAttrib(GL11.GL_CLIENT_VERTEX_ARRAY_BIT); clientAttributes = true;
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix(); matrix = true;
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } catch (RuntimeException ex) { close(); throw ex; }
        catch (LinkageError ex) { close(); throw ex; }
    }
    @Override
    public void close() {
        if (matrix) { GL11.glMatrixMode(GL11.GL_MODELVIEW); GL11.glPopMatrix(); matrix = false; }
        if (clientAttributes) { GL11.glPopClientAttrib(); clientAttributes = false; }
        if (attributes) { GL11.glPopAttrib(); attributes = false; }
        GL11.glMatrixMode(previousMode);
    }
}
