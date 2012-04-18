package com.cateye.core.jni;

import java.util.Date;

import com.cateye.core.IPreviewBitmap;
import com.cateye.core.ImageDescription;
import com.cateye.core.jni.LibraryLoader;

class RawImageDescription extends ImageDescription
{
	protected IPreviewBitmap thumbnail;
	protected int flip;
	protected float isoSpeed;
	protected float shutter;
	protected float aperture;
	protected float focalLength;
	protected long timeStamp;
	protected int shotOrder;
	protected String description;
	protected String artist;
	protected String cameraMaker;
	protected String cameraModel;
	

/*	protected void setDate(int value)
	{
		this.timeStamp = new Date(value * 1000L);
	}
*/

	/**
	 * @return the thumbnail
	 */
	public IPreviewBitmap getThumbnail()
	{
		return thumbnail;
	}
	
	/**
	 * @return the iso speed
	 */
	public float getIsoSpeed()
	{
		return isoSpeed;
	}
	
	/**
	 * @return the shutter
	 */
	public float getShutter()
	{
		return shutter;
	}
	
	/**
	 * @return the aperture
	 */
	public float getAperture()
	{
		return aperture;
	}
	
	/**
	 * @return the focal length
	 */
	public float getFocalLength()
	{
		return focalLength;
	}
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp()
	{
		return new Date(timeStamp * 1000L);
	}
	
	/**
	 * @return the shot order
	 */
	public int getShotOrder()
	{
		return shotOrder;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * @return the artist
	 */
	public String getArtist()
	{
		return artist;
	}
	
	/**
	 * @return the camera maker
	 */
	public String getCameraMaker()
	{
		return cameraMaker;
	}
	
	/**
	 * @return the camera model
	 */
	public String getCameraModel()
	{
		return cameraModel;
	}
	
	/**
	 * @return the flip
	 */
	public int getFlip()
	{
		return flip;
	}
	
	@Override
	public void free()
	{
		if (thumbnail != null)
		{
			thumbnail.free();
		}
	}
	
	static
	{
		LibraryLoader.attach("Raw.CatEyeImageLoader");		
	}

}
