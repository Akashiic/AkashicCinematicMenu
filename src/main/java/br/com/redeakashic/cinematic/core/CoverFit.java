package br.com.redeakashic.cinematic.core;
/** Center crop for full-canvas backgrounds; UI keeps its independent contain transform. */
public final class CoverFit {
    public final double u0,v0,u1,v1;
    public CoverFit(int imageWidth,int imageHeight,int canvasWidth,int canvasHeight){
        if(imageWidth<1||imageHeight<1||canvasWidth<1||canvasHeight<1)throw new IllegalArgumentException("positive dimensions required");
        double scale=Math.max((double)canvasWidth/imageWidth,(double)canvasHeight/imageHeight);
        double u=canvasWidth/(imageWidth*scale),v=canvasHeight/(imageHeight*scale);
        u0=(1-u)*.5;u1=(1+u)*.5;v0=(1-v)*.5;v1=(1+v)*.5;
    }
}
