package br.com.redeakashic.cinematic.core;
/**
 * Who owns the menu's loudness. With the Akashic Sounds Mixer installed, its channel (Menu Music / Menu Sounds) replaces
 * the Minecraft category slider, which then no longer affects the menu; without it the category slider applies as
 * before. Minecraft's Master always applies (it is the sound engine's listener gain).
 */
public final class MenuVolumeSource {
    /** Mixer channel level when the mixer is absent or could not be connected. */
    public static final float ABSENT=-1f;
    private MenuVolumeSource(){}
    public static boolean audible(float master,float vanillaCategory,float mixerChannel){
        if(!(master>0))return false;
        return mixerChannel>=0?mixerChannel>0:vanillaCategory>0;
    }
}
