package com.cateye.core.jni;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ProgressListener;
import com.cateye.core.ImageDescription;
import com.cateye.core.IncorrectImageLoaderRelation;
import com.cateye.core.jni.PreciseBitmap;
import com.cateye.core.jni.exceptions.LibRawException;

public class RawImageLoader
{
	// Progress listeners
	private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
	private boolean divideBy2 = true;
	
	private Hashtable<RawImage, String> imageFileNames = new Hashtable<RawImage, String>();
	
	public void setDivideBy2(boolean value)
	{
		divideBy2 = value;
	}
	
	public boolean getDivideBy2()
	{
		return divideBy2;
	}
	
	public void addProgressListener(ProgressListener listener)
	{
		progressListeners.add(listener);
	}
	
	public void removeProgressListener(ProgressListener listener)
	{
		progressListeners.remove(listener);
	}
	
	// Don't change the signature! It's called from native code.
	protected boolean raiseProgress(float progress)
	{
		boolean res = true;
		for (ProgressListener listener : progressListeners)
		{
			if (!listener.reportProgress(progress)) res = false;
		}
		return res;
	}
	
	//# Methods
	public boolean canLoadFromFile(String fileName)
	{
		return true;
	}
	
	public RawImage createImageFromFile(String filename)
	{
		RawImage img = new RawImage(this);
		imageFileNames.put(img, filename);
		return img;
	}
	
	protected IPreciseBitmap loadPreciseBitmapForImage(RawImage img) throws LibRawException
	{
		if (imageFileNames.containsKey(img))
		{
			return loadPreciseBitmapFromFile(imageFileNames.get(img));
		}
		else
		{
			throw new IncorrectImageLoaderRelation(img, this);
		}
	}

	protected ImageDescription loadDescriptionForImage(RawImage img) throws LibRawException
	{
		if (imageFileNames.containsKey(img))
		{
			return loadImageDescriptionFromFile(imageFileNames.get(img));
		}
		else
		{
			throw new IncorrectImageLoaderRelation(img, this);
		}		
	}

	protected void forgetImage(RawImage img)
	{
		if (imageFileNames.containsKey(img))
		{
			imageFileNames.remove(img);
		}
		else
		{
			throw new IncorrectImageLoaderRelation(img, this);
		}				
	}
	
	/**
	 * Loads the bitmap from raw file
	 * @param filename The file name
	 */
	private native PreciseBitmap loadPreciseBitmapFromFile(String filename);	

	/**
	 * Loads the description from bitmap file
	 * @param filename The file name
	 */
	private native ImageDescription loadImageDescriptionFromFile(String filename);	
	
	static
	{
		System.loadLibrary("Core");
	}
	
}
