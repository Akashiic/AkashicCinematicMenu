package br.com.redeakashic.cinematic.core;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/** Bounded first-page validation only; a full decoder test remains a separate gate. */
public final class VorbisHeader {
    public final int channels, sampleRate;
    private VorbisHeader(int channels,int rate){this.channels=channels;sampleRate=rate;}
    private static void readFully(InputStream in,byte[] dst,int offset,int count)throws IOException{
        while(count>0){int n=in.read(dst,offset,count);if(n<0)throw new EOFException("Truncated Ogg page");
            if(n==0){int one=in.read();if(one<0)throw new EOFException("Truncated Ogg page");dst[offset++]=(byte)one;count--;}
            else{offset+=n;count-=n;}}
    }
    private static long u32(byte[] b,int i){return (b[i]&255L)|((b[i+1]&255L)<<8)|((b[i+2]&255L)<<16)|((b[i+3]&255L)<<24);}
    public static VorbisHeader read(InputStream in)throws IOException{
        if(in==null)throw new IOException("No audio resource");
        byte[] header=new byte[27];readFully(in,header,0,27);
        if(header[0]!='O'||header[1]!='g'||header[2]!='g'||header[3]!='S'||header[4]!=0||header[5]!=2)
            throw new IOException("Expected initial Ogg BOS page");
        int segments=header[26]&255;if(segments<1)throw new IOException("Empty Ogg BOS page");
        byte[] lacing=new byte[segments];readFully(in,lacing,0,segments);int bytes=0,firstLength=0;boolean complete=false;
        for(byte l: lacing){int n=l&255;bytes+=n;if(!complete){firstLength+=n;if(n<255)complete=true;}}
        if(!complete||firstLength!=30)throw new IOException("Invalid Vorbis identification packet length");
        byte[] page=new byte[27+segments+bytes];System.arraycopy(header,0,page,0,27);System.arraycopy(lacing,0,page,27,segments);
        readFully(in,page,27+segments,bytes);long checksum=u32(page,22);for(int i=22;i<26;i++)page[i]=0;
        int crc=0;for(byte b:page){crc^=(b&255)<<24;for(int i=0;i<8;i++)crc=(crc<<1)^((crc&0x80000000)!=0?0x04C11DB7:0);}
        if((crc&0xffffffffL)!=checksum)throw new IOException("Ogg BOS page CRC mismatch");
        int p=27+segments;byte[] signature={1,'v','o','r','b','i','s'};for(int i=0;i<signature.length;i++)if(page[p+i]!=signature[i])throw new IOException("Not Ogg/Vorbis");
        if(u32(page,p+7)!=0)throw new IOException("Unsupported Vorbis version");
        int channels=page[p+11]&255;long rate=u32(page,p+12);
        if((channels!=1&&channels!=2)||(rate!=44100&&rate!=48000))throw new IOException("Expected mono/stereo Vorbis at 44.1/48 kHz");
        int shortExp=page[p+28]&15,longExp=(page[p+28]>>4)&15;
        if(shortExp<6||longExp>13||shortExp>longExp||(page[p+29]&1)!=1)throw new IOException("Invalid Vorbis block/framing metadata");
        return new VorbisHeader(channels,(int)rate);
    }
}
