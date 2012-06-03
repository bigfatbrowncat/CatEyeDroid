package com.cateye.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;

public class ImageLoaderReporters
{
	private Map<RawImage, List<ImageLoaderReporter>> imageLoaderReporters = new HashMap<RawImage, List<ImageLoaderReporter>>(); 

	public synchronized void add(RawImage image, ImageLoaderReporter reporter)
	{
		if (!imageLoaderReporters.containsKey(image))
		{
			ArrayList<ImageLoaderReporter> reps = new ArrayList<ImageLoaderReporter>();
			imageLoaderReporters.put(image, reps);
			reps.add(reporter);
		}
		else
		{
			if (!imageLoaderReporters.get(image).contains(reporter))
			{
				imageLoaderReporters.get(image).add(reporter);
			}
			else
			{
				// Do nothing. It's already there 
			}
		}
	}
	
	public synchronized void remove(RawImage image, ImageLoaderReporter reporter)
	{
		if (imageLoaderReporters.containsKey(image))
		{
			if (imageLoaderReporters.get(image).contains(reporter))
			{
				imageLoaderReporters.get(image).remove(reporter);
			}
		}
	}
	
	/**
	 * Calls all reporters' {@link ImageLoaderReporter.reportSuccess()} with the <code>preciseBitmap</code> argument.
	 * @param image image to report for
	 * @param preciseBitmap bitmap which has been successfully loaded 
	 */
	public synchronized void callReportSuccessForImage(RawImage image, IPreciseBitmap preciseBitmap)
	{
		if (imageLoaderReporters.containsKey(image))
		{
			for (ImageLoaderReporter rep : imageLoaderReporters.get(image))
			{
				rep.reportSuccess(preciseBitmap);
			}
		}
	}

	public synchronized void callReportExceptionForImage(RawImage image, ImageLoaderException e)
	{
		if (imageLoaderReporters.containsKey(image))
		{
			for (ImageLoaderReporter rep : imageLoaderReporters.get(image))
			{
				rep.reportException(e);
			}
		}
	}
	
	public synchronized void callReportProgressForImage(RawImage image, int progress)
	{
		if (imageLoaderReporters.containsKey(image))
		{
			for (ImageLoaderReporter rep : imageLoaderReporters.get(image))
			{
				rep.reportProgress(progress);
			}
		}
	}	
}
