package com.cateye.core;

public interface IPreviewBitmap
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
	IPreviewBitmap clone() throws NativeHeapAllocationException, InvalidDataException;
	
	/**
	 * Releases native resources
	 * @throws InvalidDataException 
	 */
	void free() throws InvalidDataException;
}
