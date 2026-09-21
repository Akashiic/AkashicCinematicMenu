package br.com.redeakashic.cinematic.render;
import br.com.redeakashic.cinematic.core.LoadState;
import org.lwjgl.opengl.GL11;
/** Pixel-native composition. Scenic art is independent of text, panels, ornaments and progress. */
public final class NativeVisuals implements AutoCloseable {
    public static final int INK=0x090D14,SLATE=0x171C25,IVORY=0xE8E4D8,GOLD=0xC4AA7C,MUTED=0xACB2BB,ICE=0xDCEBFA;
    public final NativeLayout layout;private BackdropRenderer backdrop;private final UiTypography type=new UiTypography();
    public double playPressAge=-1;public boolean depthEnabled=true,heroAnimation=true;
    public static long menuFrames,startupFrames;public static int lastWidth,lastHeight;
    public NativeVisuals(String profile){layout=new NativeLayout(profile);}
    public void prepareMenu(){if(backdrop==null)backdrop=new BackdropRenderer();}
    public void prepareBoot(){prepareMenu();}
    public static void rect(double x,double y,double w,double h,int rgb,double a){
        if(w<=0||h<=0||a<=0)return;
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glColor4d(((rgb>>16)&255)/255.0,((rgb>>8)&255)/255.0,(rgb&255)/255.0,Math.max(0,Math.min(1,a)));
        GL11.glBegin(GL11.GL_QUADS);GL11.glVertex2d(x,y);GL11.glVertex2d(x+w,y);GL11.glVertex2d(x+w,y+h);GL11.glVertex2d(x,y+h);GL11.glEnd();
    }
    public static void outline(double x,double y,double w,double h,int color,double alpha){
        rect(Math.round(x),Math.round(y),Math.ceil(w),1,color,alpha);rect(Math.round(x),Math.round(y+h-1),Math.ceil(w),1,color,alpha);
        rect(Math.round(x),Math.round(y),1,Math.ceil(h),color,alpha);rect(Math.round(x+w-1),Math.round(y),1,Math.ceil(h),color,alpha);
    }
    private static void diamond(double x,double y,double radius,int color,double alpha){
        GL11.glPushMatrix();GL11.glTranslated(Math.round(x),Math.round(y),0);GL11.glRotated(45,0,0,1);outline(-radius/2,-radius/2,radius,radius,color,alpha);GL11.glPopMatrix();
    }
    public void transform(int w,int h){layout.resize(w,h);lastWidth=w;lastHeight=h;}
    private void background(double a,double px,double py){prepareMenu();backdrop.draw(layout.width,layout.height,a,px,py);}
    private void text(String value,double x,double y,double size,double limit,boolean serif,double spacing,int colour,double a){type.draw(value,x,y,size,limit,serif,spacing,colour,a);}
    private void center(String value,double x,double y,double size,double limit,boolean serif,double spacing,int colour,double a){type.centered(value,x,y,size,limit,serif,spacing,colour,a);}
    public void menu(double age,double px,double py,double visibility,double[] highlights,boolean motion,boolean particles){
        menuFrames++;double w=layout.width,h=layout.height,s=layout.pixels.unit,p=layout.sidebar,c=p/2;
        prepareMenu();backdrop.drawDepth(layout.width,layout.height,1,motion?px:0,motion?py:0,motion&&depthEnabled);backdrop.glass(0,0,p,h,.92);rect(0,0,p,h,INK,.70);rect(p-1,0,1,h,GOLD,.25);
        outline(Math.round(p*.05),Math.round(h*.045),Math.round(p*.90),Math.round(h*.93),GOLD,.14);
        diamond(c,h*.085,10*s,GOLD,.68);diamond(c,h*.085,5*s,ICE,.38);
        center("THE END OF",c,h*.135,11*s,p*.80,false,3*s,MUTED,1);
        center("FORBIDDEN",c,h*.177,43*s,p*.86,true,1.3*s,IVORY,1);
        rect(p*.23,h*.269,p*.54,1,GOLD,.3);diamond(c,h*.269,5*s,GOLD,.8);
        if(h>=600){center("Há lugares que",c,h*.300,11*s,p*.83,true,1*s,IVORY,.85);center("o tempo não alcança.",c,h*.326,11*s,p*.83,true,.7*s,IVORY,.85);}
        text("O LIMIAR",p*.10,h*.412,9*s,p*.7,false,2*s,GOLD,.82);
        String[] actions={"JOGAR","SINGLEPLAYER","OPÇÕES","SAIR"};
        for(int i=0;i<4;i++){
            double x=layout.pixels.buttonX(),y=layout.pixels.buttonY(i),bw=layout.pixels.buttonWidth(),bh=layout.pixels.buttonHeight(),hot=Math.max(0,Math.min(1,highlights[i]));
            if(i==0)HeroButton.draw(x,y,bw,bh,age,hot,playPressAge,visibility,motion&&heroAnimation);
            else{rect(x,y,bw,bh,SLATE,(.18+hot*.40)*visibility);outline(x,y,bw,bh,GOLD,(.18+hot*.48)*visibility);if(hot>.005)rect(x,y,2,bh,GOLD,hot*visibility);}
            text("0"+(i+1),x+12*s,y+(bh-10*s)/2,9*s,24*s,false,1*s,i==0?0xFFF5F8:GOLD,visibility*.88);
            text(actions[i],x+36*s,y+(bh-19*s)/2-1,19*s,bw-(i==0?67:48)*s,true,1*s,i==0?0xFFFFFF:IVORY,visibility);
        }
        text("REDE AKASHIC",p*.10,h*.877,10*s,p*.80,false,2*s,GOLD,1);
        text("FORBIDDEN SERIES  /  1.7.10",p*.10,h*.919,8*s,p*.80,false,1*s,MUTED,1);
        text("THE END OF FORBIDDEN",p*.10,h*.947,7*s,p*.80,false,1*s,MUTED,.80);
        rect(p,h*.83,w-p,h*.17,INK,.23);
        center("O silêncio também observa.",w*.79,h*.897,23*s,w*.37,true,.3*s,IVORY,.9);
        if(motion&&particles)for(int i=0;i<16;i++){double x=p+((i*.61803398875)%1)*(w-p),y=((i*.381966+age*.005)%1)*h;rect(x,y,1,1,ICE,.16);}
    }
    public void startup(LoadState state,double elapsed,double alpha,boolean animate){
        startupFrames++;double w=layout.width,h=layout.height,s=layout.pixels.unit,a=Math.max(0,Math.min(1,alpha));
        background(a,0,0);shade(0,0,w*.69,h,INK,.78*a,0);rect(0,h*.86,w,h*.14,INK,.40*a);
        outline(Math.round(w*.025),Math.round(h*.03),Math.round(w*.95),Math.round(h*.94),GOLD,.20*a);
        diamond(w*.061,h*.076,9*s,GOLD,.85*a);
        text("REDE AKASHIC",w*.084,h*.058,12*s,w*.35,false,3*s,IVORY,a);
        text("FORBIDDEN SERIES / 1.7.10",w*.084,h*.085,8*s,w*.35,false,1.3*s,MUTED,a);
        double hero=w*.30;diamond(hero,h*.232,13*s,GOLD,.6*a);diamond(hero,h*.232,7*s,ICE,.5*a);
        center("THE END OF",hero,h*.306,14*s,w*.48,false,5*s,MUTED,a);
        center("FORBIDDEN",hero,h*.356,74*s,w*.53,true,3*s,IVORY,a);
        rect(hero-w*.10,h*.492,w*.20,1,GOLD,.50*a);diamond(hero,h*.492,6*s,GOLD,a);
        if(h>=600)center("Antes do primeiro passo, o mundo desperta.",hero,h*.522,14*s,w*.48,true,.45*s,IVORY,.83*a);
        double x=Math.round(w*.055),y=Math.round(h*.612),cw=Math.round(w*.48),ch=Math.round(170*s);
        backdrop.glass(x,y,cw,ch,.68*a);rect(x,y,cw,ch,INK,.74*a);outline(x,y,cw,ch,GOLD,.38*a);
        text("SEQUÊNCIA DE INICIALIZAÇÃO",x+20*s,y+17*s,9*s,cw*.78,false,1.55*s,GOLD,a);
        String fraction=state.total>0?state.step+" / "+state.total:"";
        text(fraction,x+cw-88*s,y+17*s,8*s,70*s,false,.25*s,MUTED,a);
        text(state.title,x+20*s,y+51*s,27*s,cw-140*s,true,.1*s,IVORY,a);
        text(state.detail,x+20*s,y+85*s,11*s,cw-80*s,false,0,MUTED,a);
        center(state.percent(),x+cw-60*s,y+47*s,37*s,94*s,true,0,IVORY,a);
        center(state.total>0?"DA ETAPA":"SEM TOTAL",x+cw-60*s,y+94*s,8*s,100*s,false,.7*s,GOLD,.9*a);
        double bx=x+20*s,by=Math.round(y+127*s),bw=cw-40*s;
        rect(bx,by,bw,2,GOLD,.20*a);
        if(state.total>0)rect(bx,by,bw*state.fraction(),2,GOLD,a);
        else{double p=animate?.5+.5*Math.sin(elapsed*1.25):.5;rect(bx+p*bw*.82,by,bw*.18,2,GOLD,.8*a);}
        text(state.branch,bx,y+145*s,9*s,bw,false,0,MUTED,.88*a);
        text("Progresso da etapa ativa / Forge",x,y+ch+12*s,9*s,cw*.8,false,.3*s,MUTED,.85*a);
        text(String.format(java.util.Locale.ROOT,"%02d:%02d",(int)elapsed/60,(int)elapsed%60),x+cw-62*s,y+ch+12*s,9*s,62*s,false,0,MUTED,a);
        text("O DESPERTAR",w*.05,h*.918,9*s,w*.3,false,2*s,GOLD,a);
        text("INICIALIZAÇÃO DO SANTUÁRIO",w*.05,h*.944,7*s,w*.35,false,1.2*s,MUTED,.85*a);
        center("A pedra guarda.",w*.81,h*.655,25*s,w*.30,true,.2*s,IVORY,.9*a);
        center("A vida retorna.",w*.81,h*.704,25*s,w*.30,true,.2*s,IVORY,.9*a);
        text("FORGE 1.7.10",w*.83,h*.925,8*s,w*.14,false,1.5*s,GOLD,.9*a);
    }
    private static void shade(double x,double y,double w,double h,int colour,double left,double right){
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glShadeModel(GL11.GL_SMOOTH);
        double r=((colour>>16)&255)/255.0,g=((colour>>8)&255)/255.0,b=(colour&255)/255.0;
        GL11.glBegin(GL11.GL_QUADS);GL11.glColor4d(r,g,b,left);GL11.glVertex2d(x,y);GL11.glVertex2d(x,y+h);
        GL11.glColor4d(r,g,b,right);GL11.glVertex2d(x+w,y+h);GL11.glVertex2d(x+w,y);GL11.glEnd();
    }
    public void releaseBoot(){/* Text atlas reuse is intentional across the splash-to-menu fade. */}
    @Override public void close(){if(backdrop!=null){backdrop.close();backdrop=null;}type.close();}
}
