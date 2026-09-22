package br.com.redeakashic.cinematic.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Explicit menu family. Unknown mod screens are not silently adopted as a music context. */
public final class BgmFrontendPolicy {
    public static final String ROOT="br.com.redeakashic.cinematic.client.CinematicMenu";
    private static final Set<String> MENUS=Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
        ROOT,
        "net.minecraft.client.gui.GuiOptions",
        "net.minecraft.client.gui.GuiVideoSettings",
        "net.minecraft.client.gui.GuiControls",
        "net.minecraft.client.gui.GuiLanguage",
        "net.minecraft.client.gui.GuiScreenResourcePacks",
        "net.minecraft.client.gui.GuiScreenOptionsSounds",
        "net.minecraft.client.gui.ScreenChatOptions",
        "net.minecraft.client.gui.GuiSnooper",
        "net.minecraft.client.gui.GuiSelectWorld",
        "net.minecraft.client.gui.GuiCreateWorld",
        "net.minecraft.client.gui.GuiCreateFlatWorld",
        "net.minecraft.client.gui.GuiFlatPresets",
        "net.minecraft.client.gui.GuiRenameWorld",
        "net.minecraft.client.gui.GuiYesNo",
        "net.minecraft.client.gui.GuiConfirmOpenLink",
        "rtg.client.gui.WorldTypeMessageGUI",
        "me.flashyreese.mods.reeses_sodium_options.client.gui.ReeseSodiumVideoOptionsScreen",
        "net.minecraft.client.gui.GuiYesNoLink"
    )));
    private BgmFrontendPolicy(){}
    public static boolean root(String screen){return ROOT.equals(screen);}
    public static boolean frontend(boolean reachedRoot,boolean world,String screen){
        return reachedRoot&&!world&&screen!=null&&MENUS.contains(screen);
    }
    public static BgmPlayback.Context context(boolean enabled,boolean reachedRoot,boolean world,String screen){
        if(!enabled||world)return BgmPlayback.Context.WORLD_OR_DISABLED;
        return frontend(reachedRoot,false,screen)?BgmPlayback.Context.FRONTEND:BgmPlayback.Context.LEAVING;
    }
}
