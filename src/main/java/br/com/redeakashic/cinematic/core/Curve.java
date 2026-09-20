package br.com.redeakashic.cinematic.core;

/** Pure easing curve; input is normalized elapsed time. */
public interface Curve { double sample(double t); }
