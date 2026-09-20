package br.com.redeakashic.cinematic.core;

/** Exact classes only: never accept arbitrary subclasses or an error screen. */
public final class ScreenPolicy {
    private ScreenPolicy() { }
    public static boolean accepts(String name, boolean dummyCore) {
        if ("net.minecraft.client.gui.GuiMainMenu".equals(name)) { return true; }
        return dummyCore && ("DummyCore.Client.GuiMainMenuVanilla".equals(name)
                || "DummyCore.Client.GuiMainMenuOld".equals(name));
    }
}
