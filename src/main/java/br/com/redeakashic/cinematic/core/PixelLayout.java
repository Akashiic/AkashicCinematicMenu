package br.com.redeakashic.cinematic.core;
/** All rectangles are framebuffer pixels. Scenery covers; interactive geometry never scales as a screenshot. */
public final class PixelLayout {
    /** Five main buttons (Jogar..Sair) and, below them, the compact Discord button; indices follow {@link MenuAction}. */
    public static final int MAIN=5,DISCORD=5,TARGETS=6;
    public final int width,height,sidebar;public final double unit;
    public PixelLayout(int w,int h){
        if(w<1||h<1||w>32768||h>32768)throw new IllegalArgumentException("Unsupported surface");
        width=w;height=h;sidebar=(int)Math.round(w*.27);unit=Math.max(.50,Math.min(2.1,Math.min(w/1366.0,h/768.0)));
    }
    public double buttonX(){return Math.round(sidebar*.10);}
    public double buttonY(int i){if(i<0||i>=MAIN)throw new IllegalArgumentException("index");return Math.round(height*.398+i*height*.079);}
    public double buttonWidth(){return sidebar-2*buttonX();}
    public double buttonHeight(){return Math.max(1,Math.round(height*.066));}
    /** Compact Discord button: same column, shorter, between the last main button and the footer. */
    public double discordY(){return Math.round(height*.800);}
    public double discordHeight(){return Math.max(1,Math.round(height*.048));}
    public double targetY(int i){if(i<0||i>=TARGETS)throw new IllegalArgumentException("index");return i==DISCORD?discordY():buttonY(i);}
    public double targetHeight(int i){if(i<0||i>=TARGETS)throw new IllegalArgumentException("index");return i==DISCORD?discordHeight():buttonHeight();}
    public int hit(double x,double y){
        if(!Double.isFinite(x)||!Double.isFinite(y))return -1;
        if(x<buttonX()||x>=buttonX()+buttonWidth())return -1;
        for(int i=0;i<TARGETS;i++)if(y>=targetY(i)&&y<targetY(i)+targetHeight(i))return i;
        return -1;
    }
}
