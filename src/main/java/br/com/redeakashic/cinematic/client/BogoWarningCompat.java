package br.com.redeakashic.cinematic.client;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.Logger;
/** Only the audited InventoryTweaks conflict notice; unknown warnings remain visible. */
final class BogoWarningCompat {
    static final String CLASS="com.cleanroommc.bogosorter.compat.screen.WarningScreen";
    static int suppressed;
    private BogoWarningCompat(){}
    static boolean consume(GuiScreen screen,Logger log){
        if(screen==null||!screen.getClass().getName().equals(CLASS))return false;
        try{
            Object value=screen.getClass().getField("messages").get(screen);
            if(!(value instanceof List<?>))return false;
            List<?> messages=(List<?>)value;
            if(!br.com.redeakashic.cinematic.core.BogoNoticePolicy.knownConflict(messages))return false;
            if(suppressed++==0)log.warn("BogoSorter modal moved out of menu: InventoryTweaks is also installed. Conflict is NOT fixed by hiding the notice. No inventory behavior or other warning changed.");
            return true;
        }catch(ReflectiveOperationException|RuntimeException ex){log.warn("BogoSorter notice could not be inspected; preserving original screen.",ex);return false;}
    }
}
