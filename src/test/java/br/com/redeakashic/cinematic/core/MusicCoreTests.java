package br.com.redeakashic.cinematic.core;
public final class MusicCoreTests {
    private static int checks;
    private static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);}
    private static void near(double a,double b,String label){check(Math.abs(a-b)<1e-9,label+" "+a+" vs "+b);}
    public static void main(String[] args){
        MusicFade f=new MusicFade();near(f.sample(0),0,"initial silence");
        f.aim(1,2.5,0);near(f.sample(0),0,"no entry jump");
        near(f.sample(1250000000L),.5,"smoothstep midpoint");
        near(f.sample(2500000000L),1,"fade complete");near(f.sample(5000000000L),1,"no overshoot");
        f.aim(0,.8,5000000000L);near(f.sample(5000000000L),1,"no exit jump");
        near(f.sample(5400000000L),.5,"exit midpoint");near(f.sample(5800000000L),0,"exit complete");
        check(f.silent(5800000000L),"stop only when silent");
        for(int rate:new int[]{20,30,60,144,240}){
            MusicFade q=new MusicFade();q.aim(1,2.5,0);double prev=0;
            for(int i=0;i<=rate*3;i++){
                double v=q.sample(Math.round(i*1e9/rate));check(v>=prev-1e-12&&v>=0&&v<=1,"bounded monotonic");prev=v;
            }
            near(q.sample(2500000000L),1,"time independent end");
            double before=q.sample(3100000000L);q.aim(0,.8,3100000000L);near(q.sample(3100000000L),before,"retarget continuity");
            before=q.sample(3300000000L);q.aim(1,2.5,3300000000L);near(q.sample(3300000000L),before,"cancel-return continuity");
        }
        for(double bad:new double[]{Double.NaN,Double.POSITIVE_INFINITY,-1,2}){
            boolean rejected=false;try{f.aim(bad,1,0);}catch(IllegalArgumentException e){rejected=true;}check(rejected,"invalid target rejected");
        }
        MusicFade instant=new MusicFade();instant.aim(1,0,0);near(instant.sample(0),1,"zero-duration deterministic");
        String[] screens={null,"br.com.redeakashic.cinematic.client.CinematicMenu","net.minecraft.client.gui.GuiOptions","net.minecraft.client.gui.GuiInventory","net.minecraft.client.multiplayer.GuiConnecting","net.minecraft.client.gui.GuiDownloadTerrain"};
        for(String screen:screens){check(!MenuMusicPolicy.frontend(true,true,screen),"world always overrides UI");check(!MenuMusicPolicy.frontend(false,false,screen),"no pre-menu BGM");}
        check(MenuMusicPolicy.frontend(true,false,screens[1]),"main menu eligible");
        check(MenuMusicPolicy.frontend(true,false,screens[2]),"frontend options continuity");
        check(!MenuMusicPolicy.frontend(true,false,screens[4]),"connection fades away");
        check(!MenuMusicPolicy.frontend(true,false,screens[5]),"terrain not frontend");
        for(boolean front:new boolean[]{false,true})for(boolean ready:new boolean[]{false,true})for(boolean category:new boolean[]{false,true}){
            check(MenuMusicPolicy.suppressVanilla(front,ready,"minecraft","music.menu",category)==(front&&ready&&category),"replacement gating");
            check(!MenuMusicPolicy.suppressVanilla(front,ready,"minecraft","gui.button.press",category),"SFX preserved");
            check(!MenuMusicPolicy.suppressVanilla(front,ready,"akashiccinematic","bgm.menu",category),"custom track preserved");
        }
        check(!MenuMusicPolicy.suppressVanilla(true,true,"minecraft",null,true),"null name handled");
        System.out.println("RESULT music_core_checks="+checks+" failures=0");
    }
}
