package com.cateye.procedures.hsb;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ProgressListener;

public class HSBStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native void process(HSBStageOperation params, IPreciseBitmap bitmap, ProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
