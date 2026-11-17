package br.com.redeakashic.cinematic.core;
public final class PixelLayoutTests {
    private static int checks;
    private static void check(boolean condition,String label){checks++;if(!condition)throw new AssertionError(label);}
    public static void main(String[] args){
        int[][] sizes={{854,480},{1024,768},{1366,768},{1920,1001},{1920,1080},{2560,1440},{3440,1440},{1600,900},{1920,400},{640,480}};
        for(int[] s:sizes){PixelLayout p=new PixelLayout(s[0],s[1]);check(p.width==s[0]&&p.height==s[1],"pixels unchanged");
            check(Math.abs(p.sidebar-p.width*.27)<=.5,"27 percent anchored left");
            for(int i=0;i<PixelLayout.TARGETS;i++){double x=p.buttonX(),y=p.targetY(i),w=p.buttonWidth(),h=p.targetHeight(i);
                check(x>=0&&x+w<=p.sidebar&&y>=0&&y+h<=p.height,"bounds");
                check(p.hit(x+w/2,y+h/2)==i,"correct target");check(p.hit(x+w,y+h/2)==-1,"exclusive edge");
                check(i==PixelLayout.TARGETS-1||y+h<p.targetY(i+1),"nonoverlap");
                check(i==PixelLayout.DISCORD||p.targetY(i)==p.buttonY(i),"main buttons");}
            check(p.discordHeight()<p.buttonHeight()||p.height<40,"discord compact");
            check(p.hit(Double.NaN,1)==-1,"NaN input");check(p.hit(-1,-1)==-1,"outside input");
        }
        for(int w=640;w<=3840;w+=73)for(int h=480;h<=2160;h+=97){PixelLayout p=new PixelLayout(w,h);check(p.buttonX()+p.buttonWidth()<=p.sidebar,"sweep no overflow");check(p.discordY()+p.discordHeight()<h*.87,"sweep footer safe");}
        boolean index=false;try{new PixelLayout(800,600).buttonY(PixelLayout.MAIN);}catch(IllegalArgumentException ex){index=true;}check(index,"discord is not a main button");
        boolean fail=false;try{new PixelLayout(0,10);}catch(IllegalArgumentException ex){fail=true;}check(fail,"zero width rejected");
        System.out.println("RESULT geometry_checks="+checks+" failures=0");
    }
}
