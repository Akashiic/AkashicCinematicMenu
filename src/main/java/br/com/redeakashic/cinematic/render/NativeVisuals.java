package br.com.redeakashic.cinematic.render;
import br.com.redeakashic.cinematic.core.LoadState;
import br.com.redeakashic.cinematic.core.PixelLayout;
import org.lwjgl.opengl.GL11;
/** Pixel-native composition. Scenic art is independent of text, panels, ornaments and progress. */
public final class NativeVisuals implements AutoCloseable {
    /** Edicao de Natal: tinta cafe, ouro de estrela, creme, vermelho de laco e verde pinheiro (tirados da arte). */
    public static final int INK=0x170D09,SLATE=0x2E1C14,IVORY=0xFFF3DE,GOLD=0xE9BF63,MUTED=0xD4BE9E,ICE=0xFFF7EA,
        RED=0xC8283C,PINE=0x3B7A47,GLOW=0xFFD98A,DISABLED=0x8C7B6A,DISCORD=0x5865F2,DISCORD_HOT=0x7983F5;
    private static final int[] BULBS={RED,GLOW,PINE,ICE};
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
    /** Estrela de cinco pontas preenchida (cinco "pipas" convexas), com halo opcional. */
    public static void star(double x,double y,double radius,int color,double alpha,double halo){
        if(radius<=0||alpha<=0)return;
        for(int k=3;k>=1&&halo>0;k--)starShape(x,y,radius*(1+k*.45),color,alpha*halo*(.10/k));
        starShape(x,y,radius,color,alpha);
    }
    private static void starShape(double x,double y,double radius,int color,double alpha){
        double inner=radius*.42;GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(((color>>16)&255)/255.0,((color>>8)&255)/255.0,(color&255)/255.0,Math.max(0,Math.min(1,alpha)));
        GL11.glBegin(GL11.GL_QUADS);
        for(int i=0;i<5;i++){
            double tip=-Math.PI/2+i*2*Math.PI/5,l=tip-Math.PI/5,r=tip+Math.PI/5;
            GL11.glVertex2d(x,y);GL11.glVertex2d(x+Math.cos(l)*inner,y+Math.sin(l)*inner);
            GL11.glVertex2d(x+Math.cos(tip)*radius,y+Math.sin(tip)*radius);GL11.glVertex2d(x+Math.cos(r)*inner,y+Math.sin(r)*inner);
        }
        GL11.glEnd();
    }
    /** Guirlanda de luzes: fio em curva e lampadas alternadas; pisca apenas quando ha movimento permitido. */
    public static void garland(double x0,double x1,double y,double sag,int bulbs,double s,double age,boolean animate,double alpha){
        if(x1<=x0||alpha<=0||bulbs<2)return;
        int segments=Math.max(12,(int)((x1-x0)/(3*Math.max(.5,s))));
        for(int i=0;i<segments;i++){double t=(double)i/segments,cx=x0+(x1-x0)*t,cy=y+sag*4*t*(1-t);rect(cx,Math.round(cy),Math.max(1,(x1-x0)/segments+.5),1,0x2A3A22,alpha*.85);}
        double size=Math.max(3,Math.round(4*s));
        for(int i=0;i<bulbs;i++){
            double t=(i+.5)/bulbs,cx=x0+(x1-x0)*t,cy=y+sag*4*t*(1-t)+1;int c=BULBS[i%BULBS.length];
            double twinkle=animate?.55+.45*Math.max(0,Math.sin(age*1.7+i*2.31)):.85;
            rect(cx-size*2,cy-size*.5,size*5,size*4,c,alpha*.07*twinkle);rect(cx-size,cy,size*3,size*2.6,c,alpha*.12*twinkle);
            rect(cx-size*.5,cy+size*.5,size,size*1.3,c,alpha*(.55+.45*twinkle));
            rect(cx-size*.5,cy,size,Math.max(1,size*.45),0x3A3328,alpha*.9);
        }
    }
    /** Neve leve e deterministica (sem estado, sem threads); usa apenas a idade do quadro. */
    public static void snow(double x0,double width,double height,int count,double s,double age,double alpha){
        for(int i=0;i<count;i++){
            double seed=(i*.61803398875)%1,speed=.010+.012*((i*.7548776662)%1),size=Math.max(1,Math.round(s*(1+(i%3)*.75)));
            double y=((i*.381966+age*speed)%1)*height,x=x0+seed*width+Math.sin(age*.6+i*1.3)*6*s;
            rect(x,y,size,size,ICE,alpha*(.22+.18*(i%3)));
        }
    }
    public void transform(int w,int h){layout.resize(w,h);lastWidth=w;lastHeight=h;}
    private void background(double a,double px,double py){prepareMenu();backdrop.draw(layout.width,layout.height,a,px,py);}
    private void text(String value,double x,double y,double size,double limit,boolean serif,double spacing,int colour,double a){type.draw(value,x,y,size,limit,serif,spacing,colour,a);}
    private void center(String value,double x,double y,double size,double limit,boolean serif,double spacing,int colour,double a){type.centered(value,x,y,size,limit,serif,spacing,colour,a);}
    /** Texto sobre a arte clara: sombra cafe deslocada antes do texto. */
    private void quote(String value,double x,double y,double size,double limit,double spacing,double s,double a){
        double d=Math.max(1,Math.round(1.5*s));type.centered(value,x+d,y+d,size,limit,true,spacing,INK,.75*a);type.centered(value,x,y,size,limit,true,spacing,IVORY,.96*a);
    }
    public void menu(double age,double px,double py,double visibility,double[] highlights,boolean motion,boolean particles){
        menuFrames++;double w=layout.width,h=layout.height,s=layout.pixels.unit,p=layout.sidebar,c=p/2;
        prepareMenu();backdrop.drawDepth(layout.width,layout.height,1,motion?px:0,motion?py:0,motion&&depthEnabled);
        if(motion&&particles)snow(p,w-p,h,46,s,age,1);
        backdrop.glass(0,0,p,h,.92);rect(0,0,p,h,INK,.70);rect(p-1,0,1,h,GOLD,.30);
        outline(Math.round(p*.05),Math.round(h*.045),Math.round(p*.90),Math.round(h*.93),GOLD,.16);
        garland(p*.05,p*.95,h*.045,22*s,9,s,age,motion,1);
        star(c,h*.098,10*s,GOLD,.95,motion?.85+.15*Math.sin(age*1.1):.9);
        center("THE END OF",c,h*.140,11*s,p*.80,false,3*s,MUTED,1);
        center("FORBIDDEN",c,h*.180,43*s,p*.86,true,1.3*s,IVORY,1);
        rect(p*.23,h*.272,p*.54,1,GOLD,.35);star(c,h*.272,4.5*s,RED,.95,0);
        if(h>=600){center("Até nos corredores sem fim,",c,h*.300,11*s,p*.83,true,1*s,IVORY,.88);center("o Natal encontra caminho.",c,h*.326,11*s,p*.83,true,.7*s,IVORY,.88);}
        rect(p*.10,h*.366+3*s,4*s,4*s,RED,.9);rect(p*.10+7*s,h*.366+3*s,4*s,4*s,PINE,.95);
        text("EDIÇÃO DE NATAL",p*.10+16*s,h*.366,9*s,p*.7,false,2*s,GOLD,.88);
        String[] actions={"JOGAR","SINGLEPLAYER","MULTIJOGADOR","OPÇÕES","SAIR"};
        for(int i=0;i<PixelLayout.MAIN;i++){
            double x=layout.pixels.buttonX(),y=layout.pixels.buttonY(i),bw=layout.pixels.buttonWidth(),bh=layout.pixels.buttonHeight(),hot=Math.max(0,Math.min(1,highlights[i]));
            if(i==0)HeroButton.draw(x,y,bw,bh,age,hot,playPressAge,visibility,motion&&heroAnimation);
            else{rect(x,y,bw,bh,SLATE,(.30+hot*.40)*visibility);outline(x,y,bw,bh,GOLD,(.20+hot*.50)*visibility);if(hot>.005){rect(x,y,2,bh,RED,hot*visibility);rect(x+2,y,1,bh,GOLD,hot*.6*visibility);}}
            text("0"+(i+1),x+12*s,y+(bh-10*s)/2,9*s,24*s,false,1*s,i==0?0xFFF5E8:GOLD,visibility*.88);
            text(actions[i],x+36*s,y+(bh-19*s)/2-1,19*s,bw-(i==0?67:48)*s,true,1*s,i==0?0xFFFFFF:IVORY,visibility);
        }
        discord(layout.pixels.buttonX(),layout.pixels.discordY(),layout.pixels.buttonWidth(),layout.pixels.discordHeight(),s,Math.max(0,Math.min(1,highlights[PixelLayout.DISCORD])),visibility);
        text("REDE AKASHIC",p*.10,h*.877,10*s,p*.80,false,2*s,GOLD,1);
        text("FORBIDDEN SERIES  /  1.7.10",p*.10,h*.919,8*s,p*.80,false,1*s,MUTED,1);
        text("BOAS FESTAS  ·  NATAL 2026",p*.10,h*.947,8*s,p*.80,false,1*s,MUTED,.85);
        shadeV(p,h*.80,w-p,h*.20,INK,0,.55);
        quote("Corra. O Natal não espera.",w*.79,h*.897,23*s,w*.37,.3*s,s,1);
    }
    private static int mixRgb(int a,int b,double t){t=Math.max(0,Math.min(1,t));
        return ((int)((a>>16&255)*(1-t)+(b>>16&255)*t)<<16)|((int)((a>>8&255)*(1-t)+(b>>8&255)*t)<<8)|(int)((a&255)*(1-t)+(b&255)*t);}
    /** Botao compacto do Discord (azul da marca), com balao de conversa generico e seta de link externo. */
    private void discord(double x,double y,double bw,double bh,double s,double hot,double vis){
        int fill=mixRgb(DISCORD,DISCORD_HOT,hot);
        rect(x,y,bw,bh,fill,(.88+.12*hot)*vis);
        rect(x+1,y+1,bw-2,1,0xFFFFFF,(.16+.24*hot)*vis);
        outline(x,y,bw,bh,0xFFFFFF,(.12+.45*hot)*vis);
        if(hot>.005)rect(x,y,2,bh,0xFFFFFF,hot*vis);
        double ih=Math.max(5,Math.round(bh*.40)),iw=Math.round(ih*1.3),ix=Math.round(x+12*s),iy=Math.round(y+(bh-ih)/2-ih*.08);
        rect(ix,iy,iw,ih,0xFFFFFF,.96*vis);rect(ix+iw*.16,iy+ih,Math.max(1,iw*.22),Math.max(1,ih*.24),0xFFFFFF,.96*vis);
        double eye=Math.max(1,Math.round(ih*.22));rect(ix+iw*.27,iy+ih*.36,eye,eye,fill,vis);rect(ix+iw*.73-eye,iy+ih*.36,eye,eye,fill,vis);
        double label=11*s,tx=ix+iw+10*s;
        text("DISCORD",tx,y+(bh-label)/2-1,label,bw*.45,false,2.5*s,0xFFFFFF,vis);
        double small=9*s,arrow=Math.max(4,Math.round(bh*.26)),ax=Math.round(x+bw-12*s-arrow),ay=Math.round(y+(bh-arrow)/2);
        String hint="ENTRAR";double hw=type.width(hint,false,small,1.5*s);
        text(hint,ax-8*s-hw,y+(bh-small)/2-1,small,bw*.30,false,1.5*s,0xFFFFFF,(.78+.22*hot)*vis);
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glColor4d(1,1,1,(.85+.15*hot)*vis);GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(ax,ay+arrow);GL11.glVertex2d(ax+arrow,ay);GL11.glVertex2d(ax+arrow*.35,ay);GL11.glVertex2d(ax+arrow,ay);
        GL11.glVertex2d(ax+arrow,ay);GL11.glVertex2d(ax+arrow,ay+arrow*.65);GL11.glEnd();
    }
    public void startup(LoadState state,double elapsed,double alpha,boolean animate){
        startupFrames++;double w=layout.width,h=layout.height,s=layout.pixels.unit,a=Math.max(0,Math.min(1,alpha));
        background(a,0,0);shade(0,0,w*.69,h,INK,.80*a,0);shadeV(0,h*.78,w,h*.22,INK,0,.62*a);
        outline(Math.round(w*.025),Math.round(h*.03),Math.round(w*.95),Math.round(h*.94),GOLD,.22*a);
        garland(w*.30,w*.60,h*.03,34*s,11,s,elapsed,animate,a);
        star(w*.061,h*.076+2*s,7*s,GOLD,.95*a,.6);
        text("REDE AKASHIC",w*.084,h*.058,12*s,w*.35,false,3*s,IVORY,a);
        text("FORBIDDEN SERIES / 1.7.10",w*.084,h*.085,8*s,w*.35,false,1.3*s,MUTED,a);
        double hero=w*.30;star(hero,h*.232,13*s,GOLD,a,animate?.8+.2*Math.sin(elapsed*1.1):.9);
        center("THE END OF",hero,h*.306,14*s,w*.48,false,5*s,MUTED,a);
        center("FORBIDDEN",hero,h*.356,74*s,w*.53,true,3*s,IVORY,a);
        rect(hero-w*.10,h*.492,w*.20,1,GOLD,.55*a);star(hero,h*.492,6*s,RED,a,0);
        if(h>=600)center("Acendam as luzes: o Natal chegou aos corredores.",hero,h*.522,14*s,w*.48,true,.45*s,IVORY,.86*a);
        double x=Math.round(w*.055),y=Math.round(h*.612),cw=Math.round(w*.48),ch=Math.round(170*s);
        backdrop.glass(x,y,cw,ch,.68*a);rect(x,y,cw,ch,INK,.76*a);outline(x,y,cw,ch,GOLD,.40*a);rect(x,y,cw,2,RED,.70*a);
        text("PREPARANDO A NOITE FELIZ",x+20*s,y+17*s,9*s,cw*.78,false,1.55*s,GOLD,a);
        String fraction=state.total>0?state.step+" / "+state.total:"";
        text(fraction,x+cw-88*s,y+17*s,8*s,70*s,false,.25*s,MUTED,a);
        text(state.title,x+20*s,y+51*s,27*s,cw-140*s,true,.1*s,IVORY,a);
        text(state.detail,x+20*s,y+85*s,11*s,cw-80*s,false,0,MUTED,a);
        center(state.percent(),x+cw-60*s,y+47*s,37*s,94*s,true,0,IVORY,a);
        center(state.total>0?"DA ETAPA":"SEM TOTAL",x+cw-60*s,y+94*s,8*s,100*s,false,.7*s,GOLD,.9*a);
        double bx=x+20*s,by=Math.round(y+127*s),bw=cw-40*s;
        rect(bx,by,bw,2,GOLD,.22*a);
        if(state.total>0){double fill=bw*state.fraction();rect(bx,by,fill,2,GOLD,a);if(fill>=3)rect(bx+fill-3,by-1,3,4,RED,a);}
        else{double p=animate?.5+.5*Math.sin(elapsed*1.25):.5;rect(bx+p*bw*.82,by,bw*.18,2,GOLD,.8*a);}
        text(state.branch,bx,y+145*s,9*s,bw,false,0,MUTED,.88*a);
        text("Progresso da etapa ativa / Forge",x,y+ch+12*s,9*s,cw*.8,false,.3*s,MUTED,.85*a);
        text(String.format(java.util.Locale.ROOT,"%02d:%02d",(int)elapsed/60,(int)elapsed%60),x+cw-62*s,y+ch+12*s,9*s,62*s,false,0,MUTED,a);
        text("NOITE FELIZ",w*.05,h*.918,9*s,w*.3,false,2*s,GOLD,a);
        text("EDIÇÃO DE NATAL  ·  REDE AKASHIC",w*.05,h*.944,8*s,w*.35,false,1.2*s,MUTED,.85*a);
        quote("Leve a árvore.",w*.80,h*.840,24*s,w*.30,.2*s,s,a);
        quote("Não olhe para trás.",w*.80,h*.886,24*s,w*.30,.2*s,s,a);
        text("FORGE 1.7.10",w*.83,h*.944,8*s,w*.14,false,1.5*s,GOLD,.9*a);
    }
    /** Fundo das telas de transicao (TransitionTheme); fica aqui para ser renderizavel fora do jogo. */
    public static void transition(BackdropRenderer backdrop,UiTypography text,int w,int h){
        backdrop.draw(w,h,1);rect(0,0,w,h,INK,.66);
        double scale=Math.max(.65,Math.min(2,Math.min(w/1366.0,h/768.0)));
        garland(w*.10,w*.34,h*.045,26*scale,8,scale,0,false,.95);garland(w*.34,w*.58,h*.045,26*scale,8,scale,.9,false,.95);
        text.centered("THE END OF FORBIDDEN",w*.5,h*.14,32*scale,w*.80,true,2*scale,IVORY,.94);
        rect(w*.37,h*.25,w*.26,1,GOLD,.65);star(w*.5,h*.25,5*scale,RED,.95,0);
        rect(w*.10,h*.38,w*.80,h*.26,INK,.58);
        text.centered("A TRAVESSIA DE NATAL",w*.5,h*.77,13*scale,w*.8,false,3*scale,GOLD,.92);
        text.centered("REDE AKASHIC",w*.5,h*.83,10*scale,w*.8,false,2*scale,MUTED,.85);
    }
    private static void shade(double x,double y,double w,double h,int colour,double left,double right){
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glShadeModel(GL11.GL_SMOOTH);
        double r=((colour>>16)&255)/255.0,g=((colour>>8)&255)/255.0,b=(colour&255)/255.0;
        GL11.glBegin(GL11.GL_QUADS);GL11.glColor4d(r,g,b,left);GL11.glVertex2d(x,y);GL11.glVertex2d(x,y+h);
        GL11.glColor4d(r,g,b,right);GL11.glVertex2d(x+w,y+h);GL11.glVertex2d(x+w,y);GL11.glEnd();
    }
    private static void shadeV(double x,double y,double w,double h,int colour,double top,double bottom){
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glShadeModel(GL11.GL_SMOOTH);
        double r=((colour>>16)&255)/255.0,g=((colour>>8)&255)/255.0,b=(colour&255)/255.0;
        GL11.glBegin(GL11.GL_QUADS);GL11.glColor4d(r,g,b,top);GL11.glVertex2d(x,y);GL11.glVertex2d(x+w,y);
        GL11.glColor4d(r,g,b,bottom);GL11.glVertex2d(x+w,y+h);GL11.glVertex2d(x,y+h);GL11.glEnd();
    }
    public void releaseBoot(){/* Text atlas reuse is intentional across the splash-to-menu fade. */}
    @Override public void close(){if(backdrop!=null){backdrop.close();backdrop=null;}type.close();}
}
