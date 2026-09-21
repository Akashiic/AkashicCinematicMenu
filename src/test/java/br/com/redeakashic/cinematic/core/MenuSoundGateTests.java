package br.com.redeakashic.cinematic.core;
public final class MenuSoundGateTests {
    private static int checks;private static void check(boolean value,String name){checks++;if(!value)throw new AssertionError(name);}
    public static void main(String[] args){
        check(MenuSoundGate.competing("minecraft","music.menu",false),"vanilla music labelled RECORDS detected");
        check(MenuSoundGate.competing("aether_legacy","music.menu",false),"known Aether menu identified independent of category");
        check(MenuSoundGate.competing("etfuturum","example_music",true),"other mod MUSIC category covered");
        check(!MenuSoundGate.competing("akashiccinematic","bgm.menu",true),"own BGM never rejected");
        check(!MenuSoundGate.competing("minecraft","random.click",false),"vanilla button retained");
        check(!MenuSoundGate.competing("akashiccinematic","ui.select",false),"own UI SFX retained");
        check(!MenuSoundGate.competing("examplemod","record.arbitrary",false),"not blanket RECORDS muting");
        check(!MenuSoundGate.competing(null,"music.menu",true),"null resource safe");
        for(String screen:new String[]{"br.com.redeakashic.cinematic.client.CinematicMenu","net.minecraft.client.gui.GuiOptions","net.minecraft.client.gui.GuiSelectWorld"})
            for(boolean world:new boolean[]{false,true})for(boolean reached:new boolean[]{false,true}){
                boolean scope=BgmFrontendPolicy.frontend(reached,world,screen);
                check(!(world&&scope),"no music ownership inside a world");check(!(!reached&&scope),"no ownership before root");
            }
        System.out.println("RESULT sound_gate_checks="+checks+" failures=0");
    }
}
