package br.com.redeakashic.cinematic.render;
/** Pixel-space backdrop/panel shared by audited vanilla secondary screens and isolated probes. */
public final class SecondaryFrameRenderer implements AutoCloseable {
    private BackdropRenderer backdrop;
    private final UiTypography type=new UiTypography();
    public static int draws;
    public void draw(int w,int h,String section){
        if(w<160||h<120)throw new IllegalArgumentException("surface");
        if(backdrop==null)backdrop=new BackdropRenderer();
        backdrop.draw(w,h,1);
        NativeVisuals.rect(0,0,w,h,NativeVisuals.INK,.54);
        double x=w*.065,y=h*.072,pw=w*.87,ph=h*.855;
        double unit=Math.max(.65,Math.min(1.75,Math.min(w/1366.0,h/768.0)));
        backdrop.glass(x,y,pw,ph,.72);
        NativeVisuals.rect(x,y,pw,ph,NativeVisuals.INK,.71);
        NativeVisuals.outline(x,y,pw,ph,NativeVisuals.GOLD,.30);
        NativeVisuals.garland(x+pw*.55,x+pw-14*unit,y,18*unit,8,unit,0,false,.9);
        type.draw("REDE AKASHIC",x+18*unit,y+16*unit,10*unit,pw*.36,false,2*unit,NativeVisuals.GOLD,.92);
        type.draw(section==null?"":section,x+18*unit,y+35*unit,17*unit,pw*.55,true,.8*unit,NativeVisuals.IVORY,.96);
        type.draw("THE END OF FORBIDDEN  ·  EDIÇÃO DE NATAL",x+18*unit,y+ph-28*unit,8*unit,pw*.45,false,1.2*unit,NativeVisuals.MUTED,.80);
        NativeVisuals.rect(x+18*unit,y+58*unit,pw-36*unit,1,NativeVisuals.GOLD,.22);
        NativeVisuals.star(x+21*unit,y+58*unit,3.5*unit,NativeVisuals.RED,.9,0);
        draws++;
    }
    /** Repaints the frame (backdrop, panel, header) only inside a framebuffer rectangle, using the scissor test. */
    public void region(int w,int h,String section,double x,double y,double rw,double rh){
        int sx=(int)Math.max(0,Math.floor(x)),sy=(int)Math.max(0,Math.floor(y));
        int ex=(int)Math.min(w,Math.ceil(x+rw)),ey=(int)Math.min(h,Math.ceil(y+rh));
        if(ex<=sx||ey<=sy)return;
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor(sx,h-ey,ex-sx,ey-sy);
        try{draw(w,h,section);}finally{org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);}
    }
    /**
     * Translucent surface for a vanilla list (GuiSlot), in place of its opaque dirt: the backdrop and panel stay visible.
     * A list as wide as the screen is clamped to the panel, so it reads as part of the frame.
     */
    public void listSurface(int w,int h,double x,double y,double rw,double rh){
        if(rw<=0||rh<=0)return;
        double unit=Math.max(.65,Math.min(1.75,Math.min(w/1366.0,h/768.0)));
        double inner=w*.065+12*unit,outer=w*.935-12*unit,l=Math.max(x,inner),r=Math.min(x+rw,outer);
        if(r-l<60*unit){l=x;r=x+rw;}
        NativeVisuals.rect(l,y,r-l,rh,NativeVisuals.INK,.46);
        NativeVisuals.rect(l,y,r-l,1,NativeVisuals.GOLD,.42);
        NativeVisuals.rect(l,y+rh-1,r-l,1,NativeVisuals.GOLD,.22);
        NativeVisuals.rect(l,y,1,rh,NativeVisuals.GOLD,.10);NativeVisuals.rect(r-1,y,1,rh,NativeVisuals.GOLD,.10);
        NativeVisuals.rect(l,y+1,2,Math.min(rh-2,28*unit),NativeVisuals.RED,.55);
    }
    @Override public void close(){if(backdrop!=null){backdrop.close();backdrop=null;}type.close();}
}
