package br.com.redeakashic.cinematic.core;

/** Immutable analytical track: repeated samples have no side effects. */
public final class Tween {
    private final double from, to, start, duration;
    private final Curve curve;
    public Tween(double from, double to, double start, double duration, Curve curve) {
        this.from = Numbers.finite(from, "from"); this.to = Numbers.finite(to, "to");
        this.start = Numbers.finite(start, "start");
        this.duration = Numbers.finite(duration, "duration");
        if (start < 0 || duration < 0 || curve == null) {
            throw new IllegalArgumentException("Invalid tween");
        }
        this.curve = curve;
    }
    public double at(double seconds) {
        Numbers.finite(seconds, "seconds");
        if (seconds < start) { return from; }
        if (duration == 0 || seconds >= start + duration) { return to; }
        return from + (to - from) * curve.sample((seconds - start) / duration);
    }
}
