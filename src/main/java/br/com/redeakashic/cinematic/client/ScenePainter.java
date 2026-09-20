package br.com.redeakashic.cinematic.client;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.FontRenderer;

/** Original procedural TECHNICAL art, not a replacement for the approved angel artwork. */
final class ScenePainter {
    private ScenePainter() { }
    private static void color(int rgb, double alpha) {
        GL11.glColor4d(((rgb >> 16) & 255) / 255.0, ((rgb >> 8) & 255) / 255.0,
                (rgb & 255) / 255.0, alpha);
    }
    static void rect(double x, double y, double w, double h, int rgb, double alpha) {
        if (alpha <= 0 || w <= 0 || h <= 0) { return; }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        color(rgb, alpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y); GL11.glVertex2d(x + w, y);
        GL11.glVertex2d(x + w, y + h); GL11.glVertex2d(x, y + h);
        GL11.glEnd();
    }
    static void ring(double x, double y, double radius, int rgb, double alpha, double phase) {
        GL11.glDisable(GL11.GL_TEXTURE_2D); color(rgb, alpha);
        GL11.glLineWidth(1.0f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 128; i++) {
            double a = phase + i * Math.PI * 2 / 128;
            GL11.glVertex2d(x + Math.cos(a) * radius, y + Math.sin(a) * radius);
        }
        GL11.glEnd();
    }
    static void text(FontRenderer font, String text, double x, double y, double scale, int rgb, double alpha) {
        // Vanilla FontRenderer treats very small alpha as opaque; skip such samples.
        int a = (int) Math.round(Math.max(0, Math.min(1, alpha)) * 255);
        if (a < 4) { return; }
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, 0); GL11.glScaled(scale, scale, 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            font.func_78276_b(text, 0, 0, (a << 24) | (rgb & 0xFFFFFF));
        } finally { GL11.glPopMatrix(); }
    }
    static void scene(double time, double px, double py, boolean particles) {
        rect(0, 0, 960, 540, 0x08080D, 1);
        for (int i = 0; i < 26; i++) {
            rect(395, i * 21, 565, 21, 0x38161F, 0.07 + i * .007);
        }
        for (int layer = 0; layer < 3; layer++) {
            for (int i = 0; i < 10; i++) {
                double x = 402 + i * 59 + Math.sin(i * 13.1 + layer) * 18 + px * (layer + 1) * 4;
                double h = 65 + ((i * 73 + layer * 53) % 227);
                double bottom = 438 + layer * 39;
                double w = 22 + layer * 10;
                rect(x, bottom - h + py * 3, w, h, layer == 0 ? 0x211923 : 0x0B0C12, 1);
                rect(x + w - 2, bottom - h + py * 3, 1, h * .65, 0xA33A4C, .3);
            }
        }
        double cx = 673 + px * 10, cy = 203 + py * 7;
        ring(cx, cy, 107, 0xEFDBCE, .54, 0);
        ring(cx, cy, 112, 0xA34B56, .3, 0);
        rect(cx - .5, 69 + py * 7, 1, 307, 0xBEDFF1, .32);
        rect(cx - 5, 106 + py * 7, 10, 3, 0xF6ECE4, .85);
        double pulse = .65 + .15 * Math.sin(time * .75);
        for (int i = 6; i >= 1; i--) { rect(cx - 3 * i, cy - 3 * i, i * 6, i * 6, 0xA6DDF6, .018 * pulse); }
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(cx, cy, 0); GL11.glRotated(45 + Math.sin(time * .22) * 4, 0, 0, 1);
            rect(-13, -13, 26, 26, 0xD9F1FA, pulse);
            rect(-10, -10, 20, 20, 0x192D3A, 1);
            rect(-4, -4, 8, 8, 0xFFFFFF, .96);
        } finally { GL11.glPopMatrix(); }
        for (int i = 0; i < 10; i++) { rect(500 - i * 13, 410 + i * 9, 345 + i * 26, 1, 0x7D4551, .17); }
        if (particles) {
            for (int i = 0; i < 32; i++) {
                double x = 390 + (i * 97.43 % 554) + Math.sin(time * .2 + i) * 6 + px * 17;
                double y = (i * 37.79 - time * (1.5 + i % 4)) % 490;
                if (y < 0) { y += 490; }
                rect(x, y + py * 13, i % 4 == 0 ? 2 : 1, 1, 0xEFC9BB, .15 + (i % 3) * .13);
            }
        }
        rect(0, 0, 386, 540, 0x06070B, .92);
        for (int i = 0; i < 24; i++) { rect(386 + i * 4, 0, 4, 540, 0x06070B, .85 * (1 - i / 24.0)); }
        rect(39, 39, 1, 452, 0x60333F, .55);
        rect(40, 39, 23, 1, 0xC5A399, .65);
        rect(40, 490, 23, 1, 0xC5A399, .45);
    }
}
