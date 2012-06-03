package com.cateye.ui.android;

import com.cateye.ui.ImagesRegistry;

import android.app.Application;

public class CatEyeApplication extends Application 
{
	private ImagesRegistry registry = new ImagesRegistry();
	
	public ImagesRegistry getRegistry()
	{
		return registry;
	}
	
	public CatEyeApplication() 
	{
	}
}
