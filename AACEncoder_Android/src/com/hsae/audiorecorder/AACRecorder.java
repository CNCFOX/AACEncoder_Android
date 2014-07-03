package com.hsae.audiorecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

public class AACRecorder {
	
	private static final String TAG = AACRecorder.class.getSimpleName();
	
	static {
//		System.loadLibrary("hsae_mp3lame");
		System.loadLibrary("hsae_aac_enc");
	}

	private static final int DEFAULT_SAMPLING_RATE = 22050;
	
	private static final int FRAME_COUNT = 160;
	
	/* Encoded bit rate. MP3 file will be encoded with bit rate 32kbps */ 
	private static final int BIT_RATE = 32;

	private AudioRecord audioRecord = null;

	private int bufferSize;

	private File mp3File;
	private File pcmFile;
	
	private RingBuffer ringBuffer;
	
	private byte[] buffer;

	private FileOutputStream os = null;
	FileOutputStream osPCM = null;

	private DataEncodeThread encodeThread;

	private int samplingRate;

	private int channelConfig;

	private PCMFormat audioFormat;
	
	private boolean isRecording = false;

	public AACRecorder(int samplingRate, int channelConfig,
			PCMFormat audioFormat) {
		this.samplingRate = samplingRate;
		this.channelConfig = channelConfig;
		this.audioFormat = audioFormat;
	}


	public AACRecorder() {
		this(DEFAULT_SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
				PCMFormat.PCM_16BIT);
	}


	public void startRecording() throws IOException {
		if (isRecording) return;
		Log.d(TAG, "Start recording");
		Log.d(TAG, "BufferSize = " + bufferSize);
		// Initialize audioRecord if it's null.
		if (audioRecord == null) {
			initAudioRecorder();
		}
		audioRecord.startRecording();
		
		new Thread() {

			@Override
			public void run() {
				isRecording = true;
				while (isRecording) {
					int bytes = audioRecord.read(buffer, 0, bufferSize);
					if (bytes > 0) {
						ringBuffer.write(buffer, bytes);
					}
				}
				
				// release and finalize audioRecord
				try {
					audioRecord.stop();
					audioRecord.release();
					audioRecord = null;

					// stop the encoding thread and try to wait
					// until the thread finishes its job
					Message msg = Message.obtain(encodeThread.getHandler(),
							DataEncodeThread.PROCESS_STOP);
					msg.sendToTarget();
					
					Log.d(TAG, "waiting for encoding thread");
					encodeThread.join();
					Log.d(TAG, "done encoding thread");
				} catch (InterruptedException e) {
					Log.d(TAG, "Faile to join encode thread");
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}			
		}.start();
	}


	public void stopRecording() throws IOException {
		Log.d(TAG, "stop recording");
		isRecording = false;
	}

	private void initAudioRecorder() throws IOException {
		
		int bytesPerFrame = audioFormat.getBytesPerFrame();
		/* Get number of samples. Calculate the buffer size (round up to the
		   factor of given frame size) */
		int frameSize = AudioRecord.getMinBufferSize(samplingRate,channelConfig, audioFormat.getAudioFormat()) / bytesPerFrame;
		Log.d(TAG, "Frame size: ---------1---" + frameSize);
		if (frameSize % FRAME_COUNT != 0) {
			frameSize = frameSize + (FRAME_COUNT - frameSize % FRAME_COUNT);
		Log.d(TAG, "Frame size: ---------2---" + frameSize);
		}
		bufferSize = frameSize * bytesPerFrame;

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				samplingRate, channelConfig, audioFormat.getAudioFormat(),
				bufferSize);
		
		// Setup RingBuffer. Currently is 10 times size of hardware buffer
		// Initialize buffer to hold data
		ringBuffer = new RingBuffer(10 * bufferSize);
		buffer = new byte[bufferSize];
		
		// Initialize lame buffer
		// The bit rate is 32kbps
//		SimpleLame.init(samplingRate, 1, samplingRate, BIT_RATE);
		Log.d(TAG, "AACEncoder.init-------------------------------before");
	//	AACEncoder.init(samplingRate, 2, samplingRate, BIT_RATE);
		//AACEncoder.init(16000,1,samplingRate,BIT_RATE,"/sdcard/t.aac");
		int ret= AACEncoder.init(44100, 2, 11025, 16, "/sdcard/sample.aac");
		Log.d(TAG, "AACEncoder.init-------------------------------after");
		// Initialize the place to put mp3 file
		String externalPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File directory = new File(externalPath + "/" + "AudioRecorder");
		if (!directory.exists()) {
			directory.mkdirs();
			Log.d(TAG, "Created directory");
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		pcmFile = new File(externalPath, "temp.pcm");
	//	mp3File = new File(directory, sdf.format(date)+".mp3");
	//	os = new FileOutputStream(mp3File);
		osPCM = new FileOutputStream(pcmFile, true);

		// Create and run thread used to encode data
		// The thread will 
		encodeThread = new DataEncodeThread(ringBuffer, os, osPCM, bufferSize);
		encodeThread.start();
		audioRecord.setRecordPositionUpdateListener(encodeThread, encodeThread.getHandler());
		audioRecord.setPositionNotificationPeriod(FRAME_COUNT);
	}
}