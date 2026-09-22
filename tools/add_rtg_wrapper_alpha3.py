from pathlib import Path
import json
R=Path(__file__).resolve().parents[1]
J=R/'src/main/java/br/com/redeakashic/cinematic'
p=J/'core/SecondaryScreenPolicy.java';s=p.read_text(encoding='utf-8')
anchor='        m.put("net.minecraft.client.gui.GuiConfirmOpenLink","LINK EXTERNO");'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        m.put("rtg.client.gui.WorldTypeMessageGUI","GERAÇÃO DE MUNDO");')
p.write_text(s,encoding='utf-8',newline='\n')
p=J/'core/BgmFrontendPolicy.java';s=p.read_text(encoding='utf-8')
anchor='        "net.minecraft.client.gui.GuiConfirmOpenLink",'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        "rtg.client.gui.WorldTypeMessageGUI",')
p.write_text(s,encoding='utf-8',newline='\n')
p=R/'src/test/java/br/com/redeakashic/cinematic/core/SecondaryScreenPolicyTests.java'
s=p.read_text(encoding='utf-8')
anchor='"net.minecraft.client.gui.GuiConfirmOpenLink"'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+',\n            "rtg.client.gui.WorldTypeMessageGUI"')
p.write_text(s,encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java',
             'src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF',
             'tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8')
    assert '0.7.0-alpha.2' in s,name
    p.write_text(s.replace('0.7.0-alpha.2','0.7.0-alpha.3'),encoding='utf-8',newline='\n')
report={
 'runtime_finding':'RTG intercepts GuiCreateWorld with rtg.client.gui.WorldTypeMessageGUI',
 'rtg_jar':'RTG-1.2.1-GTNH.jar',
 'wrapper_behavior':'extends GuiScreen; first draw call is drawDefaultBackground; one ordinary GuiButton; returns parent on OK',
 'change':'Exact wrapper allowlisted for secondary theme and BGM frontend continuity',
 'generic_mod_gui_support':False,
 'vanilla_create_world_action_logic_changed':False,
 'run1_preserved':'qa-n11-wave2/evidence/wave2run1',
 'version':'0.7.0-alpha.3'
}
(R/'evidence/rtg-wrapper-compat.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('RTG_EXACT_WRAPPER_ALPHA3_READY')
