package com.cateye.ui;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.exceptions.ImageLoaderException;

public interface ImageLoaderReporter 
{
	void reportProgress(int progress);
	void reportSuccess(IPreciseBitmap preciseBitmap);
	void reportException(ImageLoaderException e);
}
