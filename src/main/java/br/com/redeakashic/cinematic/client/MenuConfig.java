package br.com.redeakashic.cinematic.client;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
import br.com.redeakashic.cinematic.core.Numbers;
public final class MenuConfig {
    public final boolean enabled,reducedMotion,replaceDummyCore,particles,sfxEnabled,suppressBogoConflict,themeConnections,themeTransitions,themeSecondaryScreens,layeredParallax,animatedPlay;
    public final double parallax,sfxVolume;
    private MenuConfig(Configuration c){
        enabled=c.get("general","enabled",true).getBoolean(true);
        layeredParallax=c.get("motion","layeredParallax",true,"Separate sky, architecture and angel; reduced motion uses approved static art.").getBoolean(true);
        animatedPlay=c.get("motion","animatedPlayButton",true,"Gentle crimson gradient and white glint; reduced motion removes idle animation.").getBoolean(true);
        reducedMotion=c.get("motion","reducedMotion",false).getBoolean(false);
        replaceDummyCore=c.get("compatibility","replaceKnownDummyCoreMenus",true).getBoolean(true);
        particles=c.get("motion","particles",true).getBoolean(true);
        parallax=Numbers.clamp(c.get("motion","parallaxStrength",1.0).getDouble(1.0),0,2);
        sfxEnabled=c.get("audio","enabled",true,"UI cues only after the Minecraft sound engine is ready.").getBoolean(true);
        double volume=c.get("audio","uiVolume",0.45,"Additional UI volume multiplier, 0..1; respects MASTER volume.").getDouble(.45);
        sfxVolume=Double.isFinite(volume)?Math.max(0,Math.min(1,volume)):.45;
        suppressBogoConflict=c.get("compatibility","hideBogoInventoryTweaksModal",true,"Only the audited notice is moved to the log; inventory incompatibility is not repaired.").getBoolean(true);
        themeTransitions=c.get("screens","themeLoadingTransitions",true,"Only audited Aether, vanilla loading and terrain backgrounds.").getBoolean(true);
        themeSecondaryScreens=c.get("screens","themeVanillaMenus",true,"Options/world/language/resources/control family; native behavior retained.").getBoolean(true);
        themeConnections=c.get("screens","themeConnectionScreens",true,"Connecting and disconnected backgrounds; preserve native networking and error messages.").getBoolean(true);
    }
    public static MenuConfig read(File file){
        Configuration c=new Configuration(file);c.load();MenuConfig result=new MenuConfig(c);
        if(c.hasChanged())c.save();return result;
    }
}
