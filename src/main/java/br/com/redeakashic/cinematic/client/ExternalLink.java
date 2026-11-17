package br.com.redeakashic.cinematic.client;
import br.com.redeakashic.cinematic.core.MenuAction;
import br.com.redeakashic.cinematic.core.MenuLinks;
import java.net.URI;

/**
 * Opens the fixed Discord invite in the system browser (owner choice 17/11/2026: no confirmation screen).
 * Same path vanilla 1.7.10 uses for chat links (java.awt.Desktop by reflection), then LWJGL's Sys.openURL.
 * The address never comes from input, config or network: only {@link MenuLinks#forAction}.
 */
final class ExternalLink {
    private static long lastOpen;
    public static volatile String lastResult="NONE";
    private ExternalLink(){}
    /** Debounced (1.5 s), so a double click or a held Enter does not open several tabs. */
    static boolean open(MenuAction action){
        long now=System.nanoTime();if(lastOpen!=0&&now-lastOpen<1_500_000_000L){lastResult="DEBOUNCED";return false;}
        lastOpen=now;String url=MenuLinks.forAction(action);
        try{
            Class<?> desktop=Class.forName("java.awt.Desktop");
            Object instance=desktop.getMethod("getDesktop").invoke(null);
            desktop.getMethod("browse",URI.class).invoke(instance,new URI(url));
            lastResult="DESKTOP";System.out.println("[AkashicCinematic] OPENED_LINK "+url+" via Desktop");return true;
        }catch(Exception|LinkageError desktopFailed){
            try{
                if(org.lwjgl.Sys.openURL(url)){lastResult="LWJGL";System.out.println("[AkashicCinematic] OPENED_LINK "+url+" via LWJGL");return true;}
            }catch(RuntimeException|LinkageError ignored){/* reported below */}
            lastResult="FAILED";System.err.println("[AkashicCinematic] Could not open "+url+": "+desktopFailed);return false;
        }
    }
}
