package br.com.redeakashic.cinematic.core;
import java.util.*;
public final class SecondaryScreenPolicyTests {
    private static int checks;
    private static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
    public static void main(String[] args){
        String[] supported={
            "net.minecraft.client.gui.GuiOptions","net.minecraft.client.gui.GuiSelectWorld",
            "net.minecraft.client.gui.GuiCreateWorld","net.minecraft.client.gui.GuiLanguage",
            "net.minecraft.client.gui.GuiScreenResourcePacks","net.minecraft.client.gui.GuiControls",
            "net.minecraft.client.gui.GuiVideoSettings","net.minecraft.client.gui.GuiScreenOptionsSounds",
            "net.minecraft.client.gui.ScreenChatOptions","net.minecraft.client.gui.GuiSnooper",
            "net.minecraft.client.gui.GuiCreateFlatWorld","net.minecraft.client.gui.GuiFlatPresets",
            "net.minecraft.client.gui.GuiRenameWorld","net.minecraft.client.gui.GuiYesNo",
            "net.minecraft.client.gui.GuiConfirmOpenLink",
            "rtg.client.gui.WorldTypeMessageGUI",
            "me.flashyreese.mods.reeses_sodium_options.client.gui.ReeseSodiumVideoOptionsScreen"
        };
        Set<String> unique=new HashSet<String>();
        for(String name:supported){
            check(SecondaryScreenPolicy.supports(name),"supported "+name);
            check(!SecondaryScreenPolicy.label(name).isEmpty(),"label "+name);
            check(unique.add(name),"unique "+name);
        }
        check(unique.equals(SecondaryScreenPolicy.screens()),"exact support set");
        for(String name:new String[]{null,"",
                "net.minecraft.client.gui.GuiIngameMenu","mod.gui.SomeScreen"}){
            check(!SecondaryScreenPolicy.supports(name),"unknown untouched "+name);
        }
        check(BgmFrontendPolicy.frontend(true,false,"net.minecraft.client.gui.ScreenChatOptions"),
                "chat options keeps menu BGM");
        check(!BgmFrontendPolicy.frontend(true,true,"net.minecraft.client.gui.GuiOptions"),
                "world always suppresses menu BGM context");
        System.out.println("RESULT secondary_policy_checks="+checks+" failures=0");
    }
}
