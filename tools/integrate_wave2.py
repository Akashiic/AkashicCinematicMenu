from pathlib import Path
import json,zipfile
R=Path(__file__).resolve().parents[1]
J=R/'src/main/java/br/com/redeakashic/cinematic'
backup=R/'evidence/pre-wave2-src.zip'
assert not backup.exists()
with zipfile.ZipFile(backup,'x',compression=zipfile.ZIP_DEFLATED) as z:
    for p in J.rglob('*.java'):z.write(p,p.relative_to(R).as_posix())

p=J/'client/MenuConfig.java';s=p.read_text()
s=s.replace('themeConnections,themeTransitions,layeredParallax,animatedPlay;',
            'themeConnections,themeTransitions,themeSecondaryScreens,layeredParallax,animatedPlay;')
anchor='        themeTransitions=c.get("screens","themeLoadingTransitions",true,"Only audited Aether, vanilla loading and terrain backgrounds.").getBoolean(true);'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        themeSecondaryScreens=c.get("screens","themeVanillaMenus",true,"Options/world/language/resources/control family; native behavior retained.").getBoolean(true);')
p.write_text(s,encoding='utf-8',newline='\n')

p=J/'client/ClientProxy.java';s=p.read_text()
anchor='            TransitionTheme.configure(config.themeTransitions);'
assert s.count(anchor)==1
s=s.replace(anchor,'            SecondaryScreenTheme.configure(config.themeSecondaryScreens);\n            cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(SecondaryScreenTheme.INSTANCE);\n'+anchor)
p.write_text(s,encoding='utf-8',newline='\n')
p=J/'bootstrap/CinematicLoadingPlugin.java';s=p.read_text()
anchor='new String[]{"br.com.redeakashic.cinematic.bootstrap.LoadingThemeTransformer",'
assert s.count(anchor)==1
s=s.replace(anchor,'new String[]{"br.com.redeakashic.cinematic.bootstrap.SecondaryThemeTransformer","br.com.redeakashic.cinematic.bootstrap.LoadingThemeTransformer",')
p.write_text(s,encoding='utf-8',newline='\n')

p=J/'client/CinematicMenu.java';s=p.read_text()
assert 'extends GuiScreen implements GuiYesNoCallback' in s
s=s.replace('extends GuiScreen implements GuiYesNoCallback','extends GuiScreen')
old='case QUIT: field_146297_k.func_147108_a(new GuiYesNo(this, "Sair do Minecraft?", "Seu modpack sera fechado.", 1710)); break;'
assert old in s
s=s.replace(old,'case QUIT: field_146297_k.func_71400_g(); break;')
old='''    @Override public void func_73878_a(boolean confirmed, int id) {
        if (id == 1710 && confirmed) { field_146297_k.func_71400_g(); } else { field_146297_k.func_147108_a(this); }
    }
'''
assert old in s
s=s.replace(old,'')
p.write_text(s,encoding='utf-8',newline='\n')

p=J/'core/BgmFrontendPolicy.java';s=p.read_text()
anchor='        "net.minecraft.client.gui.GuiScreenOptionsSounds",'
assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        "net.minecraft.client.gui.ScreenChatOptions",')
p.write_text(s,encoding='utf-8',newline='\n')
print('WAVE2_INTEGRATED_WITH_DIRECT_QUIT')
