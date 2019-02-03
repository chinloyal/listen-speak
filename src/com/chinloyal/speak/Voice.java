package com.chinloyal.speak;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.darkprograms.speech.synthesiser.SynthesiserV2;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;


public abstract class Voice {
	private SynthesiserV2 synthesizer;
	private AdvancedPlayer player;
	protected static boolean mute = false;
	
	public Voice(String google_api_key) {
		synthesizer = new SynthesiserV2(google_api_key);
		synthesizer.setLanguage("en-in");
		synthesizer.setSpeed(0.8);
		player = null;
	}
	
	/**
	 * Calls the MaryTTS to say the given text
	 * 
	 * @param text
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void speak(String text) throws InterruptedException, ExecutionException{		
		//Create a new Thread because JLayer is running on the current Thread and will make the application to lag
		if(!mute) {
			
			Callable<Integer> callable = new Callable<Integer>() {
				public Integer call() throws IOException, JavaLayerException {
					//Create a JLayer instance
					player = new AdvancedPlayer(synthesizer.getMP3Data(text));
					player.play();
					return 0;
				}
			};
			
			ExecutorService exec = Executors.newSingleThreadExecutor();
		
			Future<Integer> future = exec.submit(callable);
			future.get();
			
			/*//We don't want the application to terminate before this Thread terminates
			thread.setDaemon(false);
			
			//Start the Thread
			thread.start();*/
		}
		
		
	}

	public AdvancedPlayer getPlayer() {
		return player;
	}
	
	/**
	 * This method should pass it's output to the speak method as input.
	 * @param String input
	 * @return String
	 */
	public abstract String respond(String input);
	
}
