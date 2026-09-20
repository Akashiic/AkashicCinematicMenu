package br.com.redeakashic.cinematic.core;

/** One transform for layout AND hit testing. Coordinates are Forge-scaled GUI units. */
public final class Viewport {
    public static final double WIDTH = 960, HEIGHT = 540;
    public final double scale, x, y;
    public Viewport(int width, int height) {
        if (width <= 0 || height <= 0) { throw new IllegalArgumentException("Invalid viewport"); }
        scale = Math.min(width / WIDTH, height / HEIGHT);
        x = (width - WIDTH * scale) * 0.5; y = (height - HEIGHT * scale) * 0.5;
    }
    public double localX(double screenX) { return (screenX - x) / scale; }
    public double localY(double screenY) { return (screenY - y) / scale; }
    public double screenX(double localX) { return x + localX * scale; }
    public double screenY(double localY) { return y + localY * scale; }
}
