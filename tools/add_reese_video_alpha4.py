from pathlib import Path
import json
R=Path(__file__).resolve().parents[1]
J=R/'src/main/java/br/com/redeakashic/cinematic'
cls='me.flashyreese.mods.reeses_sodium_options.client.gui.ReeseSodiumVideoOptionsScreen'
p=J/'core/SecondaryScreenPolicy.java';s=p.read_text(encoding='utf-8')
anchor='        m.put("rtg.client.gui.WorldTypeMessageGUI","GERAÇÃO DE MUNDO");'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        m.put("'+cls+'","VÍDEO");')
p.write_text(s,encoding='utf-8',newline='\n')
p=J/'core/BgmFrontendPolicy.java';s=p.read_text(encoding='utf-8')
anchor='        "rtg.client.gui.WorldTypeMessageGUI",'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        "'+cls+'",')
p.write_text(s,encoding='utf-8',newline='\n')
p=R/'src/test/java/br/com/redeakashic/cinematic/core/SecondaryScreenPolicyTests.java'
s=p.read_text(encoding='utf-8')
anchor='"rtg.client.gui.WorldTypeMessageGUI"'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+',\n            "'+cls+'"')
p.write_text(s,encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java',
             'src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF',
             'tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8')
    assert '0.7.0-alpha.3' in s,name
    p.write_text(s.replace('0.7.0-alpha.3','0.7.0-alpha.4'),encoding='utf-8',newline='\n')
report={
 'runtime_finding':'Angelica routes Video to ReeseSodiumVideoOptionsScreen',
 'class':cls,'jar':'angelica-2.2.8.jar',
 'inheritance':'ReeseSodiumVideoOptionsScreen -> SodiumOptionsGUI -> ScrollableGuiScreen -> GuiScreen',
 'background_call':'Reese drawScreen invokes inherited func_146276_q_ before custom frame rendering',
 'theme_scope':'exact class background + BGM continuity only',
 'reese_controls_reimplemented':False,
 'run3_preserved':'qa-n11-wave2/evidence/wave2run3',
 'version':'0.7.0-alpha.4'
}
(R/'evidence/reese-video-compat.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('REESE_VIDEO_ALPHA4_READY')
