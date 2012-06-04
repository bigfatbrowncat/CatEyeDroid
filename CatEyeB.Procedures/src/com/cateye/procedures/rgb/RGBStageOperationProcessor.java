package com.cateye.procedures.rgb;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;

public class RGBStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native void process(RGBStageOperation params, IPreciseBitmap bitmap, IProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
