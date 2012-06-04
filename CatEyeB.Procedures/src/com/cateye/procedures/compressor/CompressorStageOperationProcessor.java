package com.cateye.procedures.compressor;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;

public class CompressorStageOperationProcessor
{
	public int calculateEffort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public native void process(CompressorStageOperation params, IPreciseBitmap bitmap, IProgressListener progressListener);

	static
	{
		System.loadLibrary("Procedures");
	}
}
