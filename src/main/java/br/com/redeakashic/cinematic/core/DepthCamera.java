package br.com.redeakashic.cinematic.core;
/** Image-space depth. All planes share one cover crop; only their bounded offsets differ. */
public final class DepthCamera {
    public static final int SOURCE_W=1207,SOURCE_H=830,PAD=24,TEXTURE_W=1255,TEXTURE_H=878;
    private static final double[] DX={.9,2.8,8.0},DY={.45,1.4,4.0};
    private double left,top,spanX,spanY,mx,my;
    public void update(int width,int height,double x,double y){
        if(width<1||height<1||!Double.isFinite(x)||!Double.isFinite(y))throw new IllegalArgumentException("camera metrics");
        double scale=Math.max((double)width/SOURCE_W,(double)height/SOURCE_H);
        spanX=width/scale;spanY=height/scale;left=(SOURCE_W-spanX)*.5;top=(SOURCE_H-spanY)*.015;
        mx=Math.max(-1,Math.min(1,x));my=Math.max(-1,Math.min(1,y));
    }
    public double u(int plane,double fraction){check(plane);return (PAD+left-DX[plane]*mx+spanX*fraction)/TEXTURE_W;}
    public double v(int plane,double fraction){check(plane);return (PAD+top-DY[plane]*my+spanY*fraction)/TEXTURE_H;}
    private static void check(int plane){if(plane<0||plane>2)throw new IllegalArgumentException("plane");}
}
