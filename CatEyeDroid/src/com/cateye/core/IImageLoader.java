package com.cateye.core;

public interface IImageLoader
{
	void addProgressListener(IProgressListener listener);
	
	void removeProgressListener(IProgressListener listener);
	
	/**
	 * Should return true if current loader can load a specified file
	 */
	boolean canLoadFromFile(String fileName);
	
	/**
	 * Loads the image by a specified file name
	 */
	Image loadImageFromFile(String fileName);
	
	/**
	 * This is for IImageLoader<->Image interaction only.
	 * Loads description for the image from disk.
	 * 
	 * @param img Target image 
	 * @return The description
	 */
	ImageDescription loadDescriptionForImage(Image img);

	/**
	 * This is for IImageLoader<->Image interaction only.
	 * Loads precise bitmap for the image from disk.
	 * 
	 * @param img Target image 
	 * @return The description
	 */
	IPreciseBitmap loadPreciseBitmapForImage(Image img);

	/**
	 * This is for IImageLoader<->Image interaction only.
	 * Unlinks the image from image loader
	 * 
	 * @param img Target image 
	 * @return The description
	 */
	void forgetImage(Image img);
}