package br.com.redeakashic.cinematic.render;
/** Cover-fit only the scenic layer; never crop UI hitboxes to fill an aspect ratio. */
public final class BackdropRenderer implements AutoCloseable {
    private final NativeTexture art=new NativeTexture("backdrop.jpg");
    public void draw(int width,int height,double alpha){
        if(width<1||height<1)return;
        br.com.redeakashic.cinematic.core.CoverFit crop=new br.com.redeakashic.cinematic.core.CoverFit(art.width,art.height,width,height);
        art.draw(0,0,width,height,crop.u0,crop.v0,crop.u1,crop.v1,alpha,0xFFFFFF);
    }
    @Override public void close(){art.close();}
}
