package br.com.redeakashic.cinematic.core;

import static br.com.redeakashic.cinematic.core.BgmPlayback.Context.*;
import static br.com.redeakashic.cinematic.core.BgmPlayback.Phase.*;

public final class BgmPlaybackTests {
    private static int checks, scenarios;
    private static class Engine implements BgmPlayback.Backend {
        boolean available=true, playing, throwStart, throwGain;
        int starts, stops, alive, maxAlive, suppressCalls;
        float gain=-1;
        Object owned;
        public boolean available(){return available;}
        public Object start(float v){if(throwStart)throw new IllegalStateException("synthetic start failure");
            starts++;alive++;maxAlive=Math.max(alive,maxAlive);gain=v;owned=new Object();return owned;}
        public boolean playing(Object voice){check(voice==owned,"engine receives its own handle");return playing;}
        public void gain(Object voice,float v){check(voice==owned,"gain uses owned handle");if(throwGain)throw new IllegalStateException("synthetic gain failure");gain=v;}
        public void stop(Object voice){check(voice==owned,"stop uses owned handle");stops++;alive--;owned=null;playing=false;}
        public void silenceTrackedVanilla(){suppressCalls++;}
    }
    private static void check(boolean condition,String message){checks++;if(!condition)throw new AssertionError(message);}
    private static void near(double a,double b,String message){check(Math.abs(a-b)<1e-6,message+" "+a+" "+b);}
    private static void scenario(String name){scenarios++;System.out.println("SCENARIO "+name);}
    private static BgmPlayback make(Engine e){return new BgmPlayback(e,.4,2.5,.8,3);}
    private static void start(BgmPlayback p,Engine e,long now){p.update(FRONTEND,true,now);e.playing=true;p.update(FRONTEND,true,now+50_000_000L);}
    public static void main(String[] args){
        scenario("no root reached / leave context never starts a track");
        Engine e=new Engine();BgmPlayback p=make(e);
        p.update(LEAVING,true,0);check(e.starts==0&&!p.snapshot().suppressVanilla,"startup untouched");
        scenario("submission is not playback confirmation");
        p.update(FRONTEND,true,100_000_000L);check(e.starts==1&&p.snapshot().phase==STARTING,"one start");
        check(e.gain>0&&e.gain<=.0001f&&!p.snapshot().suppressVanilla,"bootstrap not zero / no premature suppression");
        p.update(FRONTEND,true,300_000_000L);check(e.starts==1&&e.suppressCalls==0,"no duplicate while buffering");
        e.playing=true;p.update(FRONTEND,true,400_000_000L);
        check(p.snapshot().phase==PLAYING&&p.snapshot().suppressVanilla&&e.suppressCalls==1,"confirmation enters playback");
        near(e.gain,0,"fade starts at confirmation");
        p.update(FRONTEND,true,1_650_000_000L);near(e.gain,.2,"2.5s midpoint measured from audible confirmation");
        p.update(FRONTEND,true,2_900_000_000L);near(e.gain,.4,"2.5s complete");
        scenario("frontend submenus retain one playback handle");
        Object voice=e.owned;p.update(FRONTEND,true,3_000_000_000L);check(e.owned==voice&&e.starts==1,"no restart");
        scenario("leaving fades and stops at zero only");
        p.update(LEAVING,true,3_100_000_000L);check(p.snapshot().phase==FADING_OUT&&!p.snapshot().suppressVanilla,"exit releases vanilla policy");
        near(e.gain,.4,"exit continuous");p.update(LEAVING,true,3_500_000_000L);near(e.gain,.2,"exit midpoint");
        p.update(LEAVING,true,3_900_000_000L);check(e.alive==0&&p.snapshot().phase==IDLE,"zero releases sound");
        scenario("quick return reverses fade without duplicate");
        e=new Engine();p=make(e);start(p,e,0);p.update(FRONTEND,true,3_000_000_000L);
        p.update(LEAVING,true,3_100_000_000L);p.update(LEAVING,true,3_500_000_000L);float before=e.gain;
        p.update(FRONTEND,true,3_500_000_000L);near(e.gain,before,"return has no gain jump");check(e.starts==1,"return reuses voice");
        p.update(FRONTEND,true,6_000_000_000L);near(e.gain,.4,"return finishes fade");
        scenario("world wins immediately, even during fade");
        p.update(WORLD_OR_DISABLED,true,6_000_000_000L);check(e.alive==0&&!p.snapshot().suppressVanilla,"no gameplay overlap");
        scenario("mute and unmute; no muting vanilla globally");
        e=new Engine();p=make(e);start(p,e,0);p.update(FRONTEND,true,3_000_000_000L);
        p.update(FRONTEND,false,3_100_000_000L);check(e.alive==0&&p.snapshot().phase==MUTED,"mute stops owned stream");
        p.update(FRONTEND,false,9_000_000_000L);check(e.starts==1,"no looping restart under mute");
        p.update(FRONTEND,true,10_000_000_000L);check(e.starts==2&&p.snapshot().phase==STARTING,"unmute clean reentry");
        scenario("missing resource retains vanilla and does not start");
        e=new Engine();e.available=false;p=make(e);p.update(FRONTEND,true,0);check(e.starts==0&&p.snapshot().phase==MISSING&&!p.snapshot().suppressVanilla,"missing resource fail-open");
        e.available=true;p.update(FRONTEND,true,1_000_000_000L);check(e.starts==1,"resource may become available");
        scenario("source removed mid-playback releases voice");
        e.playing=true;p.update(FRONTEND,true,1_050_000_000L);e.available=false;p.update(FRONTEND,true,1_100_000_000L);
        check(e.alive==0&&!p.snapshot().suppressVanilla,"source loss fail-open");
        scenario("failed playback retries are bounded and backoff enforced");
        e=new Engine();p=make(e);p.update(FRONTEND,true,0);p.update(FRONTEND,true,2_000_000_000L);
        check(p.snapshot().phase==BACKOFF&&e.starts==1&&e.alive==0,"first timeout stopped");
        p.update(FRONTEND,true,2_900_000_000L);check(e.starts==1,"backoff not busy looping");
        p.update(FRONTEND,true,3_000_000_000L);p.update(FRONTEND,true,5_000_000_000L);
        p.update(FRONTEND,true,8_000_000_000L);p.update(FRONTEND,true,10_000_000_000L);
        check(e.starts==3&&p.snapshot().phase==FAILED&&!p.snapshot().suppressVanilla,"three failures stop retries");
        p.update(FRONTEND,false,11_000_000_000L);p.update(WORLD_OR_DISABLED,true,12_000_000_000L);p.update(FRONTEND,true,13_000_000_000L);
        check(e.starts==3&&p.snapshot().phase==FAILED,"mute/context cannot bypass fault latch");
        scenario("reload is explicit recovery with no duplicate voice");
        p.reset(14_000_000_000L);p.update(FRONTEND,true,14_100_000_000L);check(e.starts==3,"reload grace");
        p.update(FRONTEND,true,14_500_000_000L);check(e.starts==4&&p.snapshot().phase==STARTING,"reload re-arms");
        e.playing=true;p.update(FRONTEND,true,14_600_000_000L);p.reset(14_700_000_000L);
        check(e.alive==0&&!p.snapshot().suppressVanilla,"reload stops old generation");
        scenario("start and gain exceptions are isolated to custom BGM");
        e=new Engine();e.throwStart=true;p=make(e);p.update(FRONTEND,true,0);check(p.snapshot().phase==FAILED&&!p.snapshot().suppressVanilla,"start exception fail-open");
        e=new Engine();p=make(e);start(p,e,0);e.throwGain=true;p.update(FRONTEND,true,100_000_000L);
        check(e.alive==0&&p.snapshot().phase==FAILED,"gain exception cleanup");
        scenario("leaving while buffering never starts late music");
        e=new Engine();p=make(e);p.update(FRONTEND,true,0);p.update(LEAVING,true,100_000_000L);
        check(e.alive==0&&e.suppressCalls==0,"pending buffer cancelled");
        scenario("end of healthy playback only restarts once");
        e=new Engine();p=make(e);start(p,e,0);p.update(FRONTEND,true,3_000_000_000L);e.playing=false;
        p.update(FRONTEND,true,10_000_000_000L);check(e.alive==0&&p.snapshot().phase==BACKOFF,"EOF cleanup");
        p.update(FRONTEND,true,10_100_000_000L);check(e.starts==1,"EOF gap");p.update(FRONTEND,true,10_250_000_000L);check(e.starts==2&&e.maxAlive==1,"EOF single restart");
        scenario("timestamp reversal does not reverse the fade");
        e=new Engine();p=make(e);start(p,e,0);p.update(FRONTEND,true,1_300_000_000L);before=e.gain;
        p.update(FRONTEND,true,800_000_000L);near(e.gain,before,"monotonic clock clamp");
        scenario("instant fade parameters and level zero are defined");
        e=new Engine();p=new BgmPlayback(e,.4,0,0,3);start(p,e,0);near(e.gain,.4,"instant entry");p.update(LEAVING,true,51_000_000L);check(e.alive==0,"instant exit");
        e=new Engine();p=new BgmPlayback(e,0,2.5,.8,3);p.update(FRONTEND,true,0);check(e.starts==0,"zero configured volume");
        scenario("loop=false plays once per frontend visit");
        e=new Engine();p=new BgmPlayback(e,.4,2.5,.8,3,false);start(p,e,0);p.update(FRONTEND,true,3_000_000_000L);
        e.playing=false;p.update(FRONTEND,true,4_000_000_000L);p.update(FRONTEND,true,8_000_000_000L);
        check(e.starts==1&&e.alive==0,"no unwanted repeat");
        p.update(LEAVING,true,9_000_000_000L);p.update(FRONTEND,true,10_000_000_000L);check(e.starts==2,"new visit re-arms once");
        scenario("frontend allowlist and world precedence");
        check(BgmFrontendPolicy.frontend(true,false,BgmFrontendPolicy.ROOT),"custom root allowed");
        check(BgmFrontendPolicy.frontend(true,false,"net.minecraft.client.gui.GuiOptions"),"options continuity");
        check(!BgmFrontendPolicy.frontend(true,false,"net.minecraft.client.gui.GuiMainMenu"),"F8 original menu not adopted");
        check(!BgmFrontendPolicy.frontend(true,false,"unknown.mod.gui.CustomScreen"),"unknown screens excluded");
        check(!BgmFrontendPolicy.frontend(false,false,"net.minecraft.client.gui.GuiOptions"),"no music before root");
        check(!BgmFrontendPolicy.frontend(true,true,BgmFrontendPolicy.ROOT),"world overrides even root");
        check(BgmFrontendPolicy.context(true,true,false,"net.minecraft.client.multiplayer.GuiConnecting")==LEAVING,"connect fades");
        check(BgmFrontendPolicy.context(true,true,true,null)==WORLD_OR_DISABLED,"world stops");
        System.out.println("RESULT playback_scenarios="+scenarios+" assertions="+checks+" failures=0");
    }
}
