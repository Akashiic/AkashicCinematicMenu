package br.com.redeakashic.cinematic.core;

/** Shared numerical contracts. Invalid input is rejected, never propagated as NaN. */
public final class Numbers {
    private Numbers() { }
    public static double finite(double v, String name) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
        return v;
    }
    public static double clamp(double v, double low, double high) {
        finite(v, "value");
        return Math.max(low, Math.min(high, v));
    }
    public static double unit(double v) { return clamp(v, 0.0, 1.0); }
    public static double positive(double v, String name) {
        finite(v, name);
        if (v <= 0) { throw new IllegalArgumentException(name + " must be positive"); }
        return v;
    }
}
