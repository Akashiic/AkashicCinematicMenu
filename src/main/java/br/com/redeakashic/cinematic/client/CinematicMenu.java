package br.com.redeakashic.cinematic.client;
import br.com.redeakashic.cinematic.core.*;
import br.com.redeakashic.cinematic.render.*;
import br.com.redeakashic.cinematic.bootstrap.SplashHooks;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
/** Forge 1.7.10 adapter compiled against actual SRG classes, not generated stubs. */
public final class CinematicMenu extends GuiScreen implements GuiYesNoCallback {
    private final GuiScreen original;
    private final MenuHook hook;
    private final MenuConfig config;
    private final FrameClock clock = new FrameClock(System::nanoTime);
    private final DampedAxis mouseX = new DampedAxis(10), mouseY = new DampedAxis(10);
    private final DampedAxis[] highlights = new DampedAxis[MenuLayout.COUNT];
    private MenuFlow flow;
    private NativeVisuals visuals;
    private NativeLayout layout;
    private double visitAge;
    private static boolean handoffSeen;
    private boolean fadeFromBoot;
    private final double[] highlightValues=new double[MenuLayout.COUNT];
    private boolean reduced, keyboardMode = true;
    private int audioSelection=-2;
    private int focus, previousMouseX = Integer.MIN_VALUE, previousMouseY = Integer.MIN_VALUE;
    CinematicMenu(GuiScreen original, MenuHook hook, MenuConfig config) {
        this.original = original; this.hook = hook; this.config = config; this.reduced = config.reducedMotion;
        for (int i = 0; i < highlights.length; i++) { highlights[i] = new DampedAxis(18); }
    }
    @Override public void func_73866_w_() {
        // Upload only under RawScope, never in init/worker callbacks.
        flow = new MenuFlow(reduced, false); flow.escape(); visitAge=0; clock.resync();
        fadeFromBoot=!handoffSeen&&SplashHooks.finished&&SplashHooks.rendered; if(fadeFromBoot)handoffSeen=true;
    }
    @Override @SuppressWarnings("try")
    public void func_73863_a(int mx, int my, float partialTicks) {
        int w=Display.getWidth(),h=Display.getHeight(); if(w<1||h<1)return;
        try {
            double dt=clock.tick(Display.isActive());visitAge+=dt;
            if(!Display.isActive())flow.escape();
            flow.advance(dt);
            if(previousMouseX!=Integer.MIN_VALUE&&(mx!=previousMouseX||my!=previousMouseY))keyboardMode=false;
            previousMouseX=mx;previousMouseY=my;
            try(RawScope scope=new RawScope(w,h)) {
                String profile=NativeLayout.choose(w,h);
                if(visuals==null||!visuals.layout.profile.equals(profile)) {
                    if(visuals!=null)visuals.close();visuals=new NativeVisuals(profile);layout=visuals.layout;
                }
                double px=mx*(double)w/Math.max(1,field_146294_l),py=my*(double)h/Math.max(1,field_146295_m);
                int hovered=layout.hit(layout.localX(px,w,h),layout.localY(py,w,h));
                if(!keyboardMode&&hovered>=0)focus=hovered;
                int selected=keyboardMode?focus:hovered;
                if(selected!=audioSelection){if(selected>=0&&audioSelection!=-2)UiAudio.play(br.com.redeakashic.cinematic.core.UiCueGate.Cue.FOCUS);audioSelection=selected;}
                if(reduced){mouseX.reset(0);mouseY.reset(0);}
                else{mouseX.step(Numbers.clamp(px/w*2-1,-1,1),dt);mouseY.step(Numbers.clamp(py/h*2-1,-1,1),dt);}
                double[] values=highlightValues;
                for(int i=0;i<values.length;i++) {
                    double target=i==selected?1:0;if(reduced)highlights[i].reset(target);else highlights[i].step(target,dt);
                    values[i]=highlights[i].position();
                }
                visuals.transform(w,h);
                visuals.menu(clock.elapsed(),mouseX.position()*config.parallax,mouseY.position()*config.parallax,flow.visibility(),values,!reduced,config.particles);
                // Only actual splash completion allows the handoff; this does not hold up loading.
                if(fadeFromBoot&&!reduced&&visitAge<.45)visuals.startup(SplashHooks.last,SplashHooks.elapsedSeconds,1-visitAge/.45,false);
                else if(fadeFromBoot){visuals.releaseBoot();fadeFromBoot=false;}
            }
            if(visitAge>.1)UiAudio.greet();
            MenuAction next=flow.consume();if(next!=null)dispatch(next);
        }catch(RuntimeException ex){dispose();hook.fallback(field_146297_k,original,ex);}
        catch(LinkageError ex){dispose();hook.fallback(field_146297_k,original,ex);}
    }
    @SuppressWarnings("try") private void dispose(){
        if(visuals==null)return;
        try(RawScope scope=new RawScope(Math.max(1,Display.getWidth()),Math.max(1,Display.getHeight()))){visuals.close();}
        catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] Menu cleanup: "+ex);}
        finally{visuals=null;layout=null;}
    }
    @Override public void func_146281_b(){if(flow!=null)flow.escape();dispose();}
    @Override protected void func_73864_a(int mx, int my, int button) {
        if (button != 0 || layout == null || flow == null) { return; }
        int w=Display.getWidth(),h=Display.getHeight();
        double x=mx*(double)w/Math.max(1,field_146294_l),y=my*(double)h/Math.max(1,field_146295_m);
        int index=layout.hit(layout.localX(x,w,h),layout.localY(y,w,h));
        if (index >= 0) { focus = index; requestAction(MenuLayout.action(index),"mouse:"+mx+","+my); }
    }
    @Override protected void func_73869_a(char character, int key) {
        if (flow == null) { return; }
        if (key == Keyboard.KEY_F8) { hook.fallback(field_146297_k, original, null); return; }
        if (key == Keyboard.KEY_F7) { reduced = !reduced; flow = new MenuFlow(reduced, false); flow.escape(); return; }
        if (key == Keyboard.KEY_ESCAPE) { if(flow.state()==MenuFlow.State.EXITING)UiAudio.play(br.com.redeakashic.cinematic.core.UiCueGate.Cue.BACK);flow.escape(); return; }
        if(key==Keyboard.KEY_HOME){keyboardMode=true;focus=0;return;}
        if(key==Keyboard.KEY_END){keyboardMode=true;focus=MenuLayout.COUNT-1;return;}
        int delta = 0;
        if (key == Keyboard.KEY_UP) { delta = -1; }
        if (key == Keyboard.KEY_DOWN) { delta = 1; }
        if (key == Keyboard.KEY_TAB) {
            delta = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? -1 : 1;
        }
        if (delta != 0) { keyboardMode = true; focus = MenuLayout.nextFocus(focus, delta); }
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER || key == Keyboard.KEY_SPACE) { requestAction(MenuLayout.action(focus),"key:"+key); }
    }
    private void requestAction(MenuAction action,String source){
        if(flow.request(action)){System.out.println("[AkashicCinematic] ACCEPTED_INPUT "+source+" action="+action);UiAudio.play(action==MenuAction.PLAY?br.com.redeakashic.cinematic.core.UiCueGate.Cue.CONNECT:br.com.redeakashic.cinematic.core.UiCueGate.Cue.SELECT);}
    }
    private void dispatch(MenuAction action) {
        hook.dispatched(action.name());
        switch (action) {
            case PLAY:
                ConnectionCancellationGuard.INSTANCE.connect(field_146297_k, this);
                break;
            case SINGLEPLAYER: field_146297_k.func_147108_a(new GuiSelectWorld(this)); break;
            case OPTIONS: field_146297_k.func_147108_a(new GuiOptions(this, field_146297_k.field_71474_y)); break;
            case QUIT: field_146297_k.func_147108_a(new GuiYesNo(this, "Sair do Minecraft?", "Seu modpack sera fechado.", 1710)); break;
            default: throw new IllegalStateException("Unsupported action: " + action);
        }
    }
    @Override public void func_73878_a(boolean confirmed, int id) {
        if (id == 1710 && confirmed) { field_146297_k.func_71400_g(); } else { field_146297_k.func_147108_a(this); }
    }
    @Override public boolean func_73868_f() { return false; }
}
