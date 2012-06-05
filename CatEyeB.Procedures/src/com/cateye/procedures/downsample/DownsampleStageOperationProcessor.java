package com.cateye.procedures.downsample;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ProgressListener;

public class DownsampleStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native void process(DownsampleStageOperation params, IPreciseBitmap bitmap, ProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
