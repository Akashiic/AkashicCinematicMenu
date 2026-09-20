from pathlib import Path
R=Path(__file__).resolve().parents[1];S=R/'src/main/java/br/com/redeakashic/cinematic'
def change(path,old,new):
    p=S/path;s=p.read_text(encoding='utf-8');assert s.count(old)==1,(path,old[:80]);p.write_text(s.replace(old,new),encoding='utf-8',newline='\n')
change('client/MenuHook.java','        GuiScreen original = event.gui;','        GuiScreen original = event.gui;') if False else None
change('client/MenuHook.java','        if (disabledForSession || event.gui == null','        if(!disabledForSession && config.suppressBogoConflict && Minecraft.func_71410_x().field_71441_e==null && BogoWarningCompat.consume(event.gui,log))event.gui=new net.minecraft.client.gui.GuiMainMenu();\n        if (disabledForSession || event.gui == null')
change('client/ClientProxy.java','            MinecraftForge.EVENT_BUS.register(new MenuHook(config, event.getModLog()));','            UiAudio.configure(config);\n            MinecraftForge.EVENT_BUS.register(new MenuHook(config, event.getModLog()));\n            if(config.themeConnections)MinecraftForge.EVENT_BUS.register(new ConnectionScreenTheme(event.getModLog()));')
change('client/CinematicMenu.java','    private int focus,','    private int audioSelection=-2;\n    private int focus,')
change('client/CinematicMenu.java','                int selected=keyboardMode?focus:hovered;','                int selected=keyboardMode?focus:hovered;\n                if(selected!=audioSelection){if(selected>=0&&audioSelection!=-2)UiAudio.play(br.com.redeakashic.cinematic.core.UiCueGate.Cue.FOCUS);audioSelection=selected;}')
change('client/CinematicMenu.java','            MenuAction next=flow.consume();','            if(visitAge>.1)UiAudio.greet();\n            MenuAction next=flow.consume();')
change('client/CinematicMenu.java','focus = index; flow.request(MenuLayout.action(index));','focus = index; requestAction(MenuLayout.action(index));')
change('client/CinematicMenu.java','{ flow.request(MenuLayout.action(focus)); }','{ requestAction(MenuLayout.action(focus)); }')
change('client/CinematicMenu.java','if (key == Keyboard.KEY_ESCAPE) { flow.escape(); return; }','if (key == Keyboard.KEY_ESCAPE) { if(flow.state()==MenuFlow.State.EXITING)UiAudio.play(br.com.redeakashic.cinematic.core.UiCueGate.Cue.BACK);flow.escape(); return; }')
change('client/CinematicMenu.java','    private void dispatch(MenuAction action) {','    private void requestAction(MenuAction action){\n        if(flow.request(action))UiAudio.play(action==MenuAction.PLAY?br.com.redeakashic.cinematic.core.UiCueGate.Cue.CONNECT:br.com.redeakashic.cinematic.core.UiCueGate.Cue.SELECT);\n    }\n    private void dispatch(MenuAction action) {')
# Sound assets are generated later; repair scalar expression before running the generator.
p=R/'tools/build_audio.py';s=p.read_text();s=s.replace("float(20*np.log10(np.sqrt(np.mean(decoded**2))))),","float(20*np.log10(np.sqrt(np.mean(decoded**2)))),");p.write_text(s,encoding='utf-8',newline='\n')
print('N06_UI_INTEGRATED: targeted Bogo notice, event-based audio, connection theme registration',flush=True)
