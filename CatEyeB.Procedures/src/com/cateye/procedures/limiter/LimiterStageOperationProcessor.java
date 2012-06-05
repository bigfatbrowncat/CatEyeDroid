package com.cateye.procedures.limiter;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ProgressListener;

public class LimiterStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native void process(LimiterStageOperation params, IPreciseBitmap bitmap, ProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
