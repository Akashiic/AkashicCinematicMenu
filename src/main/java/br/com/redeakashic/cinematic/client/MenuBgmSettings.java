package br.com.redeakashic.cinematic.client;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

/** Separate settings, so UI SFX preferences are never repurposed as music volume. */
public final class MenuBgmSettings {
    public final boolean enabled, loop;
    public final double volume, fadeIn, fadeOut;
    private MenuBgmSettings(Configuration c){
        enabled=c.get("music","enabled",true,"Custom music only in the supported frontend after its main menu appears.").getBoolean(true);
        loop=c.get("music","loop",true,"Repeat the same supplied recording; no network source.").getBoolean(true);
        volume=finite(c.get("music","volume",.40,"Additional MUSIC/MASTER multiplier; does not change the game sliders.").getDouble(.40),.40,0,1);
        fadeIn=finite(c.get("music","fadeInSeconds",2.5,"Entry fade begins after SoundHandler reports playback.").getDouble(2.5),2.5,0,30);
        fadeOut=finite(c.get("music","fadeOutSeconds",.8,"Soft exit to connection; world entry still stops immediately.").getDouble(.8),.8,0,10);
    }
    private static double finite(double value,double fallback,double min,double max){return Double.isFinite(value)?Math.max(min,Math.min(max,value)):fallback;}
    public static MenuBgmSettings read(File configDirectory){
        Configuration c=new Configuration(new File(configDirectory,"akashiccinematic-bgm.cfg"));c.load();
        MenuBgmSettings settings=new MenuBgmSettings(c);if(c.hasChanged())c.save();return settings;
    }
}
