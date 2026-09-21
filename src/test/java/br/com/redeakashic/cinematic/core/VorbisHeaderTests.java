package br.com.redeakashic.cinematic.core;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

public final class VorbisHeaderTests {
    private static int checks;
    private static void check(boolean b,String label){checks++;if(!b)throw new AssertionError(label);}
    private static void reject(byte[] b,String label){boolean thrown=false;try{VorbisHeader.read(new ByteArrayInputStream(b));}catch(IOException e){thrown=true;}check(thrown,label);}
    private static byte[] page(byte[] all){int segments=all[26]&255,body=0;for(int i=0;i<segments;i++)body+=all[27+i]&255;return Arrays.copyOf(all,27+segments+body);}
    private static void crc(byte[] b){for(int i=22;i<26;i++)b[i]=0;int crc=0;for(byte v:b){crc^=(v&255)<<24;for(int i=0;i<8;i++)crc=(crc<<1)^((crc&0x80000000)!=0?0x04C11DB7:0);}for(int i=0;i<4;i++)b[22+i]=(byte)(crc>>>(i*8));}
    public static void main(String[] args)throws Exception{
        byte[] recording=Files.readAllBytes(Paths.get(args[0])),original=page(recording);
        VorbisHeader v=VorbisHeader.read(new ByteArrayInputStream(recording));check(v.channels==2&&v.sampleRate==44100,"supplied track prepared stereo44.1k");
        final ByteArrayInputStream underlying=new ByteArrayInputStream(recording);
        InputStream bounded=new FilterInputStream(underlying){@Override public int read(byte[] b,int off,int len){return underlying.read(b,off,Math.min(3,len));}};
        v=VorbisHeader.read(bounded);check(v.channels==2&&underlying.available()==recording.length-original.length,"short reads complete only first page");
        for(int n=0;n<original.length;n++)reject(Arrays.copyOf(original,n),"truncated page "+n);
        byte[] c=original.clone();c[0]='X';reject(c,"wrong magic");
        c=original.clone();c[c.length-1]^=1;reject(c,"CRC damage");
        c=original.clone();c[5]=0;crc(c);reject(c,"not BOS");
        int offset=27+(original[26]&255);
        c=original.clone();c[offset+1]='X';crc(c);reject(c,"not Vorbis");
        c=original.clone();c[offset+11]=8;crc(c);reject(c,"unsupported multichannel");
        c=original.clone();c[offset+7]=1;crc(c);reject(c,"unsupported codec version");
        c=original.clone();c[offset+29]=0;crc(c);reject(c,"framing bit missing");
        c=original.clone();c[offset+28]=0;crc(c);reject(c,"bad blocksize");
        c=original.clone();c[offset+11]=1;crc(c);check(VorbisHeader.read(new ByteArrayInputStream(c)).channels==1,"valid mono metadata");
        c=original.clone();int rate=48000;for(int i=0;i<4;i++)c[offset+12+i]=(byte)(rate>>>(8*i));crc(c);
        check(VorbisHeader.read(new ByteArrayInputStream(c)).sampleRate==48000,"valid48k metadata");
        System.out.println("RESULT vorbis_header_checks="+checks+" failures=0; actual file plus malformed fixtures; not full decoder approval");
    }
}
