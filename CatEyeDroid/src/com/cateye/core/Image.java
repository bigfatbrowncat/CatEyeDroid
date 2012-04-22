package com.cateye.core;

import android.os.Parcel;
import android.os.Parcelable;

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
		this.description = imageDescription;
		this.bitmap = bitmap;
	}
	
	public Image(Parcel in) {
		// TODO Auto-generated constructor stub
	}

	public void free()
	{
		if (this.bitmap != null)
			this.bitmap.free();
		
		if (this.description != null)
			this.description.free();
	}

}
