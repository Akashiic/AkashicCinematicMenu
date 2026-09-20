package br.com.redeakashic.cinematic.core;
import java.util.List;
/** Fail-open to unknown messages; not a generic warning suppressor. */
public final class BogoNoticePolicy {
    private BogoNoticePolicy(){}
    public static boolean knownConflict(List<?> messages){
        if(messages==null||messages.isEmpty()||messages.size()>8)return false;
        boolean conflict=false;
        for(Object value:messages){
            if(!(value instanceof String))return false;
            String text=((String)value).replaceAll("\u00a7.","").trim();
            if(text.equals("InventoryTweaks is loaded. This will cause issues!")){conflict=true;continue;}
            if(!text.isEmpty()&&!text.equals("! Warning from Inventory Bogosorter !")&&!text.equals("Consider removing the mod and reload the game."))return false;
        }
        return conflict;
    }
}
