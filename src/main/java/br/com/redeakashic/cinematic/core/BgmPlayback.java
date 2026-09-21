package br.com.redeakashic.cinematic.core;

/**
 * Engine-independent, single-owner BGM lifecycle.
 * All calls except reading the immutable snapshot occur on the client thread.
 * An engine accepting start() is not proof of playback: confirmation is required.
 */
public final class BgmPlayback {
    public interface Backend {
        boolean available();
        Object start(float bootstrapGain);
        boolean playing(Object voice);
        void gain(Object voice, float value);
        void stop(Object voice);
        void silenceTrackedVanilla();
    }
    public enum Phase { IDLE, MISSING, MUTED, STARTING, PLAYING, FADING_OUT, BACKOFF, FAILED }
    public enum Context { FRONTEND, LEAVING, WORLD_OR_DISABLED }
    public static final class Snapshot {
        public final Phase phase;
        public final double gain;
        public final int starts, stops, failedStarts;
        public final boolean ownsVoice, suppressVanilla;
        private Snapshot(Phase p,double g,int starts,int stops,int failedStarts,boolean owns,boolean suppress){
            phase=p;gain=g;this.starts=starts;this.stops=stops;this.failedStarts=failedStarts;
            ownsVoice=owns;suppressVanilla=suppress;
        }
    }
    private static final long START_GRACE=2_000_000_000L, HEALTHY_AFTER=2_000_000_000L;
    private final Backend backend;
    private final double level, fadeInSeconds, fadeOutSeconds;
    private final int maxConsecutiveFailures;
    private MusicFade envelope = new MusicFade();
    private Object voice;
    private Phase phase=Phase.IDLE;
    private long requestedAt, confirmedAt, retryAt, lastNow;
    private boolean clockSeen, faulted, completedOnce;
    private final boolean loop;
    private int starts, stops, failures;
    private volatile Snapshot snapshot=new Snapshot(Phase.IDLE,0,0,0,0,false,false);

    public BgmPlayback(Backend backend,double level,double fadeIn,double fadeOut,int maxFailures){
        this(backend,level,fadeIn,fadeOut,maxFailures,true);
    }
    public BgmPlayback(Backend backend,double level,double fadeIn,double fadeOut,int maxFailures,boolean loop){
        if(backend==null || !Double.isFinite(level)||level<0||level>1 ||
           !Double.isFinite(fadeIn)||fadeIn<0||fadeIn>30 ||
           !Double.isFinite(fadeOut)||fadeOut<0||fadeOut>10 || maxFailures<1||maxFailures>10)
            throw new IllegalArgumentException("Invalid BGM configuration");
        this.backend=backend;this.loop=loop;this.level=level;fadeInSeconds=fadeIn;fadeOutSeconds=fadeOut;
        maxConsecutiveFailures=maxFailures;
    }
    public Snapshot snapshot(){return snapshot;}
    private long monotonic(long now){if(clockSeen&&now<lastNow)return lastNow;clockSeen=true;return lastNow=now;}
    private void publish(double gain,boolean frontend){
        boolean suppress=frontend&&voice!=null&&(phase==Phase.PLAYING||phase==Phase.FADING_OUT);
        snapshot=new Snapshot(phase,Math.max(0,Math.min(1,gain)),starts,stops,failures,voice!=null,suppress);
    }
    private void release(){
        Object owned=voice;voice=null;
        try {if(owned!=null){backend.stop(owned);stops++;}}
        finally{envelope=new MusicFade();}
    }
    private void failedStart(long now){
        release();failures++;
        if(failures>=maxConsecutiveFailures){phase=Phase.FAILED;faulted=true;return;}
        retryAt=now+(failures==1?1_000_000_000L:3_000_000_000L);phase=Phase.BACKOFF;
    }
    /** Engine/resource reload is an explicit recovery boundary, not a per-frame retry. */
    public void reset(long now){
        now=monotonic(now);
        try{release();}finally{failures=0;faulted=false;completedOnce=false;phase=Phase.IDLE;retryAt=now+500_000_000L;publish(0,false);}
    }
    public void update(Context context,boolean audible,long now){
        if(context==null)throw new IllegalArgumentException("context");
        now=monotonic(now);boolean front=context==Context.FRONTEND;
        if(!front)completedOnce=false;
        try {
            // A world or disabled feature always wins, even when entry/fade is pending.
            if(context==Context.WORLD_OR_DISABLED||level==0){release();phase=Phase.IDLE;publish(0,false);return;}
            if(faulted){phase=Phase.FAILED;faulted=true;publish(0,false);return;}
            if(!audible){release();phase=Phase.MUTED;publish(0,false);return;}
            if(!backend.available()){release();phase=Phase.MISSING;publish(0,false);return;}
            if(phase==Phase.FAILED){publish(0,false);return;}
            if(voice==null){
                if(!front){phase=Phase.IDLE;publish(0,false);return;}
                if(completedOnce){phase=Phase.IDLE;publish(0,false);return;}
                if(now<retryAt){phase=Phase.BACKOFF;publish(0,false);return;}
                envelope=new MusicFade();
                // Vanilla's audio manager can reject a zero-volume submission.
                voice=backend.start(0.0001f);requestedAt=now;starts++;phase=Phase.STARTING;
                if(voice==null)failedStart(now);
                publish(0,false);return;
            }
            boolean playing=backend.playing(voice);
            if(phase==Phase.STARTING){
                if(!front){release();phase=Phase.IDLE;publish(0,false);return;}
                if(!playing){
                    if(now-requestedAt>=START_GRACE)failedStart(now);
                    publish(0,false);return;
                }
                confirmedAt=now;phase=Phase.PLAYING;
                envelope.aim(level,fadeInSeconds,now);
                backend.silenceTrackedVanilla();
            } else if(!playing){
                if(!front){release();phase=Phase.IDLE;publish(0,false);return;}
                if(now-confirmedAt<HEALTHY_AFTER){failedStart(now);publish(0,false);return;}
                // Healthy stream ended/stopped: bounded re-entry; one owner at all times.
                if(!loop){release();completedOnce=true;phase=Phase.IDLE;publish(0,false);return;}
                release();failures=0;retryAt=now+250_000_000L;phase=Phase.BACKOFF;publish(0,false);return;
            }
            if(now-confirmedAt>=HEALTHY_AFTER)failures=0;
            if(front){
                if(phase==Phase.FADING_OUT){envelope.aim(level,fadeInSeconds,now);phase=Phase.PLAYING;}
            }else if(phase!=Phase.FADING_OUT){envelope.aim(0,fadeOutSeconds,now);phase=Phase.FADING_OUT;}
            double volume=envelope.sample(now);
            if(!front&&envelope.silent(now)){release();phase=Phase.IDLE;publish(0,false);return;}
            backend.gain(voice,(float)volume);publish(volume,front);
        }catch(RuntimeException ex){
            try{release();}catch(RuntimeException ignored){/* Fail closed only for our voice. */}
            phase=Phase.FAILED;faulted=true;publish(0,false);
        }
    }
}
