package br.com.redeakashic.cinematic.core;

/** Exact critically damped spring for a piecewise constant target. */
public final class DampedAxis {
    private final double omega;
    private double position, velocity;
    public DampedAxis(double omega) { this.omega = Numbers.positive(omega, "omega"); }
    public void step(double target, double dt) {
        Numbers.finite(target, "target"); Numbers.finite(dt, "dt");
        if (dt < 0 || dt > 1) { throw new IllegalArgumentException("dt outside [0,1]"); }
        if (dt == 0) { return; }
        double y = position - target;
        double c = velocity + omega * y;
        double decay = Math.exp(-omega * dt);
        position = target + (y + c * dt) * decay;
        velocity = (velocity - omega * c * dt) * decay;
    }
    public void reset(double v) { position = Numbers.finite(v, "position"); velocity = 0; }
    public double position() { return position; }
    public double velocity() { return velocity; }
}
