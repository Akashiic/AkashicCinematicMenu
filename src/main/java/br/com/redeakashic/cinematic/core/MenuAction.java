package br.com.redeakashic.cinematic.core;

/**
 * Actions are a closed enum. No arbitrary command or script execution; the only URL is the fixed Discord invite
 * in {@link MenuLinks}. Order = on-screen order: five main buttons, then the compact Discord button.
 */
public enum MenuAction {
    PLAY, SINGLEPLAYER, MULTIPLAYER, OPTIONS, QUIT, DISCORD;
    /** Navigation leaves the menu (exit transition); the Discord link opens the browser and the menu stays. */
    public boolean navigates() { return this != DISCORD; }
}
