package com.cateye.core;

public class Image
{
	protected ImageDescription description;
	protected IPreciseBitmap bitmap;
	protected IImageLoader loader;
	
	/**
	 * @return the description of image
	 */
	public ImageDescription getDescription()
	{
		if (description == null && loader != null)
			description = loader.loadDescriptionForImage(this);
		
		return description;
	}
	
	/**
	 * @return the precise bitmap
	 */
	public IPreciseBitmap getBitmap()
	{
		if (bitmap == null && loader != null)
			bitmap = loader.loadPreciseBitmapForImage(this);
		
		return bitmap;
	}
	
	public Image(IImageLoader loader)
	{
		if (loader == null)
			throw new IllegalArgumentException("loader shouldn't be null");
		
		this.loader = loader;
	}

	public Image(ImageDescription imageDescription, IPreciseBitmap bitmap)
	{
/*		if (imageDescription == null)
			throw new IllegalArgumentException("imageDescription shouldn't be null");
		
		if (bitmap == null)
			throw new IllegalArgumentException("bitmap shouldn't be null");
*/		
		this.description = imageDescription;
		this.bitmap = bitmap;
	}
	
	public void free()
	{
		if (this.bitmap != null)
			this.bitmap.free();
		
		if (this.description != null)
			this.description.free();
	}
}
