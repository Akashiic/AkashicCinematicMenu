package br.com.redeakashic.cinematic.client;

import br.com.redeakashic.cinematic.core.DampedAxis;
import br.com.redeakashic.cinematic.core.FrameClock;
import br.com.redeakashic.cinematic.core.MenuAction;
import br.com.redeakashic.cinematic.core.MenuFlow;
import br.com.redeakashic.cinematic.core.MenuLayout;
import br.com.redeakashic.cinematic.core.Numbers;
import br.com.redeakashic.cinematic.core.Viewport;
import cpw.mods.fml.client.FMLClientHandler;
import br.com.redeakashic.cinematic.core.ServerTarget;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/** Forge 1.7.10 adapter compiled against actual SRG classes, not generated stubs. */
public final class CinematicMenu extends GuiScreen implements GuiYesNoCallback {
    private static final String[] LABELS = {"JOGAR", "SINGLEPLAYER", "OPCOES", "SAIR"};
    private final GuiScreen original;
    private final MenuHook hook;
    private final MenuConfig config;
    private final FrameClock clock = new FrameClock(System::nanoTime);
    private final DampedAxis mouseX = new DampedAxis(10), mouseY = new DampedAxis(10);
    private final DampedAxis[] highlights = new DampedAxis[MenuLayout.COUNT];
    private MenuFlow flow;
    private Viewport viewport;
    private boolean shown, reduced, keyboardMode = true;
    private int focus, previousMouseX = Integer.MIN_VALUE, previousMouseY = Integer.MIN_VALUE;
    CinematicMenu(GuiScreen original, MenuHook hook, MenuConfig config) {
        this.original = original; this.hook = hook; this.config = config; this.reduced = config.reducedMotion;
        for (int i = 0; i < highlights.length; i++) { highlights[i] = new DampedAxis(18); }
    }
    @Override
    public void func_73866_w_() {
        viewport = new Viewport(Math.max(1, field_146294_l), Math.max(1, field_146295_m));
        flow = new MenuFlow(reduced, !shown); shown = true; clock.resync();
    }
    @Override
    @SuppressWarnings("try")
    public void func_73863_a(int mx, int my, float partialTicks) {
        try {
            double dt = clock.tick(Display.isActive());
            flow.advance(dt);
            if (previousMouseX != Integer.MIN_VALUE && (mx != previousMouseX || my != previousMouseY)) { keyboardMode = false; }
            previousMouseX = mx; previousMouseY = my;
            int hovered = MenuLayout.hit(viewport.localX(mx), viewport.localY(my));
            if (!keyboardMode && hovered >= 0) { focus = hovered; }
            int selected = keyboardMode ? focus : hovered;
            if (reduced) { mouseX.reset(0); mouseY.reset(0); }
            else {
                mouseX.step(Numbers.clamp(viewport.localX(mx) / 480 - 1, -1, 1), dt);
                mouseY.step(Numbers.clamp(viewport.localY(my) / 270 - 1, -1, 1), dt);
            }
            try (RenderScope scope = new RenderScope()) {
                ScenePainter.rect(0, 0, field_146294_l, field_146295_m, 0x06070B, 1);
                GL11.glTranslated(viewport.x, viewport.y, 0);
                GL11.glScaled(viewport.scale, viewport.scale, 1);
                ScenePainter.scene(reduced ? 0 : clock.elapsed(), mouseX.position() * config.parallax,
                        mouseY.position() * config.parallax, config.particles);
                double alpha = flow.visibility();
                ScenePainter.text(field_146289_q, "REDE AKASHIC / FORBIDDEN SERIES", 75, 55, .8, 0x988E96, alpha);
                ScenePainter.text(field_146289_q, "T H E   E N D   O F", 76, 125, 1.25, 0xD1BAB4, alpha);
                ScenePainter.text(field_146289_q, "FORBIDDEN", 73, 146, 4.6, 0xF1E6DE, alpha);
                ScenePainter.text(field_146289_q, "UM LIMIAR. INFINITAS POSSIBILIDADES.", 76, 199, .75, 0xA17B83, alpha);
                for (int i = 0; i < MenuLayout.COUNT; i++) {
                    double target = i == selected ? 1 : 0;
                    if (reduced) { highlights[i].reset(target); } else { highlights[i].step(target, dt); }
                    double h = highlights[i].position(), a = flow.buttonVisibility(i), y = MenuLayout.top(i);
                    ScenePainter.rect(MenuLayout.X, y, MenuLayout.W, MenuLayout.H, 0x712333, h * .28 * a);
                    ScenePainter.rect(MenuLayout.X, y, 2, MenuLayout.H, 0xE35A6B, h * a);
                    ScenePainter.rect(MenuLayout.X, y + MenuLayout.H, MenuLayout.W, 1, 0x82636A, .2 * a);
                    ScenePainter.text(field_146289_q, LABELS[i], MenuLayout.X + 17 + h * 5, y + 12,
                            1.25, 0xECE2DD, a * (.72 + .28 * h));
                    // Direct Play no longer opens a server-list screen.
                }
                ScenePainter.text(field_146289_q, "1.7.10 / ALPHA TECNICA / STATUS NAO CONSULTADO", 74, 473, .66, 0x927F87, 1);
                ScenePainter.text(field_146289_q, "TAB: NAVEGAR   F7: MOVIMENTO   F8: MENU ORIGINAL", 74, 496, .66, 0xAA9098, 1);
                ScenePainter.text(field_146289_q, "CENA PROCEDURAL / ARTE FINAL PENDENTE", 585, 487, .65, 0xAD9099, .7);
                if (reduced) { ScenePainter.text(field_146289_q, "MOVIMENTO REDUZIDO", 758, 43, .68, 0xD3B8BE, 1); }
            }
            // Handoff happens only AFTER all local GL state has been restored.
            MenuAction next = flow.consume();
            if (next != null) { dispatch(next); }
        } catch (RuntimeException ex) { hook.fallback(field_146297_k, original, ex); }
        catch (LinkageError ex) { hook.fallback(field_146297_k, original, ex); }
    }
    @Override
    protected void func_73864_a(int mx, int my, int button) {
        if (button != 0 || viewport == null || flow == null) { return; }
        int index = MenuLayout.hit(viewport.localX(mx), viewport.localY(my));
        if (index >= 0) { focus = index; flow.request(MenuLayout.action(index)); }
    }
    @Override
    protected void func_73869_a(char character, int key) {
        if (flow == null) { return; }
        if (key == Keyboard.KEY_F8) { hook.fallback(field_146297_k, original, null); return; }
        if (key == Keyboard.KEY_F7) { reduced = !reduced; flow = new MenuFlow(reduced, false); return; }
        if (key == Keyboard.KEY_ESCAPE) { flow.escape(); return; }
        int delta = 0;
        if (key == Keyboard.KEY_UP) { delta = -1; }
        if (key == Keyboard.KEY_DOWN) { delta = 1; }
        if (key == Keyboard.KEY_TAB) {
            delta = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? -1 : 1;
        }
        if (delta != 0) { keyboardMode = true; focus = MenuLayout.nextFocus(focus, delta); }
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) { flow.request(MenuLayout.action(focus)); }
    }
    private void dispatch(MenuAction action) {
        hook.dispatched(action.name());
        switch (action) {
            case PLAY:
                FMLClientHandler handler = FMLClientHandler.instance();
                // This direct route does not instantiate GuiMultiplayer. Forge needs these maps initialized.
                handler.setupServerList();
                handler.connectToServer(this, new ServerData(ServerTarget.NAME, ServerTarget.address()));
                break;
            case SINGLEPLAYER: field_146297_k.func_147108_a(new GuiSelectWorld(this)); break;
            case OPTIONS: field_146297_k.func_147108_a(new GuiOptions(this, field_146297_k.field_71474_y)); break;
            case QUIT: field_146297_k.func_147108_a(new GuiYesNo(this, "Sair do Minecraft?", "Seu modpack sera fechado.", 1710)); break;
            default: throw new IllegalStateException("Unsupported action: " + action);
        }
    }
    @Override
    public void func_73878_a(boolean confirmed, int id) {
        if (id == 1710 && confirmed) { field_146297_k.func_71400_g(); }
        else { field_146297_k.func_147108_a(this); }
    }
    @Override
    public boolean func_73868_f() { return false; }
}
