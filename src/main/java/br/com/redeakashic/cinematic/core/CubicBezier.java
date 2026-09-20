package br.com.redeakashic.cinematic.core;

/** CSS-style cubic bezier: solve x(u)=time, THEN evaluate y(u). */
public final class CubicBezier implements Curve {
    private final double x1, y1, x2, y2;
    public CubicBezier(double x1, double y1, double x2, double y2) {
        this.x1 = Numbers.finite(x1, "x1"); this.x2 = Numbers.finite(x2, "x2");
        this.y1 = Numbers.finite(y1, "y1"); this.y2 = Numbers.finite(y2, "y2");
        if (x1 < 0 || x1 > 1 || x2 < 0 || x2 > 1 || Math.abs(y1) > 10 || Math.abs(y2) > 10) {
            throw new IllegalArgumentException("Bezier controls out of allowed range");
        }
    }
    private static double axis(double u, double a, double b) {
        double v = 1 - u;
        return 3 * v * v * u * a + 3 * v * u * u * b + u * u * u;
    }
    public double sample(double time) {
        double t = Numbers.unit(time);
        if (t == 0 || t == 1) { return t; }
        double low = 0, high = 1;
        for (int i = 0; i < 40; i++) {
            double mid = (low + high) * 0.5;
            if (axis(mid, x1, x2) < t) { low = mid; } else { high = mid; }
        }
        return axis((low + high) * 0.5, y1, y2);
    }
}
