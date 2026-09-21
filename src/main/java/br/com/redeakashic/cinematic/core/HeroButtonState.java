package br.com.redeakashic.cinematic.core;
/** No action dispatch here: visual animation cannot trigger, delay or duplicate a connection. */
public final class HeroButtonState {
    private HeroButtonState(){}
    public static double clamp(double x){return Double.isFinite(x)?Math.max(0,Math.min(1,x)):0;}
    public static double glow(double seconds,double hover,boolean motion){
        double t=Double.isFinite(seconds)?Math.max(0,seconds):0;
        return .20+.31*clamp(hover)+(motion?.065*(1+Math.sin(t*2*Math.PI/5.6)):0);
    }
    public static double shimmer(double seconds,boolean motion){
        if(!motion||!Double.isFinite(seconds)||seconds<0)return -2;
        double phase=seconds%6.8;return phase<=1.35?-0.22+1.44*phase/1.35:-2;
    }
    public static double press(double elapsed,boolean motion){
        if(!motion||!Double.isFinite(elapsed)||elapsed<0||elapsed>=.22)return 0;
        return Math.sin(Math.PI*elapsed/.22);
    }
}
