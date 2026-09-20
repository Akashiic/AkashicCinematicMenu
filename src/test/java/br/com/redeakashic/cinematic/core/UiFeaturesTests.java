package br.com.redeakashic.cinematic.core;
import java.util.*;
import br.com.redeakashic.cinematic.core.UiCueGate.Cue;
public final class UiFeaturesTests {
    private static int checks;private static void check(boolean value,String label){checks++;if(!value)throw new AssertionError(label);}
    public static void main(String[] args){
        UiCueGate gate=new UiCueGate();check(gate.allow(Cue.FOCUS,0),"first focus");
        for(int i=0;i<100;i++)check(!gate.allow(Cue.FOCUS,1_000_000L*i),"focus debounce");
        check(gate.allow(Cue.FOCUS,110_000_000L),"focus at boundary");
        check(gate.allow(Cue.SELECT,120_000_000L),"action priority");check(!gate.allow(Cue.FOCUS,230_000_000L),"no focus over action");
        check(!gate.allow(Cue.SELECT,120_000_000L),"no repeated select");check(gate.allow(Cue.FOCUS,280_000_000L),"focus resumes");
        check(gate.allow(Cue.ENTER,0),"first greeting");check(!gate.allow(Cue.ENTER,1_999_999_999L),"greeting debounce");
        check(gate.allow(Cue.ENTER,2_000_000_000L),"greeting boundary");
        String warning="InventoryTweaks is loaded. This will cause issues!";
        check(BogoNoticePolicy.knownConflict(Arrays.asList("\u00a7l! Warning from Inventory Bogosorter !","",warning,"Consider removing the mod and reload the game.")),"audited warning");
        check(!BogoNoticePolicy.knownConflict(Arrays.asList(warning,"NEW SECURITY WARNING")),"unknown warning remains");
        check(!BogoNoticePolicy.knownConflict(Arrays.asList("Other mod is loaded")),"other warning remains");
        check(!BogoNoticePolicy.knownConflict(null),"missing messages");check(!BogoNoticePolicy.knownConflict(Collections.emptyList()),"empty messages");
        check(!BogoNoticePolicy.knownConflict(Arrays.asList(warning,17)),"nontext messages");
        for(int[] size:new int[][]{{854,480},{1024,768},{1366,768},{1920,1001},{1650,860},{480,854},{2560,1080},{640,480}}){
            CoverFit c=new CoverFit(1207,830,size[0],size[1]);
            check(c.u0>=0&&c.v0>=0&&c.u1<=1&&c.v1<=1,"UV bounded");
            double ratio=(c.u1-c.u0)*1207/((c.v1-c.v0)*830);check(Math.abs(ratio-(double)size[0]/size[1])<1e-9,"no distortion");
            check(Math.abs(c.u1-c.u0-1)<1e-9||Math.abs(c.v1-c.v0-1)<1e-9,"cover fills canvas");
        }
        boolean rejected=false;try{new CoverFit(0,1,1,1);}catch(IllegalArgumentException ex){rejected=true;}check(rejected,"invalid image rejected");
        System.out.println("RESULT ui-feature-checks="+checks+" failures=0");
    }
}
