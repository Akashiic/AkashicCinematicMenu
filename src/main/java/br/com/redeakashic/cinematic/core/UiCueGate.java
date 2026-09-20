package br.com.redeakashic.cinematic.core;
/** Monotonic debounce. No tick-rate assumption, catch-up queue or ambient loop. */
public final class UiCueGate {
    public enum Cue { FOCUS, SELECT, BACK, ENTER, CONNECT }
    private final long[] last=new long[Cue.values().length];
    private final boolean[] used=new boolean[last.length];
    private boolean actionUsed;private long lastAction;
    public boolean allow(Cue cue,long now){
        if(cue==null)throw new IllegalArgumentException("cue");
        long spacing=cue==Cue.FOCUS?110_000_000L:cue==Cue.ENTER?2_000_000_000L:100_000_000L;
        int i=cue.ordinal();
        if(used[i]&&now-last[i]<spacing)return false;
        if(cue==Cue.FOCUS&&actionUsed&&now-lastAction<160_000_000L)return false;
        used[i]=true;last[i]=now;if(cue!=Cue.FOCUS){actionUsed=true;lastAction=now;}
        return true;
    }
}
