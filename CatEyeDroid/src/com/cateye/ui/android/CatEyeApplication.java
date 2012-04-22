package com.cateye.ui.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.cateye.core.Image;
import com.cateye.core.jni.PreciseBitmap;
import com.cateye.core.jni.RawImageLoader;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

public class CatEyeApplication extends Application 
{
	private RawImageLoader imageLoader = new RawImageLoader();

	private HashMap<Integer, Image> loadedImagesRegistry = new HashMap<Integer, Image>(); 
	private int loadedImagesLastId = 0;
	
	public Image loadImage(String fileName)
	{
		Image img = imageLoader.loadImageFromFile(fileName);
		loadedImagesRegistry.put(loadedImagesLastId, img);
		loadedImagesLastId++;
		return img;
	}
	
	public void forgetImage(Image image)
	{
		image.free();
		loadedImagesRegistry.remove(image);
	}
	
	public Image getImageById(int id)
	{
		return loadedImagesRegistry.get(id);
	}
	
	public int getIdOfImage(Image img)
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
