package com.cateye.ui.android;

import com.cateye.core.Image;
import com.cateye.core.jni.PreciseBitmap;
import com.cateye.core.jni.RawImageLoader;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

public class CatEyeApplication extends Application 
{
	private RawImageLoader imageLoader;
	private Image img = null;
	private PreciseBitmap pb;
	
	public PreciseBitmap getPreciseBitmap() 
	{
		if (img == null)
		{
			img = imageLoader.loadImageFromFile(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/IMG_1520.CR2");			
			pb = (PreciseBitmap)imageLoader.loadPreciseBitmapForImage(img);
		}
		return pb; 
	}

	public CatEyeApplication() 
	{
		imageLoader = new RawImageLoader();
	}
}
