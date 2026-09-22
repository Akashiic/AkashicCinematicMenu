from pathlib import Path
import json
R=Path(__file__).resolve().parents[1]
J=R/'src/main/java/br/com/redeakashic/cinematic'
p=J/'core/SecondaryScreenPolicy.java';s=p.read_text(encoding='utf-8')
anchor='        m.put("net.minecraft.client.gui.GuiRenameWorld","RENOMEAR MUNDO");'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        m.put("net.minecraft.client.gui.GuiYesNo","CONFIRMAÇÃO");\n        m.put("net.minecraft.client.gui.GuiConfirmOpenLink","LINK EXTERNO");')
p.write_text(s,encoding='utf-8',newline='\n')
p=J/'core/BgmFrontendPolicy.java';s=p.read_text(encoding='utf-8')
anchor='        "net.minecraft.client.gui.GuiYesNo",'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        "net.minecraft.client.gui.GuiConfirmOpenLink",')
p.write_text(s,encoding='utf-8',newline='\n')
p=R/'src/test/java/br/com/redeakashic/cinematic/core/SecondaryScreenPolicyTests.java';s=p.read_text(encoding='utf-8')
s=s.replace('"net.minecraft.client.gui.GuiRenameWorld"','"net.minecraft.client.gui.GuiRenameWorld","net.minecraft.client.gui.GuiYesNo",\n            "net.minecraft.client.gui.GuiConfirmOpenLink"')
s=s.replace('for(String name:new String[]{null,"","net.minecraft.client.gui.GuiYesNo",','for(String name:new String[]{null,"",')
p.write_text(s,encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.7.0-alpha.1' in s,name
    p.write_text(s.replace('0.7.0-alpha.1','0.7.0-alpha.2'),encoding='utf-8',newline='\n')
(R/'evidence/alpha1-extension.json').write_text(json.dumps({'change':'Theme remaining vanilla confirmation dialogs visually; no callback/action logic changed','quit_confirmation':'still removed only in CinematicMenu','destructive_confirmations':'still native','version':'0.7.0-alpha.2'},indent=2))
print('CONFIRMATION_SKIN_EXTENDED_ALPHA2')
