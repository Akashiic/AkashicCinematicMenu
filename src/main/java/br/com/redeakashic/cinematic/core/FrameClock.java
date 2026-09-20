package br.com.redeakashic.cinematic.core;

import java.util.function.LongSupplier;

/** Monotonic, focus-aware clock. Long stalls are clamped; no background catch-up. */
public final class FrameClock {
    private final LongSupplier source;
    private boolean initialized, wasActive;
    private long previous;
    private double elapsed;
    public FrameClock(LongSupplier source) {
        if (source == null) { throw new IllegalArgumentException("source is required"); }
        this.source = source;
    }
    public double tick(boolean active) {
        long now = source.getAsLong();
        if (!initialized) {
            initialized = true; previous = now; wasActive = active; return 0;
        }
        long nanos = now - previous;
        previous = now;
        boolean advance = active && wasActive;
        wasActive = active;
        double dt = advance ? Math.max(0, Math.min(0.100, nanos * 1.0e-9)) : 0;
        elapsed += dt;
        return dt;
    }
    public void resync() { initialized = false; }
    public double elapsed() { return elapsed; }
}
