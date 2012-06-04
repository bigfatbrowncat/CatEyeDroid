package com.cateye.procedures.limiter;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;

public class LimiterStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native void process(LimiterStageOperation params, IPreciseBitmap bitmap, IProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
