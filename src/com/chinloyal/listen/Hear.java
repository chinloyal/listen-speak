package com.chinloyal.listen;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import net.sourceforge.javaflacencoder.FLACFileWriter;

public abstract class Hear {
	
	private GSpeechDuplex duplex;
	private Microphone mic;
	private File audioFile;
	private int recordTime;
	
	private List<Hearable> listeners = new ArrayList<>();
	
	public Hear(String google_api_key) {
		duplex = new GSpeechDuplex(google_api_key);//Initialize Google API
		mic = new Microphone(FLACFileWriter.FLAC);//Instantiate microphone and have it record FLAC file.
		audioFile = new File("UserAudio.flac");//The File to record the buffer to. 
		recordTime = 10000;// 10 seconds (default)
	}
	
	/**
	 * Adds response listener to Google Duplex
	 */
	public void listen() {
		
		duplex.addResponseListener(new GSpeechResponseListener(){// Adds the listener
			public void onResponse(GoogleResponse gr){
				processResponse(gr);				
			}
		});
		
	}
	/**
	 * Record audio to send to Google's API
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void record() throws InterruptedException, IOException {
		mic.captureAudioToFile(audioFile);//Begins recording
		recording();

		Thread.sleep(recordTime);//Records for a variable amount of seconds
		mic.close();//Stops recording
		closing();
		
		
		byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());//Saves data into memory.
		duplex.recognize(data, (int)mic.getAudioFormat().getSampleRate());//Sends voice recording to Google
		mic.getAudioFile().delete();//Deletes Buffer file
	}
	
	
	
	public int getRecordTime() {
		return recordTime;
	}

	/**
	 * Time should be in milliseconds (cannot be more than 15 seconds).
	 * 
	 * @param recordTime
	 */
	public void setRecordTime(int recordTime) {
		if(recordTime > 15000)
			throw new IllegalArgumentException("The recording time cannot be more than 15 seconds");
		
		this.recordTime = recordTime;
	}
	/**
	 * Fire event
	 * 
	 * @param String response
	 */
	private void processResponse(GoogleResponse gResponse) {
		String processed = "";
		int index = gResponse.getResponse().indexOf("\"");
		
		if(index > 0) {
			processed = gResponse.getResponse().substring(0, index);
		}else {
			if(gResponse.getResponse().length() > 0) {
				processed = gResponse.getResponse();
			}
		}
		
		for(Hearable listener : listeners) {
			listener.onRespond(processed);
		}
	}
	
	/**
	 * Add a listener to execute once the response has been processed.
	 * 
	 * @param Hearable listener
	 */
	public void addRespondListener(Hearable listener) {
		listeners.add(listener);
	}

	/**
	 * Execute some action at the beginning of the recording
	 */
	public abstract void recording();
	
	/**
	 * Execute some action at the end of the recording
	 */
	public abstract void closing();
}
