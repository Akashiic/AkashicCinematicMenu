package br.com.redeakashic.cinematic.core;

public final class MenuLayout {
    public static final int COUNT = MenuAction.values().length;
    public static final double X = 74, Y = 236, W = 278, H = 35, GAP = 9;
    private MenuLayout() { }
    public static double top(int index) {
        if (index < 0 || index >= COUNT) { throw new IllegalArgumentException("index"); }
        return Y + index * (H + GAP);
    }
    public static int hit(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) { return -1; }
        for (int i = 0; i < COUNT; i++) {
            double top = top(i);
            if (x >= X && x < X + W && y >= top && y < top + H) { return i; }
        }
        return -1;
    }
    public static int nextFocus(int index, int delta) {
        int v = (index + delta) % COUNT;
        return v < 0 ? v + COUNT : v;
    }
    public static MenuAction action(int index) {
        if (index < 0 || index >= COUNT) { throw new IllegalArgumentException("index"); }
        return MenuAction.values()[index];
    }
}
