package br.com.redeakashic.cinematic.bootstrap;
import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;
@IFMLLoadingPlugin.Name("AkashicCinematicBoot")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"br.com.redeakashic.cinematic.bootstrap.","br.com.redeakashic.cinematic.render."})
public final class CinematicLoadingPlugin implements IFMLLoadingPlugin {
    public CinematicLoadingPlugin(){
        Launch.classLoader.addTransformerExclusion("br.com.redeakashic.cinematic.render.");
        Launch.classLoader.addTransformerExclusion("br.com.redeakashic.cinematic.bootstrap.");
    }
    @Override public String[] getASMTransformerClass(){return new String[]{"br.com.redeakashic.cinematic.bootstrap.SplashTransformer"};}
    @Override public String getModContainerClass(){return null;}
    @Override public String getSetupClass(){return null;}
    @Override public void injectData(Map<String,Object> data){}
    @Override public String getAccessTransformerClass(){return null;}
}
