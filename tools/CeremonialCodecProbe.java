import java.io.File;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.SoundBuffer;
public final class CeremonialCodecProbe {
    public static void main(String[] args)throws Exception{
        String[] cues={"focus","focus_b","select","back","enter","connect"};double[] expected={1.05,1.1,1.9,1.4,3.6,2.8};
        for(int i=0;i<cues.length;i++){
            CodecJOrbis codec=new CodecJOrbis();long bytes=0;int chunks=0;
            try{if(!codec.initialize(new File(args[0],cues[i]+".ogg").toURI().toURL()))throw new AssertionError("init "+cues[i]);
                while(!codec.endOfStream()){SoundBuffer b=codec.read();if(b==null){if(!codec.endOfStream())throw new AssertionError("null PCM");break;}bytes+=b.audioData.length;chunks++;b.cleanup();if(chunks>5000)throw new AssertionError("unbounded codec");}
                double duration=bytes/(double)codec.getAudioFormat().getFrameSize()/codec.getAudioFormat().getFrameRate();if(Math.abs(duration-expected[i])>.06)throw new AssertionError("duration "+cues[i]+" "+duration);
                System.out.println("DECODED "+cues[i]+" seconds="+duration+" chunks="+chunks);
            }finally{codec.cleanup();}
        }System.out.println("RESULT actual_pack_jorbis cues=6 failures=0");
    }
}
