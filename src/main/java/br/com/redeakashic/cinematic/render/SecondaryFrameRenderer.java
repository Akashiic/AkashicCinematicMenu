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
        backdrop.glass(x,y,pw,ph,.72);
        NativeVisuals.rect(x,y,pw,ph,NativeVisuals.INK,.71);
        NativeVisuals.outline(x,y,pw,ph,NativeVisuals.GOLD,.27);
        double unit=Math.max(.65,Math.min(1.75,Math.min(w/1366.0,h/768.0)));
        type.draw("REDE AKASHIC",x+18*unit,y+16*unit,10*unit,pw*.36,false,2*unit,NativeVisuals.GOLD,.92);
        type.draw(section==null?"":section,x+18*unit,y+35*unit,17*unit,pw*.55,true,.8*unit,NativeVisuals.IVORY,.96);
        type.draw("THE END OF FORBIDDEN",x+18*unit,y+ph-28*unit,8*unit,pw*.45,false,1.2*unit,NativeVisuals.MUTED,.74);
        NativeVisuals.rect(x+18*unit,y+58*unit,pw-36*unit,1,NativeVisuals.GOLD,.18);
        draws++;
    }
    @Override public void close(){if(backdrop!=null){backdrop.close();backdrop=null;}type.close();}
}
