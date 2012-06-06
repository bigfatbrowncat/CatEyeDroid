package com.cateye.ui.swt;

import java.util.ArrayList;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ProgressListener;
import com.cateye.procedures.compressor.CompressorStageOperation;
import com.cateye.procedures.compressor.CompressorStageOperationProcessor;

public class ImageProcessor
{
	public enum State { Idle, Working }
	private IPreciseBitmap resultPreciseBitmap = null;
	private ArrayList<ImageProcessingReporter> reporters = new ArrayList<ImageProcessingReporter>(); 
	private State state = State.Idle;
	
	public State getState() { return state; }
	
	private void doProcessing(IPreciseBitmap sourcePreciseBitmap)
	{
		state = State.Working;
		
		// Freeing the previous result
		if (resultPreciseBitmap != null)
		{
			resultPreciseBitmap.free();
		}
		
		CompressorStageOperation cso = new CompressorStageOperation();
		CompressorStageOperationProcessor csop = new CompressorStageOperationProcessor();
		
		// Creating an image for the new result
		
		resultPreciseBitmap = sourcePreciseBitmap.copy();
		
		if (csop.process(cso, resultPreciseBitmap, new ProgressListener() 
			{
				@Override
				public boolean reportProgress(float progress)
				{
					// Report the processing progress. 
					// If any of the listeners return false, we return false. That will mean "cancel"
	
					boolean result = true;
					for (ImageProcessingReporter rpt : reporters)
					{
						if (!rpt.reportProgress(progress))
						{
							result = false;
						}
					}
					return result;
				}
			}))
		{
			for (ImageProcessingReporter rpt : reporters)
			{
				rpt.reportResult(resultPreciseBitmap);
			}
			
		}
		else
		{
			System.out.print("Processing cancelled.\n");
			
		}
		
		state = State.Idle;
	}
	
	/**
	 * Processes the selected image. 
	 * The <code>ImageProcessor</code> object should be idle to do that. 
	 * @param sourcePreciseBitmap image to process
	 * @return result image (equals to {@link #resultPreciseBitmap} value)
	 */
	public synchronized IPreciseBitmap processImage(final IPreciseBitmap sourcePreciseBitmap)
	{
		if (state == State.Idle)
		{
			reporters.clear();
			doProcessing(sourcePreciseBitmap);

			return resultPreciseBitmap;
		}
		else if (state == State.Working)
		{
			return null;
		}
		else 
		{
			throw new RuntimeException("Strange case");
		}
	}
	
	/**
	 * Starts the new processing thread.
	 * @param sourcePreciseBitmap the source image to apply processing on
	 * @param reporter the reporter which will receive the result
	 * @return <code>true</code> if the new thread has started, <code>false</code> otherwise. 
	 */
	public synchronized boolean startProcessingAsync(final IPreciseBitmap sourcePreciseBitmap, ImageProcessingReporter reporter)
	{
		if (state == State.Idle)
		{
			reporters.clear();
			// Adding the new reporter
			reporters.add(reporter);

			new Thread()
			{
				public void run() 
				{
					doProcessing(sourcePreciseBitmap);
				};
			}.start();	
			return true;
		}
		else if (state == State.Working)
		{
			return false;
		}
		else 
		{
			throw new RuntimeException("Strange case");
		}
	}
}
