package com.chinloyal.listen;

public interface Hearable {
	/**
	 * Perform some action after something is heard
	 * @param responseText
	 */
	void onRespond(String responseText);
}
