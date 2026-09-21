package br.com.redeakashic.cinematic.core;
public final class MenuMusicTests {
    private static int checks;private static void check(boolean b,String n){checks++;if(!b)throw new AssertionError(n);}
    public static void main(String[] args){
        long start=1_000_000_000L;MusicFade fade=new MusicFade();fade.aim(1,2.5,start);check(fade.sample(start)==0,"zero at start");
        double prev=0;for(int i=0;i<=1000;i++){double v=fade.sample(start+i*2_500_000L);check(v>=prev&&v<=1,"monotonic bounded entrance");prev=v;}
        check(Math.abs(fade.sample(start+1_250_000_000L)-.5)<1e-9,"smooth midpoint");
        check(fade.sample(start+3_000_000_000L)==1,"exact endpoint");
        long turn=start+3_000_000_000L;fade.aim(0,.8,turn);double before=fade.sample(turn+200_000_000L);fade.aim(1,2.5,turn+200_000_000L);check(Math.abs(fade.sample(turn+200_000_000L)-before)<1e-12,"retarget continuity");
        fade.aim(0,.8,turn+300_000_000L);check(fade.silent(turn+2_000_000_000L),"exit reaches silent");
        for(int rate:new int[]{20,30,60,144}){MusicFade f=new MusicFade();f.aim(1,2.5,start);for(int i=0;i<=rate*3;i++)f.sample(start+(long)(i*1e9/rate));check(f.sample(start+3_000_000_000L)==1,"frame independent");}
        check(!MenuMusicPolicy.frontend(false,false,"net.minecraft.client.gui.GuiOptions"),"not before main");
        check(!MenuMusicPolicy.frontend(true,true,"net.minecraft.client.gui.GuiOptions"),"gameplay wins");
        check(!MenuMusicPolicy.frontend(true,false,null),"no screen no BGM");
        check(MenuMusicPolicy.frontend(true,false,"net.minecraft.client.gui.GuiOptions"),"submenu retains soundtrack");
        for(String n:new String[]{"net.minecraft.client.multiplayer.GuiConnecting","net.minecraft.client.gui.GuiDownloadTerrain","net.minecraft.client.gui.GuiScreenWorking"})check(!MenuMusicPolicy.frontend(true,false,n),"transition exits music");
        check(MenuMusicPolicy.suppressVanilla(true,true,"minecraft","music.menu",true),"menu music replaced");
        check(!MenuMusicPolicy.suppressVanilla(false,true,"minecraft","music.game",true),"world music retained");
        check(!MenuMusicPolicy.suppressVanilla(true,false,"minecraft","music.menu",true),"missing asset fallback");
        check(!MenuMusicPolicy.suppressVanilla(true,true,"akashiccinematic","bgm.menu",true),"own BGM not blocked");
        check(!MenuMusicPolicy.suppressVanilla(true,true,"minecraft","random.click",false),"SFX retained");
        int rejected=0;for(double d:new double[]{Double.NaN,Double.POSITIVE_INFINITY,-.1,1.1})try{fade.aim(d,1,start);}catch(IllegalArgumentException e){rejected++;}check(rejected==4,"invalid gains rejected");
        System.out.println("RESULT music_checks="+checks+" failures=0");
    }
}
