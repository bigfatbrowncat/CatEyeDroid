package com.cateye.procedures.compressor;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ProgressListener;

public class CompressorStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native boolean process(CompressorStageOperation params, IPreciseBitmap bitmap, ProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
