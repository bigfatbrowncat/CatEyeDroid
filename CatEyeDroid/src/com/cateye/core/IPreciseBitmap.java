package com.cateye.core;

public interface IPreciseBitmap
{
	/**
	 * @return the width
	 */
	int getWidth();
	
	/**
	 * @return the height
	 */
	int getHeight();
	
	/**
	 * Allocates native resources
	 */
	void alloc(int width, int height) throws NativeHeapAllocationException;
	
	/**
	 * Creates a clone of the bitmap
	 */
	IPreciseBitmap clone() throws NativeHeapAllocationException, InvalidDataException;
	
	/**
	 * Releases native resources
	 * @throws InvalidDataException 
	 */
	void free() throws InvalidDataException;
}
