package com.cateye.ui.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;
import com.cateye.core.jni.RawImageLoader;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class CatEyeApplication extends Application 
{
	public enum LoadingState { NotLoadedYet, LoadingInProgress, Loaded, LoadingError }
	
	private RawImageLoader imageLoader = new RawImageLoader();

	private Map<String, RawImage> imagesRegistry = Collections.synchronizedMap(new HashMap<String, RawImage>());
	private Map<RawImage, LoadingState> imagesLoadingState = Collections.synchronizedMap(new HashMap<RawImage, LoadingState>());
	private Map<RawImage, IPreciseBitmap> loadedBitmaps = Collections.synchronizedMap(new HashMap<RawImage, IPreciseBitmap>());
	
	private ImageLoaderReporters reporters = new ImageLoaderReporters();
	
	public RawImage requestImageFromRegistry(String filename) throws IOException
	{
		File imageFile = new File(filename);
		String canonicalPath = imageFile.getCanonicalPath();

		if (imagesRegistry.containsKey(canonicalPath)) 
		{
			return imagesRegistry.get(canonicalPath);
		}
		else
		{
			RawImage newImage = imageLoader.createImageFromFile(filename);
			imagesRegistry.put(canonicalPath, newImage);
			imagesLoadingState.put(newImage, LoadingState.NotLoadedYet);
			return newImage;
		}
	}
	
	/**
	 * Starts the image loading process if it has not been started yet. 
	 * If ther loading process is in progress, adds the caller's reporter its 
	 * reporters list. If the image is loaded successfully, just calls reporter's
	 * <code>reportSuccess</code> with the bitmap.
	 * @param activity caller activity
	 * @param filename name of the image to be loaded
	 * @param reporter caller's reporter object
	 * @return The current loading progress status
	 * @throws IOException
	 */
	public synchronized LoadingState loadImageForActivity(String filename, final ImageLoaderReporter reporter) throws IOException
	{
		// Retrieving image from the registry 
		final RawImage img = requestImageFromRegistry(filename);
		
		// Checking if the image needs loading
		if (imagesLoadingState.get(img) == LoadingState.NotLoadedYet || imagesLoadingState.get(img) == LoadingState.LoadingError)
		{
			// Creating the progress listener
			final IProgressListener imageLoadingProgressListener = new IProgressListener()
			{
				@Override
				public boolean invoke(Object sender, final float progress)
				{
					int p = (int)(progress * 100);
					reporters.callReportProgressForImage(img, p);
					return true;
				}
			};
			
			// Adding the new listener to the loader
			imageLoader.addProgressListener(imageLoadingProgressListener);
			
			// Adding the image loading reporter
			reporters.add(img, reporter);
			
			// Creating a runnable which will load the image for us 			
    		Runnable imageLoadingRunnable = new Runnable()
			{
    			ImageLoaderException exception = null;
				@Override
				public void run() 
				{
					// At start...
					// Showing the progress dialog (reporting the 0% progress)
					reporters.callReportProgressForImage(img, 0);
					
					// Setting the image's loading state
					imagesLoadingState.put(img, LoadingState.LoadingInProgress);
					
					// Loading the bitmap itself
					IPreciseBitmap pb = null;
		    		try 
		    		{
						pb = img.getBitmap();
					}
		    		catch (ImageLoaderException e) 
		    		{
		    			// Saving the exception
		    			exception = e;
						return;
					}
		    		
					// At final...
		    		final IPreciseBitmap result = pb;
		    		final ImageLoaderException resultException = exception;

		    		// Removing the progress handler 
					imageLoader.removeProgressListener(imageLoadingProgressListener);

					// Checking if exception has occured
					if (resultException != null)
					{
						// Setting the image's loading state
						imagesLoadingState.put(img, LoadingState.LoadingError);
						
						// Reporting exception to the caller
						reporters.callReportExceptionForImage(img, resultException);
					}
					else
					{
						// Setting the image's loading state
						imagesLoadingState.put(img, LoadingState.Loaded);

						// Adding the result to the registry
						loadedBitmaps.put(img, result);
						
						// Reporting the result to the caller
						reporters.callReportSuccessForImage(img, result);
						
					}
		    		
				}	
			};
			
			// Running the loader thread
			new Thread(imageLoadingRunnable).start();
			
		}
		else if (imagesLoadingState.get(img) == LoadingState.LoadingInProgress)
		{
			// Adding the image loading reporter
			// (after the image has loaded by other, already started thread,
			// the result will be sent to the caller)
			reporters.add(img, reporter);
		}
		else if (imagesLoadingState.get(img) == LoadingState.Loaded)
		{
			// If the image is loaded, its bitmap should be in the registry.
			// So we retrieve it from there and sending to caller.
			reporter.reportSuccess(loadedBitmaps.get(img));
		}
		
		// Returning the current loading state
		return imagesLoadingState.get(img);
	}
	
	public synchronized void forgetImage(String filename) throws IOException
	{
		RawImage image = requestImageFromRegistry(filename);
		
		if (imagesRegistry.containsKey(filename))
		{
			imagesRegistry.remove(filename);
			imagesLoadingState.remove(image);
			loadedBitmaps.get(image).free();
			loadedBitmaps.remove(image);
		}
	}
	
	public synchronized void unregisterImageLoaderReporter(RawImage image, ImageLoaderReporter reporter)
	{
		reporters.remove(image, reporter);
	}
	
	public CatEyeApplication() 
	{
	}
}
