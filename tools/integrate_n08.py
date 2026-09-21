from pathlib import Path
import json,hashlib,zipfile
R=Path(__file__).resolve().parents[1];J=R/'src/main/java/br/com/redeakashic/cinematic';backup=R/'evidence/pre-integration-src.zip';assert not backup.exists()
with zipfile.ZipFile(backup,'x',compression=zipfile.ZIP_DEFLATED) as z:
    for p in J.rglob('*.java'):z.write(p,p.relative_to(R).as_posix())
p=J/'client/MenuConfig.java';s=p.read_text();s=s.replace('    public final double parallax,sfxVolume;','    public final double parallax,sfxVolume;\n    public final boolean bgmEnabled,themeTransitions;\n    public final double bgmVolume,bgmFadeIn,bgmFadeOut;')
anchor='        enabled=c.get("general","enabled",true).getBoolean(true);';assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        bgmEnabled=c.get("bgm","enabled",true).getBoolean(true);\n        themeTransitions=c.get("screens","themeLoadingTransitions",true).getBoolean(true);\n        bgmVolume=finite(c.get("bgm","volume",0.40).getDouble(.40),.40,0,1);\n        bgmFadeIn=finite(c.get("bgm","fadeInSeconds",2.5).getDouble(2.5),2.5,.05,15);\n        bgmFadeOut=finite(c.get("bgm","fadeOutSeconds",0.8).getDouble(.8),.8,.05,10);')
s=s.replace('    public static MenuConfig read(File file){','    private static double finite(double v,double fallback,double lo,double hi){return Double.isFinite(v)?Math.max(lo,Math.min(hi,v)):fallback;}\n    public static MenuConfig read(File file){');p.write_text(s,encoding='utf-8',newline='\n')
p=J/'client/ClientProxy.java';s=p.read_text();anchor='            cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(ConnectionCancellationGuard.INSTANCE);';assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n            TransitionTheme.configure(config.themeTransitions);\n            cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new TransitionTheme());\n            try{MenuBgm.install(config,event.getSuggestedConfigurationFile().getParentFile());}\n            catch(ReflectiveOperationException|RuntimeException|LinkageError failure){event.getModLog().error("BGM unavailable; vanilla music preserved.",failure);}')
p.write_text(s,encoding='utf-8',newline='\n')
p=J/'bootstrap/CinematicLoadingPlugin.java';s=p.read_text();anchor='new String[]{';assert s.count(anchor)==1;s=s.replace(anchor,anchor+'"br.com.redeakashic.cinematic.bootstrap.LoadingThemeTransformer",');p.write_text(s,newline='\n')
# Record the received source; the previous source-absent baseline remains history.
(R/'evidence/resumption.json').write_text(json.dumps({'last_MenuBgm_write_before_reconnect':'30 lines; timed-out append had not landed','new_source':'BGM.mp3 supplied in conversation and identical Windows Downloads file found','input_sha256':'1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408','baseline_N07_preserved':True,'no_daily_pack_mutation':True,'target':'native-n08/0.5.0-alpha.1'},indent=2));print('N08_INTEGRATED',flush=True)
