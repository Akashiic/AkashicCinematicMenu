package br.com.redeakashic.cinematic.core;
public final class MenuVolumeTests {
    private static int checks;private static void check(boolean value,String name){checks++;if(!value)throw new AssertionError(name);}
    public static void main(String[] args){
        float absent=MenuVolumeSource.ABSENT;
        check(MenuVolumeSource.audible(1f,1f,absent),"no mixer: Music slider governs");
        check(!MenuVolumeSource.audible(1f,0f,absent),"no mixer: Music at 0 silences the menu");
        check(MenuVolumeSource.audible(1f,0f,.5f),"mixer: Minecraft Music at 0 no longer silences the menu");
        check(!MenuVolumeSource.audible(1f,1f,0f),"mixer: muted Menu channel silences the menu");
        check(MenuVolumeSource.audible(1f,1f,.01f),"mixer: any channel level plays");
        check(!MenuVolumeSource.audible(0f,1f,1f),"Master at 0 still silences everything");
        check(!MenuVolumeSource.audible(0f,1f,absent),"Master at 0 without mixer");
        check(!MenuVolumeSource.audible(Float.NaN,1f,1f),"NaN master is silent");
        check(BgmFrontendPolicy.frontend(true,false,"com.akashiic.mixer.client.GuiAkashicMixer"),"mixer screen keeps the menu music");
        check(!BgmFrontendPolicy.frontend(true,true,"com.akashiic.mixer.client.GuiAkashicMixer"),"mixer screen in a world is not the menu");
        check(!MenuSoundGate.competing("akashiccinematic","bgm.menu",true),"own routed BGM is never filtered");
        System.out.println("RESULT menu_volume_checks="+checks+" failures=0");
    }
}
