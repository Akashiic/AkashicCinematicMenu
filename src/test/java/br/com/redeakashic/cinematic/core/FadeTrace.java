package br.com.redeakashic.cinematic.core;
import java.util.Locale;
public final class FadeTrace {
    public static void main(String[] args){
        MusicFade fade=new MusicFade();fade.aim(.4,2.5,0);
        System.out.println("seconds,gain");
        for(int i=0;i<=28000;i++){
            long now=i*1_000_000L;if(i==26000)fade.aim(0,.8,now);
            System.out.printf(Locale.ROOT,"%.3f,%.9f%n",i/1000.0,fade.sample(now));
        }
    }
}
