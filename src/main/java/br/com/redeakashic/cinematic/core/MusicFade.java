package br.com.redeakashic.cinematic.core;
/** Time-based bounded smoothstep envelope; retargeting preserves amplitude. */
public final class MusicFade {
    private double from,target,value,duration;private long began;
    public double sample(long now){
        double elapsed=Math.max(0,(now-began)/1.0e9),t=duration<=0?1:Math.min(1,elapsed/duration);
        value=from+(target-from)*(t*t*(3-2*t));return Math.max(0,Math.min(1,value));
    }
    public void aim(double next,double seconds,long now){
        if(!Double.isFinite(next)||!Double.isFinite(seconds)||seconds<0||next<0||next>1)throw new IllegalArgumentException("fade");
        if(next==target)return;from=sample(now);target=next;duration=seconds;began=now;
    }
    public boolean silent(long now){return target==0&&sample(now)<=.00001;}
}
