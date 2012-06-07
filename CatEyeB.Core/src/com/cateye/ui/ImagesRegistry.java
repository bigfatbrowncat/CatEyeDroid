package com.cateye.ui;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ProgressListener;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;
import com.cateye.core.jni.RawImageLoader;

public class ImagesRegistry
{
	public enum State { Idle, Working }
	public enum LoadingState { NotLoadedYet, LoadingInProgress, Loaded, LoadingError }
	
	private RawImageLoader imageLoader = new RawImageLoader();

	private Map<String, RawImage> registry = Collections.synchronizedMap(new HashMap<String, RawImage>());
	private Map<RawImage, LoadingState> loadingStates = Collections.synchronizedMap(new HashMap<RawImage, LoadingState>());
	private Map<RawImage, IPreciseBitmap> loadedBitmaps = Collections.synchronizedMap(new HashMap<RawImage, IPreciseBitmap>());
	
	private ImageLoaderReporters reporters = new ImageLoaderReporters();
	
	/**
	 * Checks if the registry is loading anything right now.
	 * @return If any image is loading now, returns <code>State.Working</code> 
	 * else returns <code>State.Idle</code>
	 */
	public State getState()
	{
		for (Map.Entry<RawImage, LoadingState> state : loadingStates.entrySet())
		{
			if (state.getValue() == LoadingState.LoadingInProgress)
			{
				return State.Working;
			}
		}
		return State.Idle;
	}
	
	public RawImage requestOrLoadImage(String filename) throws IOException
	{
		File imageFile = new File(filename);
		String canonicalPath = imageFile.getCanonicalPath();

		if (registry.containsKey(canonicalPath)) 
		{
			return registry.get(canonicalPath);
		}
		else
		{
			RawImage newImage = imageLoader.createImageFromFile(filename);
			registry.put(canonicalPath, newImage);
			loadingStates.put(newImage, LoadingState.NotLoadedYet);
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
	public synchronized LoadingState loadImage(final RawImage img, final ImageLoaderReporter reporter)
	{
		// Checking if the image needs loading
		if (loadingStates.get(img) == LoadingState.NotLoadedYet || loadingStates.get(img) == LoadingState.LoadingError)
		{
			// Creating the progress listener
			final ProgressListener imageLoadingProgressListener = new ProgressListener()
			{
				@Override
				public boolean reportProgress(float progress) 
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
					loadingStates.put(img, LoadingState.LoadingInProgress);
					
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
						loadingStates.put(img, LoadingState.LoadingError);
						
						// Reporting exception to the caller
						reporters.callReportExceptionForImage(img, resultException);
					}
					else
					{
						// Setting the image's loading state
						loadingStates.put(img, LoadingState.Loaded);

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
		else if (loadingStates.get(img) == LoadingState.LoadingInProgress)
		{
			// Adding the image loading reporter
			// (after the image has loaded by other, already started thread,
			// the result will be sent to the caller)
			reporters.add(img, reporter);
		}
		else if (loadingStates.get(img) == LoadingState.Loaded)
		{
			// If the image is loaded, its bitmap should be in the registry.
			// So we retrieve it from there and sending to caller.
			reporter.reportSuccess(loadedBitmaps.get(img));
		}
		
		// Returning the current loading state
		return loadingStates.get(img);
	}
	
	public synchronized void forgetImage(RawImage image)
	{
		if (registry.containsValue(image))
		{
			for (Map.Entry<String, RawImage> e : registry.entrySet())
			{
				if (e.getValue() == image) 
				{
					registry.remove(e.getKey());
					loadingStates.remove(image);
					loadedBitmaps.get(image).free();
					loadedBitmaps.remove(image);
					break;
				}
			}
		}
	}
	
	public synchronized void unregisterImageLoaderReporter(RawImage image, ImageLoaderReporter reporter)
	{
		reporters.remove(image, reporter);
	}

}
