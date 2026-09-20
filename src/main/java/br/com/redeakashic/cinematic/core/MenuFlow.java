package br.com.redeakashic.cinematic.core;

/** A click can schedule exactly one navigation; render code never owns navigation. */
public final class MenuFlow {
    public enum State { INTRO, READY, EXITING, HANDED_OFF }
    private State state;
    private final double introSeconds, exitSeconds;
    private double age;
    private MenuAction pending;
    public MenuFlow(boolean reducedMotion, boolean firstVisit) {
        introSeconds = reducedMotion ? 0 : (firstVisit ? 0.90 : 0.22);
        exitSeconds = reducedMotion ? 0 : 0.22;
        state = introSeconds == 0 ? State.READY : State.INTRO;
    }
    public void advance(double dt) {
        Numbers.finite(dt, "dt");
        if (dt < 0 || dt > 1) { throw new IllegalArgumentException("dt outside [0,1]"); }
        age += dt;
        if (state == State.INTRO && age + 1.0e-9 >= introSeconds) {
            state = State.READY; age = 0;
        }
    }
    public boolean request(MenuAction action) {
        if (action == null || state != State.READY) { return false; }
        pending = action; state = State.EXITING; age = 0; return true;
    }
    public void escape() {
        if (state == State.INTRO || state == State.EXITING) {
            state = State.READY; pending = null; age = 0;
        }
    }
    public MenuAction consume() {
        if (state != State.EXITING || age + 1.0e-9 < exitSeconds) { return null; }
        MenuAction result = pending; pending = null; state = State.HANDED_OFF;
        return result;
    }
    public double visibility() {
        if (state == State.HANDED_OFF) { return 0; }
        if (state == State.INTRO) { return Easing.CUBIC_OUT.sample(age / introSeconds); }
        if (state == State.EXITING) {
            return exitSeconds == 0 ? 0 : 1 - Easing.SMOOTH.sample(age / exitSeconds);
        }
        return 1;
    }
    public double buttonVisibility(int index) {
        if (index < 0 || index >= MenuLayout.COUNT) { throw new IllegalArgumentException("index"); }
        if (state != State.INTRO) { return visibility(); }
        return Easing.CUBIC_OUT.sample((age - index * 0.055) / Math.max(0.01, introSeconds - 0.22));
    }
    public State state() { return state; }
}
