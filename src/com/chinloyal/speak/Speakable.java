package com.chinloyal.speak;

public interface Speakable {
	/**
	 * Perform some action before the start of speaking.
	 */
	void onSpeakStart();
	
	/**
	 * Perform some action after speaking ends.
	 */
	void onSpeakEnd();
}
