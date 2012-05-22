package com.cateye.core.jni;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ImageDescription;
import com.cateye.core.exceptions.ImageLoaderException;

public class RawImage
{
	protected ImageDescription description;
	protected IPreciseBitmap bitmap;
	protected RawImageLoader loader;
	
	/**
	 * @return the description of image
	 * @throws ImageLoaderException 
	 */
	public ImageDescription getDescription() throws ImageLoaderException
	{
		if (description == null && loader != null)
			description = loader.loadDescriptionForImage(this);
		
		return description;
	}
	
	/**
	 * @return the precise bitmap
	 * @throws ImageLoaderException 
	 */
	public IPreciseBitmap getBitmap() throws ImageLoaderException
	{
		if (bitmap == null && loader != null)
			bitmap = loader.loadPreciseBitmapForImage(this);
		
		return bitmap;
	}
	
	protected RawImage(RawImageLoader loader)
	{
		if (loader == null)
			throw new IllegalArgumentException("loader shouldn't be null");
		
		this.loader = loader;
	}

	protected RawImage(ImageDescription imageDescription, IPreciseBitmap bitmap)
	{
		this.description = imageDescription;
		this.bitmap = bitmap;
	}
	
	public void dispose()
	{
		loader.forgetImage(this);

		if (this.bitmap != null)
			this.bitmap.free();
		
		if (this.description != null)
			this.description.free();
		
	}

}
