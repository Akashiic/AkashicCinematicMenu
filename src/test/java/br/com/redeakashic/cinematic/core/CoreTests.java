package br.com.redeakashic.cinematic.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** Dependency-free regression suite. Each named scenario is reported individually. */
public final class CoreTests {
    private static final List<String> passed = new ArrayList<String>();
    private static int assertions;
    private CoreTests() { }
    private static void check(boolean v) { assertions++; if (!v) { throw new AssertionError("assertion " + assertions); } }
    private static void near(double a, double b, double eps) { check(Math.abs(a - b) <= eps); }
    private static void bad(Runnable r) {
        boolean rejected = false;
        try { r.run(); } catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected);
    }
    private static void test(String name, Runnable r) { r.run(); passed.add(name); System.out.println("PASS " + name); }
    public static void main(String[] args) {
        test("easing endpoints and clamps", () -> {
            for (Easing c : Easing.values()) { near(c.sample(-1),0,0); near(c.sample(0),0,0); near(c.sample(1),1,0); near(c.sample(2),1,0); }
        });
        test("easing range and monotonicity", () -> {
            for (Easing c : Easing.values()) { double previous = -1; for (int i=0;i<=1000;i++) {
                double v = c.sample(i/1000.0); check(v>=previous && v>=0 && v<=1); previous=v;
            } }
        });
        test("easing rejects nonfinite inputs", () -> { for (Easing c : Easing.values()) { bad(() -> c.sample(Double.NaN)); bad(() -> c.sample(Double.POSITIVE_INFINITY)); } });
        test("bezier linear inversion", () -> { Curve c=new CubicBezier(0,0,1,1); for(int i=0;i<=100;i++) {near(c.sample(i/100.0),i/100.0,1e-9);} });
        test("bezier premium curve remains bounded", () -> { Curve c=new CubicBezier(.16,1,.3,1); double previous=0; for(int i=0;i<=1000;i++){ double v=c.sample(i/1000.0);check(v>=previous && v<=1);previous=v; } });
        test("bezier rejects invalid controls", () -> { bad(()->new CubicBezier(-.1,0,.3,1)); bad(()->new CubicBezier(0,0,1.1,1)); bad(()->new CubicBezier(0,Double.NaN,1,1)); bad(()->new CubicBezier(0,11,1,1)); });
        test("tween delay and interpolation", () -> { Tween t=new Tween(2,6,.5,1,Easing.LINEAR);near(t.at(0),2,0);near(t.at(.5),2,0);near(t.at(1),4,1e-9);near(t.at(1.5),6,0);near(t.at(9),6,0); });
        test("tween zero duration", () -> { Tween t=new Tween(0,1,.4,0,Easing.LINEAR);near(t.at(.39),0,0);near(t.at(.4),1,0); });
        test("tween repeated sampling is pure", () -> { Tween t=new Tween(0,1,0,1,Easing.CUBIC_OUT);double v=t.at(.5);t.at(1);near(t.at(.5),v,0); });
        test("tween rejects malformed tracks", () -> { bad(()->new Tween(0,1,0,-1,Easing.LINEAR));bad(()->new Tween(0,1,-1,1,Easing.LINEAR));bad(()->new Tween(0,1,0,1,null));bad(()->new Tween(Double.NaN,1,0,1,Easing.LINEAR)); });
        test("clock starts without fabricated delta", () -> { AtomicLong n=new AtomicLong(100);FrameClock c=new FrameClock(n::get);near(c.tick(true),0,0);near(c.elapsed(),0,0); });
        test("clock uses nanoseconds", () -> { AtomicLong n=new AtomicLong();FrameClock c=new FrameClock(n::get);c.tick(true);n.addAndGet(16666667);near(c.tick(true),.016666667,1e-10); });
        test("clock long stall clamp", () -> { AtomicLong n=new AtomicLong();FrameClock c=new FrameClock(n::get);c.tick(true);n.addAndGet(5000000000L);near(c.tick(true),.1,1e-12); });
        test("clock loses focus without catchup", () -> { AtomicLong n=new AtomicLong();FrameClock c=new FrameClock(n::get);c.tick(true);n.addAndGet(100000000);near(c.tick(false),0,0);n.addAndGet(10000000000L);near(c.tick(true),0,0);n.addAndGet(20000000);near(c.tick(true),.02,1e-12); });
        test("clock resync preserves scene time", () -> { AtomicLong n=new AtomicLong();FrameClock c=new FrameClock(n::get);c.tick(true);n.addAndGet(100000000);c.tick(true);c.resync();n.addAndGet(900000000);near(c.tick(true),0,0);near(c.elapsed(),.1,1e-12); });
        test("clock nonmonotonic source cannot rewind", () -> { AtomicLong n=new AtomicLong(100);FrameClock c=new FrameClock(n::get);c.tick(true);n.set(0);near(c.tick(true),0,0); });
        test("spring 30 60 144 FPS equivalence", () -> { double[] positions=new double[3],velocities=new double[3];int[] fps={30,60,144};for(int j=0;j<fps.length;j++){DampedAxis a=new DampedAxis(10);for(int i=0;i<fps[j];i++){a.step(1,1.0/fps[j]);}positions[j]=a.position();velocities[j]=a.velocity();}near(positions[0],positions[1],1e-12);near(positions[1],positions[2],1e-12);near(velocities[0],velocities[2],1e-10); });
        test("spring stationary target convergence", () -> { DampedAxis a=new DampedAxis(10);double last=0;for(int i=0;i<600;i++){a.step(1,1.0/60);check(a.position()>=last && a.position()<=1.000000001);last=a.position();}near(a.position(),1,1e-10); });
        test("spring changing direction stays finite", () -> { DampedAxis a=new DampedAxis(12);for(int i=0;i<10000;i++){a.step(i%31<15 ? -1:1,1.0/144);check(Double.isFinite(a.position()) && Double.isFinite(a.velocity()));} });
        test("spring rejects invalid dt and target", () -> { DampedAxis a=new DampedAxis(10);bad(()->a.step(1,-1));bad(()->a.step(Double.NaN,.1));bad(()->a.step(1,2));bad(()->new DampedAxis(0)); });
        test("spring reset and zero delta", () -> { DampedAxis a=new DampedAxis(10);a.reset(.3);a.step(1,0);near(a.position(),.3,0);near(a.velocity(),0,0); });
        test("viewport inverse across resolutions", () -> {int[][] sizes={{320,240},{854,480},{1280,720},{1920,1080},{2560,1080},{3840,2160},{360,800}};for(int[] s:sizes){Viewport v=new Viewport(s[0],s[1]);for(int i=0;i<100;i++){near(v.localX(v.screenX(i*9.0)),i*9.0,1e-9);near(v.localY(v.screenY(i*5.0)),i*5.0,1e-9);}} });
        test("viewport rejects zero or negative dimensions", () -> {bad(()->new Viewport(0,1));bad(()->new Viewport(1,-1));});
        test("button hitboxes centers and right edge", () -> {for(int i=0;i<MenuLayout.COUNT;i++){check(MenuLayout.hit(MenuLayout.X+10,MenuLayout.top(i)+10)==i);check(MenuLayout.hit(MenuLayout.X+MenuLayout.W,MenuLayout.top(i)+10)==-1);}});
        test("button spacing has no overlapping targets", () -> {for(int i=0;i<MenuLayout.COUNT;i++){check(MenuLayout.hit(MenuLayout.X+10,MenuLayout.top(i)+MenuLayout.H+1)==-1);}});
        test("button nonfinite coordinates rejected", () -> {check(MenuLayout.hit(Double.NaN,0)==-1);check(MenuLayout.hit(100,Double.POSITIVE_INFINITY)==-1);});
        test("focus wraps in both directions", () -> {check(MenuLayout.nextFocus(4,1)==0);check(MenuLayout.nextFocus(0,-1)==4);check(MenuLayout.nextFocus(2,1)==3);});
        test("action whitelist has five routes", () -> {for(int i=0;i<5;i++){check(MenuLayout.action(i)!=null);}bad(()->MenuLayout.action(5));});
        test("flow ignores clicks during intro", () -> {MenuFlow f=new MenuFlow(false,true);check(!f.request(MenuAction.QUIT));check(f.consume()==null);});
        test("flow becomes ready after intro", () -> {MenuFlow f=new MenuFlow(false,true);for(int i=0;i<54;i++){f.advance(1.0/60);}check(f.state()==MenuFlow.State.READY);near(f.visibility(),1,0);});
        test("flow duplicate click protection", () -> {MenuFlow f=new MenuFlow(true,true);check(f.request(MenuAction.SERVERS));check(!f.request(MenuAction.QUIT));check(f.consume()==MenuAction.SERVERS);check(f.consume()==null);check(!f.request(MenuAction.MODS));});
        test("flow cancel transition prevents action", () -> {MenuFlow f=new MenuFlow(false,true);f.escape();f.request(MenuAction.SERVERS);f.advance(.1);f.escape();f.advance(.5);check(f.consume()==null);check(f.state()==MenuFlow.State.READY);});
        test("flow escape skips intro", () -> {MenuFlow f=new MenuFlow(false,true);f.escape();check(f.state()==MenuFlow.State.READY);near(f.visibility(),1,0);});
        test("flow waits for exit before handoff", () -> {MenuFlow f=new MenuFlow(false,true);f.escape();f.request(MenuAction.OPTIONS);f.advance(.1);check(f.consume()==null);f.advance(.12);check(f.consume()==MenuAction.OPTIONS);check(f.consume()==null);});
        test("flow reduced motion skips decorative waits", () -> {MenuFlow f=new MenuFlow(true,true);check(f.state()==MenuFlow.State.READY);check(f.request(MenuAction.MODS));check(f.consume()==MenuAction.MODS);});
        test("flow stagger preserves bounds", () -> {MenuFlow f=new MenuFlow(false,true);for(int frame=0;frame<60;frame++){for(int i=0;i<5;i++){double a=f.buttonVisibility(i);check(a>=0&&a<=1);}f.advance(1.0/60);}});
        test("flow returns to existing menu with short intro", () -> {MenuFlow f=new MenuFlow(false,false);f.advance(.22);check(f.state()==MenuFlow.State.READY);});
        test("flow invalid delta rejected", () -> {MenuFlow f=new MenuFlow(false,true);bad(()->f.advance(Double.NaN));bad(()->f.advance(-1));bad(()->f.advance(1.1));});
        test("policy exact vanilla only", () -> {check(ScreenPolicy.accepts("net.minecraft.client.gui.GuiMainMenu",false));check(!ScreenPolicy.accepts("net.minecraft.client.gui.GuiMainMenu$Error",true));});
        test("policy supports only explicit DummyCore classes", () -> {check(ScreenPolicy.accepts("DummyCore.Client.GuiMainMenuVanilla",true));check(ScreenPolicy.accepts("DummyCore.Client.GuiMainMenuOld",true));check(!ScreenPolicy.accepts("DummyCore.Client.GuiMainMenuOld",false));});
        test("policy leaves errors containers and disconnects alone", () -> {for(String s:new String[]{null,"","net.minecraft.client.gui.GuiDisconnected","cpw.mods.fml.client.GuiModMissing","net.minecraft.client.gui.inventory.GuiInventory","other.Menu","br.com.redeakashic.cinematic.client.CinematicMenu"}){check(!ScreenPolicy.accepts(s,true));}});
        test("boot unknown total is indeterminate", () -> {BootSnapshot b=new BootSnapshot("Discovering mods",0,0,false);check(!b.determinate());check(Double.isNaN(b.stageFraction()));check(!b.ready);});
        test("boot ratio is stage scoped not readiness", () -> {BootSnapshot b=new BootSnapshot("Stage",10,10,false);near(b.stageFraction(),1,0);check(!b.ready);});
        test("boot malformed snapshots rejected", () -> {bad(()->new BootSnapshot(null,0,0,false));bad(()->new BootSnapshot("x",1,0,false));bad(()->new BootSnapshot("x",11,10,false));bad(()->new BootSnapshot("x",-1,10,false));});
        System.out.println("RESULT scenarios="+passed.size()+" assertions="+assertions+" failures=0");
    }
}
