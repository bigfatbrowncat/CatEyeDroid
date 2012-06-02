package com.cateye.ui.android;

import java.util.HashMap;

import com.cateye.core.jni.RawImage;
import com.cateye.core.jni.RawImageLoader;

import android.app.Application;

public class CatEyeApplication extends Application 
{
	private RawImageLoader imageLoader = new RawImageLoader();

	private HashMap<Integer, RawImage> loadedImagesRegistry = new HashMap<Integer, RawImage>(); 
	private int loadedImagesLastId = 0;

	public RawImageLoader getImageLoader()
	{
		return imageLoader;
	}
	
	public RawImage loadImage(String fileName)
	{
		RawImage img = imageLoader.createImageFromFile(fileName);
		loadedImagesRegistry.put(loadedImagesLastId, img);
		loadedImagesLastId++;
		return img;
	}
	
	public void forgetImage(RawImage image)
	{
		image.dispose();
		loadedImagesRegistry.remove(image);
	}
	
	public RawImage getImageById(int id)
	{
		return loadedImagesRegistry.get(id);
	}
	
	public int getIdOfImage(RawImage img)
	{
		for (Integer key : loadedImagesRegistry.keySet())
		{
			if (loadedImagesRegistry.get(key) == img)
			{
				return key;
			}
		}
		return -1;
	}
	
	
	
	public CatEyeApplication() 
	{
	}
}
