package br.com.redeakashic.cinematic.render;
import br.com.redeakashic.cinematic.core.HeroButtonState;
import org.lwjgl.opengl.GL11;
/** Bounded crimson/white material, independent of the unchanged button hitbox. */
public final class HeroButton {
    private HeroButton(){}
    private static void color(int rgb,double a){GL11.glColor4d((rgb>>16&255)/255.0,(rgb>>8&255)/255.0,(rgb&255)/255.0,a);}
    private static int mix(int a,int b,double t){return ((int)((a>>16&255)*(1-t)+(b>>16&255)*t)<<16)|((int)((a>>8&255)*(1-t)+(b>>8&255)*t)<<8)|(int)((a&255)*(1-t)+(b&255)*t);}
    public static void draw(double x,double y,double w,double h,double age,double hover,double pressed,double opacity,boolean motion){
        double hot=HeroButtonState.clamp(hover),a=HeroButtonState.clamp(opacity),glow=HeroButtonState.glow(age,hot,motion);
        double inset=HeroButtonState.press(pressed,motion)*1.6;
        for(int i=4;i>=1;i--)NativeVisuals.outline(x-i,y-i,w+2*i,h+2*i,0xC93A57,a*glow*(5-i)*.023);
        x+=inset;y+=inset;w-=2*inset;h-=2*inset;
        double drift=motion?.5+.5*Math.sin(age*2*Math.PI/7.2):.5;
        int left=mix(0x540C20,0x74102D,.30+.22*drift+.15*hot),right=mix(0xA92143,0xCB3454,.12+.20*drift+.13*hot);
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glShadeModel(GL11.GL_SMOOTH);GL11.glBegin(GL11.GL_QUADS);
        color(left,a);GL11.glVertex2d(x,y);color(right,a);GL11.glVertex2d(x+w,y);
        color(mix(right,0x310814,.20),a);GL11.glVertex2d(x+w,y+h);color(mix(left,0x240710,.22),a);GL11.glVertex2d(x,y+h);GL11.glEnd();
        double band=HeroButtonState.shimmer(age,motion);
        if(band>-.5){
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            for(int i=0;i<=64;i++){
                double u=i/64.0,energy=Math.exp(-Math.pow((u-band)/.085,2));
                GL11.glColor4d(1,1,1,a*energy*(.075+hot*.12));
                GL11.glVertex2d(x+u*w,y+1);GL11.glVertex2d(x+u*w,y+h-1);
            }GL11.glEnd();
        }
        NativeVisuals.outline(x,y,w,h,0xF0B1C0,a*(.39+.30*hot));
        NativeVisuals.rect(x+1,y+1,w-2,1,0xFFF7FA,a*(.08+glow*.15));
        NativeVisuals.rect(x+1,y+1,2,h-2,0xFFF7FA,a*(.36+.24*hot));
        // Fine double-chevron, remaining strictly inside the right padding.
        double cx=x+w-17,cy=y+h/2,size=Math.max(3,Math.min(5,h*.08));
        GL11.glColor4d(1,.97,.98,a*(.7+.25*hot));GL11.glBegin(GL11.GL_LINES);
        for(int i=0;i<2;i++){double bx=cx-i*6;GL11.glVertex2d(bx-size,cy-size);GL11.glVertex2d(bx,cy);GL11.glVertex2d(bx,cy);GL11.glVertex2d(bx-size,cy+size);}GL11.glEnd();
    }
}
