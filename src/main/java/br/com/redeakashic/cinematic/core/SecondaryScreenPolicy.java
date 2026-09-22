package br.com.redeakashic.cinematic.core;
import java.util.*;
/** Exact vanilla front-end family. Unknown mod GUIs keep their original renderer. */
public final class SecondaryScreenPolicy {
    private static final Map<String,String> LABELS;
    static{
        LinkedHashMap<String,String> m=new LinkedHashMap<String,String>();
        m.put("net.minecraft.client.gui.GuiOptions","CONFIGURAÇÕES");
        m.put("net.minecraft.client.gui.GuiSelectWorld","MUNDOS");
        m.put("net.minecraft.client.gui.GuiCreateWorld","NOVO MUNDO");
        m.put("net.minecraft.client.gui.GuiLanguage","IDIOMA");
        m.put("net.minecraft.client.gui.GuiScreenResourcePacks","TEXTURAS");
        m.put("net.minecraft.client.gui.GuiControls","CONTROLES");
        m.put("net.minecraft.client.gui.GuiVideoSettings","VÍDEO");
        m.put("net.minecraft.client.gui.GuiScreenOptionsSounds","ÁUDIO");
        m.put("net.minecraft.client.gui.ScreenChatOptions","CHAT");
        m.put("net.minecraft.client.gui.GuiSnooper","TELEMETRIA");
        m.put("net.minecraft.client.gui.GuiCreateFlatWorld","MUNDO PLANO");
        m.put("net.minecraft.client.gui.GuiFlatPresets","PRESETS");
        m.put("net.minecraft.client.gui.GuiRenameWorld","RENOMEAR MUNDO");
        m.put("net.minecraft.client.gui.GuiYesNo","CONFIRMAÇÃO");
        m.put("net.minecraft.client.gui.GuiConfirmOpenLink","LINK EXTERNO");
        m.put("rtg.client.gui.WorldTypeMessageGUI","GERAÇÃO DE MUNDO");
        m.put("me.flashyreese.mods.reeses_sodium_options.client.gui.ReeseSodiumVideoOptionsScreen","VÍDEO");
        LABELS=Collections.unmodifiableMap(m);
    }
    private SecondaryScreenPolicy(){}
    public static boolean supports(String className){return className!=null&&LABELS.containsKey(className);}
    public static String label(String className){String v=LABELS.get(className);return v==null?"":v;}
    public static Set<String> screens(){return LABELS.keySet();}
}
