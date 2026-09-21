package br.com.redeakashic.cinematic.core;
/** All rectangles are framebuffer pixels. Scenery covers; interactive geometry never scales as a screenshot. */
public final class PixelLayout {
    public final int width,height,sidebar;public final double unit;
    public PixelLayout(int w,int h){
        if(w<1||h<1||w>32768||h>32768)throw new IllegalArgumentException("Unsupported surface");
        width=w;height=h;sidebar=(int)Math.round(w*.27);unit=Math.max(.50,Math.min(2.1,Math.min(w/1366.0,h/768.0)));
    }
    public double buttonX(){return Math.round(sidebar*.10);}
    public double buttonY(int i){if(i<0||i>3)throw new IllegalArgumentException("index");return Math.round(height*.45+i*height*.092);}
    public double buttonWidth(){return sidebar-2*buttonX();}
    public double buttonHeight(){return Math.max(1,Math.round(height*.076));}
    public int hit(double x,double y){
        if(!Double.isFinite(x)||!Double.isFinite(y))return -1;
        for(int i=0;i<4;i++)if(x>=buttonX()&&x<buttonX()+buttonWidth()&&y>=buttonY(i)&&y<buttonY(i)+buttonHeight())return i;
        return -1;
    }
}
