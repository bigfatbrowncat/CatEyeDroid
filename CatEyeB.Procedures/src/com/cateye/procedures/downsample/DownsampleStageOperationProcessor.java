package com.cateye.procedures.downsample;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;

public class DownsampleStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native void process(DownsampleStageOperation params, IPreciseBitmap bitmap, IProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
