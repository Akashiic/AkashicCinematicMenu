package br.com.redeakashic.cinematic.core;
import static br.com.redeakashic.cinematic.core.ConnectionWatchPolicy.Decision.*;
public final class ConnectionWatchTests {
    private static int checks;
    private static void test(String n,boolean b){checks++;if(!b)throw new AssertionError(n);System.out.println("PASS "+n);}
    public static void main(String[] args){
        test("one pending connector admitted",ConnectionWatchPolicy.canAdmit(0));
        test("parallel attempt blocked",!ConnectionWatchPolicy.canAdmit(1)&&!ConnectionWatchPolicy.canAdmit(99));
        test("active uncancelled connection preserved",ConnectionWatchPolicy.decide(false,false,true,true,false,true,false)==WAIT);
        test("cancel before manager publication stays watched",ConnectionWatchPolicy.decide(true,false,false,false,false,false,false)==WAIT);
        test("late open manager must close",ConnectionWatchPolicy.decide(true,false,true,true,false,false,false)==CLOSE_CANCELLED);
        test("close request is not closure",ConnectionWatchPolicy.decide(true,false,true,true,true,false,false)==CLOSE_CANCELLED);
        test("closed manager with worker alive stays watched",ConnectionWatchPolicy.decide(true,false,true,false,false,false,false)==WAIT);
        test("cancelled closed worker releases watch",ConnectionWatchPolicy.decide(true,false,true,false,true,false,false)==RELEASE);
        test("DNS failure after cancellation releases watch",ConnectionWatchPolicy.decide(true,false,false,false,true,false,false)==RELEASE);
        test("return to our menu requires cancellation",ConnectionWatchPolicy.decide(false,true,true,true,false,false,false)==CLOSE_CANCELLED);
        test("successful world is not cancelled",ConnectionWatchPolicy.decide(false,false,true,true,true,false,true)==RELEASE);
        test("normal terrain transition transfers ownership",ConnectionWatchPolicy.decide(false,false,true,true,true,false,false)==RELEASE);
        test("cancel wins over late world transition",ConnectionWatchPolicy.decide(true,false,true,true,true,false,true)==CLOSE_CANCELLED);
        boolean invalid=false;try{ConnectionWatchPolicy.canAdmit(-1);}catch(IllegalArgumentException e){invalid=true;}test("negative pending rejected",invalid);
        invalid=false;try{ConnectionWatchPolicy.decide(false,false,false,true,false,true,false);}catch(IllegalArgumentException e){invalid=true;}test("impossible channel state rejected",invalid);
        System.out.println("RESULT checks="+checks+" failures=0");
    }
}
