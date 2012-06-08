package com.cateye.ui;

import com.cateye.core.IPreciseBitmap;

public interface ImageProcessingReporter
{
	boolean reportProgress(float progress);
	void reportResult(IPreciseBitmap result);
}
