package br.com.redeakashic.cinematic.core;

/** The only external address the menu can open. Fixed at build time; never read from config, network or input. */
public final class MenuLinks {
    /** Convite permanente do Discord da Rede Akashic (pedido do dono, 17/11/2026). */
    public static final String DISCORD = "https://discord.gg/cMc32d7UTF";
    private MenuLinks() { }
    public static String forAction(MenuAction action) {
        if (action == MenuAction.DISCORD) { return DISCORD; }
        throw new IllegalArgumentException("no link for " + action);
    }
}
