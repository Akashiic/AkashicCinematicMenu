package br.com.redeakashic.cinematic.client;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
import br.com.redeakashic.cinematic.core.Numbers;
public final class MenuConfig {
    public final boolean enabled,reducedMotion,replaceDummyCore,particles,sfxEnabled,suppressBogoConflict,themeConnections;
    public final double parallax,sfxVolume;
    public final boolean bgmEnabled,themeTransitions;
    public final double bgmVolume,bgmFadeIn,bgmFadeOut;
    private MenuConfig(Configuration c){
        enabled=c.get("general","enabled",true).getBoolean(true);
        bgmEnabled=c.get("bgm","enabled",true).getBoolean(true);
        themeTransitions=c.get("screens","themeLoadingTransitions",true).getBoolean(true);
        bgmVolume=finite(c.get("bgm","volume",0.40).getDouble(.40),.40,0,1);
        bgmFadeIn=finite(c.get("bgm","fadeInSeconds",2.5).getDouble(2.5),2.5,.05,15);
        bgmFadeOut=finite(c.get("bgm","fadeOutSeconds",0.8).getDouble(.8),.8,.05,10);
        reducedMotion=c.get("motion","reducedMotion",false).getBoolean(false);
        replaceDummyCore=c.get("compatibility","replaceKnownDummyCoreMenus",true).getBoolean(true);
        particles=c.get("motion","particles",true).getBoolean(true);
        parallax=Numbers.clamp(c.get("motion","parallaxStrength",1.0).getDouble(1.0),0,2);
        sfxEnabled=c.get("audio","enabled",true,"UI cues only after the Minecraft sound engine is ready.").getBoolean(true);
        double volume=c.get("audio","uiVolume",0.45,"Additional UI volume multiplier, 0..1; respects MASTER volume.").getDouble(.45);
        sfxVolume=Double.isFinite(volume)?Math.max(0,Math.min(1,volume)):.45;
        suppressBogoConflict=c.get("compatibility","hideBogoInventoryTweaksModal",true,"Only the audited notice is moved to the log; inventory incompatibility is not repaired.").getBoolean(true);
        themeConnections=c.get("screens","themeConnectionScreens",true,"Connecting and disconnected backgrounds; preserve native networking and error messages.").getBoolean(true);
    }
    private static double finite(double v,double fallback,double lo,double hi){return Double.isFinite(v)?Math.max(lo,Math.min(hi,v)):fallback;}
    public static MenuConfig read(File file){
        Configuration c=new Configuration(file);c.load();MenuConfig result=new MenuConfig(c);
        if(c.hasChanged())c.save();return result;
    }
}
