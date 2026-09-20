package br.com.redeakashic.cinematic.core;

/** Data contract only. The real Forge splash adapter is NOT implemented in this release. */
public final class BootSnapshot {
    public final String stage;
    public final int completed, total;
    public final boolean ready;
    public BootSnapshot(String stage, int completed, int total, boolean ready) {
        if (stage == null || stage.length() > 160 || completed < 0 || total < 0
                || (total > 0 && completed > total) || (total == 0 && completed != 0)) {
            throw new IllegalArgumentException("Invalid boot snapshot");
        }
        this.stage = stage; this.completed = completed; this.total = total; this.ready = ready;
    }
    public boolean determinate() { return total > 0; }
    /** Not an estimate of total loading time. Ratio belongs ONLY to the current stage. */
    public double stageFraction() { return total == 0 ? Double.NaN : (double) completed / total; }
}
