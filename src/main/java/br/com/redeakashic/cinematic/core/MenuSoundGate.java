package br.com.redeakashic.cinematic.core;
/** Classification only; the caller must additionally own the out-of-world frontend. */
public final class MenuSoundGate {
    private MenuSoundGate(){}
    public static boolean competing(String domain,String path,boolean musicCategory){
        if(domain==null||path==null)return false;
        if("akashiccinematic".equals(domain)&&"bgm.menu".equals(path))return false;
        if(musicCategory)return true;
        // These audited music IDs may be reclassified as RECORDS by resource/mod combinations.
        return ("minecraft".equals(domain)||"aether_legacy".equals(domain))&&path.startsWith("music.");
    }
}
