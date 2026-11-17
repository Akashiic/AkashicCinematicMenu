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
    /**
     * Fractions of the height: header rule under FORBIDDEN, end of the two-line tagline (shown when h>=600),
     * button height, the taller Jogar button, the gap between buttons, the larger gap under Jogar, and the Discord top.
     */
    static final double HEADER_RULE=.272,TAGLINE_END=.350,BUTTON=.060,HERO=.081,GAP=.010,HERO_GAP=.026,DISCORD_TOP=.800;
    public double buttonX(){return Math.round(sidebar*.10);}
    /** Header rule (divider under the title); drawn by the renderer at exactly this row. */
    public double headerRuleY(){return Math.round(height*HEADER_RULE);}
    /** Upper bound of the free space: the end of the tagline when it is shown, else the header rule. */
    public double groupUpperBound(){return height>=600?Math.round(height*TAGLINE_END):headerRuleY();}
    /** Rows of the group: Jogar; Single | Multi side by side (owner request 17/11); Opcoes; Sair. */
    public static final int ROWS=4;
    /** Row of a main button (0..4 -> 0,1,1,2,3). */
    public static int rowOf(int i){if(i<0||i>=MAIN)throw new IllegalArgumentException("index");return i<=1?i:i-1;}
    /** Height of the group (Jogar taller than the rest). */
    public double groupHeight(){return heroHeight()+heroGap()+(ROWS-1)*buttonHeight()+(ROWS-2)*gap();}
    /** Gap between the regular buttons. */
    public double gap(){return Math.round(height*GAP);}
    /** Larger gap under Jogar, to set it apart (owner request 17/11). */
    public double heroGap(){return Math.round(height*HERO_GAP);}
    /** Top of the group, centered in the free space between the tagline (or header rule) and the Discord button (owner choice B, 17/11). */
    public double groupTop(){return Math.round(groupUpperBound()+(discordY()-groupUpperBound()-groupHeight())/2);}
    public double buttonY(int i){
        if(i<0||i>=MAIN)throw new IllegalArgumentException("index");
        int row=rowOf(i);
        return row==0?groupTop():groupTop()+heroHeight()+heroGap()+(row-1)*(buttonHeight()+gap());
    }
    /** Horizontal gap between Single and Multi. */
    public double splitGap(){return Math.max(gap(),Math.round(sidebar*.03));}
    /** Single and Multi are equal halves that together span exactly the container width. */
    public double halfWidth(){return Math.floor((buttonWidth()-splitGap())/2);}
    public double targetX(int i){
        if(i<0||i>=TARGETS)throw new IllegalArgumentException("index");
        return i==MenuAction.MULTIPLAYER.ordinal()?buttonX()+buttonWidth()-halfWidth():buttonX();
    }
    public double targetWidth(int i){
        if(i<0||i>=TARGETS)throw new IllegalArgumentException("index");
        return i==MenuAction.SINGLEPLAYER.ordinal()||i==MenuAction.MULTIPLAYER.ordinal()?halfWidth():buttonWidth();
    }
    public double buttonWidth(){return sidebar-2*buttonX();}
    /** Regular main-button height (Singleplayer..Sair). */
    public double buttonHeight(){return Math.max(1,Math.round(height*BUTTON));}
    /** The Jogar button is taller, to stand out. */
    public double heroHeight(){return Math.max(1,Math.round(height*HERO));}
    public double buttonHeight(int i){if(i<0||i>=MAIN)throw new IllegalArgumentException("index");return i==0?heroHeight():buttonHeight();}
    /** Compact Discord button: same column, shorter, between the last main button and the footer. */
    public double discordY(){return Math.round(height*DISCORD_TOP);}
    public double discordHeight(){return Math.max(1,Math.round(height*.048));}
    public double targetY(int i){if(i<0||i>=TARGETS)throw new IllegalArgumentException("index");return i==DISCORD?discordY():buttonY(i);}
    public double targetHeight(int i){if(i<0||i>=TARGETS)throw new IllegalArgumentException("index");return i==DISCORD?discordHeight():buttonHeight(i);}
    public int hit(double x,double y){
        if(!Double.isFinite(x)||!Double.isFinite(y))return -1;
        for(int i=0;i<TARGETS;i++)if(x>=targetX(i)&&x<targetX(i)+targetWidth(i)&&y>=targetY(i)&&y<targetY(i)+targetHeight(i))return i;
        return -1;
    }
}
