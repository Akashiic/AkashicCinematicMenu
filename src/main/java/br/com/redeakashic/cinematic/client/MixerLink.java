package br.com.redeakashic.cinematic.client;

import br.com.redeakashic.cinematic.core.MenuVolumeSource;
import cpw.mods.fml.common.Loader;
import org.apache.logging.log4j.Logger;

/**
 * Optional AkashicMixer integration (fail-open). When the mixer is installed, the menu music and the UI cues get
 * their own channels in the Akashic Sounds Mixer ("Menu Music", "Menu Sounds") and the mixer applies them in place of
 * Minecraft's Music slider and of the Master-only category. Without the mixer, or with a mixer build older than the
 * sound routing API, nothing changes. Only {@link MixerChannels} touches the mixer's classes, and it is loaded only
 * after {@code Loader.isModLoaded("akashicmixer")}.
 */
public final class MixerLink {
    interface Levels { float music(); float sfx(); }
    private static volatile Levels levels;
    public static volatile String status="NOT_CHECKED";
    private MixerLink(){}
    static void install(Logger log){
        if(levels!=null)return;
        if(!Loader.isModLoaded("akashicmixer")){status="ABSENT";return;}
        try{
            Levels connected=MixerChannels.connect();
            levels=connected;status=connected==null?"MIXER_WITHOUT_ROUTING":"ACTIVE";
            if(connected==null)log.warn("AkashicMixer found without sound routing; menu volume stays on Minecraft's sliders.");
            else log.info("Menu music and UI sounds handed to the Akashic Sounds Mixer.");
        }catch(RuntimeException|LinkageError ex){
            levels=null;status="FAILED";log.warn("AkashicMixer integration unavailable; menu volume stays on Minecraft's sliders.",ex);
        }
    }
    static boolean active(){return levels!=null;}
    /** Menu Music channel level, 0..1, or {@link MenuVolumeSource#ABSENT}. */
    static float music(){Levels l=levels;return l==null?MenuVolumeSource.ABSENT:l.music();}
    /** Menu Sounds channel level, 0..1, or {@link MenuVolumeSource#ABSENT}. */
    static float sfx(){Levels l=levels;return l==null?MenuVolumeSource.ABSENT:l.sfx();}
}
