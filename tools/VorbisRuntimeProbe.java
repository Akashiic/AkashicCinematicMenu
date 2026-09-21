import java.io.File;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.SoundBuffer;
public final class VorbisRuntimeProbe {
    public static void main(String[] args)throws Exception{
        CodecJOrbis codec=new CodecJOrbis();long total=0;int buffers=0;int max=0;
        if(!codec.initialize(new File(args[0]).toURI().toURL()))throw new AssertionError("JOrbis init");
        System.out.println("FORMAT "+codec.getAudioFormat());
        try{
            while(!codec.endOfStream()){
                SoundBuffer b=codec.read();if(b==null){if(!codec.endOfStream())throw new AssertionError("Unexpected null chunk");break;}
                if(b.audioData==null)throw new AssertionError("Missing PCM");total+=b.audioData.length;buffers++;max=Math.max(max,b.audioData.length);b.cleanup();
                if(buffers>100000)throw new AssertionError("Unbounded decoding");
            }
            double seconds=total/(double)codec.getAudioFormat().getFrameSize()/codec.getAudioFormat().getFrameRate();
            if(Math.abs(seconds-161.8808163265306)>.1)throw new AssertionError("Runtime codec duration mismatch "+seconds);
            System.out.println("RESULT decoded_seconds="+seconds+" chunks="+buffers+" max_buffer="+max+" failures=0");
        }finally{codec.cleanup();}
    }
}
