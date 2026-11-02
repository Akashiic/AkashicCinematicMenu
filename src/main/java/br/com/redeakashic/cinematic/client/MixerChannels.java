package br.com.redeakashic.cinematic.client;

import com.akashiic.mixer.api.AkashicMixerApi;
import com.akashiic.mixer.api.Channels;
import com.akashiic.mixer.api.MixerChannel;

/** The only class that references AkashicMixer; loaded by {@link MixerLink} only when the mixer is installed. */
final class MixerChannels implements MixerLink.Levels {
    /** After the other Akashic channels in the mixer screen. */
    static final int MUSIC_ORDER=80, SFX_ORDER=81;
    static final String DOMAIN="akashiccinematic";
    private final MixerChannel music, sfx;
    private MixerChannels(MixerChannel music,MixerChannel sfx){this.music=music;this.sfx=sfx;}
    /** Registers and routes both channels, or returns null for a mixer without {@code routeSounds}. */
    static MixerLink.Levels connect(){
        try{AkashicMixerApi.class.getMethod("routeSounds",String.class,String.class,MixerChannel.class);}
        catch(NoSuchMethodException older){return null;}
        MixerChannel music=AkashicMixerApi.register(Channels.MENU_MUSIC,MUSIC_ORDER);
        MixerChannel sfx=AkashicMixerApi.register(Channels.MENU_SFX,SFX_ORDER);
        // sounds.json: bgm.menu (category music) and ui.* (category master).
        AkashicMixerApi.routeSounds(DOMAIN,"bgm.",music);
        AkashicMixerApi.routeSounds(DOMAIN,"ui.",sfx);
        return new MixerChannels(music,sfx);
    }
    @Override public float music(){return music.level();}
    @Override public float sfx(){return sfx.level();}
}
