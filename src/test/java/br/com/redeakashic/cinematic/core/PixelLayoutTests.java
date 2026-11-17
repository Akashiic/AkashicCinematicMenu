package br.com.redeakashic.cinematic.core;
public final class PixelLayoutTests {
    private static int checks;
    private static void check(boolean condition,String label){checks++;if(!condition)throw new AssertionError(label);}
    public static void main(String[] args){
        int[][] sizes={{854,480},{1024,768},{1366,768},{1920,1001},{1920,1080},{2560,1440},{3440,1440},{1600,900},{1920,400},{640,480}};
        for(int[] s:sizes){PixelLayout p=new PixelLayout(s[0],s[1]);check(p.width==s[0]&&p.height==s[1],"pixels unchanged");
            check(Math.abs(p.sidebar-p.width*.27)<=.5,"27 percent anchored left");
            for(int i=0;i<PixelLayout.TARGETS;i++){double x=p.targetX(i),y=p.targetY(i),w=p.targetWidth(i),h=p.targetHeight(i);
                check(x>=0&&x+w<=p.sidebar&&y>=0&&y+h<=p.height,"bounds");
                check(p.hit(x+w/2,y+h/2)==i,"correct target");
                boolean single=i==MenuAction.SINGLEPLAYER.ordinal();
                check(p.hit(x+w,y+h/2)==-1,"exclusive edge");
                check(i==PixelLayout.TARGETS-1||single||y+h<p.targetY(i+1),"nonoverlap between rows");
                check(i==PixelLayout.DISCORD||p.targetY(i)==p.buttonY(i),"main buttons");}
            int sp=MenuAction.SINGLEPLAYER.ordinal(),mp=MenuAction.MULTIPLAYER.ordinal();
            check(p.targetY(sp)==p.targetY(mp)&&p.targetHeight(sp)==p.targetHeight(mp),"single and multi share a row");
            check(p.targetX(sp)==p.buttonX()&&p.targetX(mp)+p.targetWidth(mp)==p.buttonX()+p.buttonWidth(),"halves aligned to the container edges");
            check(p.targetWidth(sp)==p.targetWidth(mp)&&p.targetX(sp)+p.targetWidth(sp)<p.targetX(mp),"equal halves with a gap");
            check(p.hit(p.targetX(sp)+p.targetWidth(sp)+1,p.targetY(sp)+2)==-1||p.splitGap()<2,"gap between halves is not a target");
            check(p.discordHeight()<p.buttonHeight()||p.height<40,"discord compact");
            double above=p.buttonY(0)-p.groupUpperBound(),below=p.discordY()-(p.buttonY(PixelLayout.MAIN-1)+p.buttonHeight(PixelLayout.MAIN-1));
            check(Math.abs(above-below)<=1.5,"group centered in the free space "+s[0]+"x"+s[1]+" "+above+"/"+below);
            check(above>=p.height*.02,"breathing room above the group");
            check(p.groupUpperBound()==(p.height>=600?Math.round(p.height*.350):p.headerRuleY()),"upper bound: tagline end or header rule");
            check(p.heroHeight()>p.buttonHeight()*1.25||p.height<60,"Jogar taller");
            double underJogar=p.buttonY(1)-(p.buttonY(0)+p.heroHeight()),between=p.buttonY(3)-(p.buttonY(1)+p.buttonHeight());
            check(underJogar>=between*2||p.height<200,"larger gap under Jogar "+underJogar+"/"+between);
            for(int k=2;k<PixelLayout.MAIN-1;k++)check(Math.abs(p.buttonY(k+1)-(p.buttonY(k)+p.buttonHeight())-between)<1e-9,"regular gaps equal");
            check(PixelLayout.rowOf(1)==PixelLayout.rowOf(2)&&PixelLayout.rowOf(4)==PixelLayout.ROWS-1,"four rows");
            check(Math.abs(p.groupHeight()-(p.buttonY(PixelLayout.MAIN-1)+p.buttonHeight(PixelLayout.MAIN-1)-p.buttonY(0)))<1e-9,"group height consistent");
            check(p.hit(Double.NaN,1)==-1,"NaN input");check(p.hit(-1,-1)==-1,"outside input");
        }
        for(int w=640;w<=3840;w+=73)for(int h=480;h<=2160;h+=97){PixelLayout p=new PixelLayout(w,h);check(p.buttonX()+p.buttonWidth()<=p.sidebar,"sweep no overflow");check(p.discordY()+p.discordHeight()<h*.87,"sweep footer safe");}
        boolean index=false;try{new PixelLayout(800,600).buttonY(PixelLayout.MAIN);}catch(IllegalArgumentException ex){index=true;}check(index,"discord is not a main button");
        boolean fail=false;try{new PixelLayout(0,10);}catch(IllegalArgumentException ex){fail=true;}check(fail,"zero width rejected");
        System.out.println("RESULT geometry_checks="+checks+" failures=0");
    }
}
