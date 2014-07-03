package com.hsae.audiorecorder;


public class AACEncoder {
	
	

//	public native static int encode(short[] buffer,int samples, byte[] aacbuf) ;
//	public native static int encode(byte[] buffer,int samples, byte[] aacbuf) ;
//	public native static void close();
//	public native static int uninit();
//	public native static int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);
//	public native static int flush(byte[] aacbuf);
//	public static void init(int inSamplerate, int outChannel, int outSamplerate, int outBitrate) {
//	init(inSamplerate, outChannel, outSamplerate, outBitrate, 7);
//	}
//	public native static void init(int inSamplerate, int outChannel, int outSamplerate, int outBitrate, int quality);
	
	
    public native static int init(int bitrate, int channels,
            int sampleRate, int bitsPerSample, String outputFile);
    public native static int encode(short[] inputArray,byte[] outArray);
    public native static int uninit();
}
