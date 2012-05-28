package com.cateye.core;

public interface PreciseBitmapGetPixelsCallback 
{
	/**
	 * This function will be called many times from getPixels... 
	 * native code to ask if we want to stop it
	 * @return Should return <code>true</code> to continue 
	 * and <code>false</code> to cancel.
	 */
	boolean report();
}
