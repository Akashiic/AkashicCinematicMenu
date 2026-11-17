package br.com.redeakashic.cinematic.core;
/** Image-space depth. All planes share one cover crop; only their bounded offsets differ. */
public final class DepthCamera {
    /** Edicao de Natal: fonte 2000x1116 com 40 px de margem por lado (tools/build_natal_layers.py). */
    public static final int SOURCE_W=2000,SOURCE_H=1116,PAD=40,TEXTURE_W=2080,TEXTURE_H=1196;
    /** Ancora do corte "cover": a direita (em 4:3 preserva a estrela e a arvore; a esquerda fica sob o painel) e quase no topo (preserva a boina em 21:9). */
    public static final double ANCHOR_X=.72,ANCHOR_Y=.08;
    private static final double[] DX={1.5,4.6,13.3},DY={.75,2.3,6.6};
    private double left,top,spanX,spanY,mx,my;
    public void update(int width,int height,double x,double y){
        if(width<1||height<1||!Double.isFinite(x)||!Double.isFinite(y))throw new IllegalArgumentException("camera metrics");
        double scale=Math.max((double)width/SOURCE_W,(double)height/SOURCE_H);
        spanX=width/scale;spanY=height/scale;left=(SOURCE_W-spanX)*ANCHOR_X;top=(SOURCE_H-spanY)*ANCHOR_Y;
        mx=Math.max(-1,Math.min(1,x));my=Math.max(-1,Math.min(1,y));
    }
    public double u(int plane,double fraction){check(plane);return (PAD+left-DX[plane]*mx+spanX*fraction)/TEXTURE_W;}
    public double v(int plane,double fraction){check(plane);return (PAD+top-DY[plane]*my+spanY*fraction)/TEXTURE_H;}
    private static void check(int plane){if(plane<0||plane>2)throw new IllegalArgumentException("plane");}
}
