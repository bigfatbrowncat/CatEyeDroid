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
	 * Gets all the pixel data from the range 
	 * (x, y, x + width, y + height) of the image 
	 * and stores it in the buf. If buf is null,
	 * it will be allocated automatically.
	 * @param screenBuffer target buffer to draw into
	 * @return <code>true</code> if it's completed successfully, 
	 * <code>false</code> if the {@link #PreciseBitmapGetPixelsCallback.report()}
	 * returned <code>false</code> 
	 */
	boolean getPixelsBGRIntoIntBuffer(int[] screenBuffer, int x, int y, int screenWidth, int screenHeight, float brightness, float scale, PreciseBitmapGetPixelsCallback cb);
	
	void getPixelsRGBIntoByteBuffer(byte[] buf, int bytesPerLine, int x, int y, int screenWidth, int screenHeight, float brightness, float scale);
	
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
