package br.com.redeakashic.cinematic.core;

public enum Easing implements Curve {
    LINEAR { public double sample(double t) { return Numbers.unit(t); } },
    CUBIC_OUT { public double sample(double t) {
        double u = 1 - Numbers.unit(t); return 1 - u * u * u;
    } },
    QUINT_OUT { public double sample(double t) {
        double u = 1 - Numbers.unit(t); return 1 - u * u * u * u * u;
    } },
    SMOOTH { public double sample(double t) {
        double u = Numbers.unit(t); return u * u * (3 - 2 * u);
    } };
}
