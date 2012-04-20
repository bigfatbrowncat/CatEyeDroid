package com.cateye.ui.android;

import com.cateye.core.Image;
import com.cateye.core.jni.PreciseBitmap;
import com.cateye.core.jni.RawImageLoader;

import android.app.Application;
import android.os.Environment;

public class CatEyeApplication extends Application 
{
	private RawImageLoader imageLoader;
	private Image img;
	private PreciseBitmap pb;
	
	public PreciseBitmap getPreciseBitmap() { return pb; }
	
	protected void initBitmap()
	{
		imageLoader = new RawImageLoader();
		img = imageLoader.loadImageFromFile(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/IMG_1520.CR2");
		pb = (PreciseBitmap)imageLoader.loadPreciseBitmapForImage(img);
	}

	public CatEyeApplication() 
	{
		initBitmap();
	}
}
