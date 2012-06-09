package com.cateye.ui.android;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;
import com.cateye.ui.ImageLoaderReporter;
import com.cateye.ui.ImagesRegistry.LoadingState;

public class ImageActivity extends Activity 
{
    private String filename;
    private RawImage image;
	private PreciseBitmapView preciseBitmapView;
	private ProgressDialog loadingProgressDialog = null;
	private int imageLoadingProgress = 0;

	ImageLoaderReporter imageLoaderReporter = new ImageLoaderReporter() 
	{
		public void reportSuccess(final IPreciseBitmap preciseBitmap)
		{
			ImageActivity.this.runOnUiThread(new Runnable()
			{
				public void run() 
				{
					loadingProgressDialog.dismiss();
					ImageActivity.this.preciseBitmapView.setPreciseBitmap(preciseBitmap);
				}
			});
		}
		
		public void reportException(ImageLoaderException e) 
		{
			ImageActivity.this.runOnUiThread(new Runnable()
			{
				public void run() 
				{
					loadingProgressDialog.dismiss();
					// TODO Handle exception here!
				}
			});
		}

		public void reportProgress(final int progress)
		{
			ImageActivity.this.runOnUiThread(new Runnable()
			{
				public void run() 
				{
					if (!loadingProgressDialog.isShowing())
					{
						loadingProgressDialog.show();
					}
		
					final File imageFile = new File(filename); 
					imageLoadingProgress = progress;
					loadingProgressDialog.setProgress(progress);
		    		loadingProgressDialog.setMessage("Loading image " + imageFile.getName() + " (" + progress + "%)...");
				}
			});
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);

	    final CatEyeApplication application = ((CatEyeApplication) getApplication());
		
	    // Removing title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    // Loading UI
        setContentView(R.layout.raw_viewer_activity);
        preciseBitmapView = (PreciseBitmapView)findViewById(R.id.preciseBitmapView);

        // Determining if the activity has just been created or it has been restored
        if (savedInstanceState != null)
        {
        	Log.i("PreciseBitmapViewActivity", "Restoring the view...");
        	filename = savedInstanceState.getString("filename");
        	imageLoadingProgress = savedInstanceState.getInt("imageLoadingProgress_progress");
        }
        else
        {
        	Log.i("PreciseBitmapViewActivity", "Creating the view from scratch...");
	        Intent intent = getIntent();
	        if (intent.getAction().equals(Intent.ACTION_VIEW))
	        {
	        	Log.i("PreciseBitmapViewActivity", "Received ACTION_VIEW intent. Loading the image...");
	        	filename = getIntent().getData().getPath();
	        }
	        else
	        {
	        	Log.i("PreciseBitmapViewActivity", "Intent action isn't ACTION_VIEW ...");
	        	this.finish();
	        	// TODO Incorrect opening error to be shown here	        	
	        	return;
	        }
        }

		// Creating the file object
		final File imageFile = new File(filename); 
        
		// Creating the progress dialog
	    loadingProgressDialog = new ProgressDialog(this);
	    loadingProgressDialog.setMax(100);
		loadingProgressDialog.setMessage("Loading image " + imageFile.getName() + "...");
		loadingProgressDialog.setTitle("Please wait");
		loadingProgressDialog.setCancelable(false);

		// Setting the current progress
		loadingProgressDialog.setProgress(imageLoadingProgress);
		loadingProgressDialog.setMessage("Loading image " + imageFile.getName() + " (" + imageLoadingProgress + "%)...");			
        
		// Adding the image to registry
		try 
		{
			image = application.getRegistry().requestOrLoadImage(filename);
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LoadingState currentState = application.getRegistry().loadImage(image, imageLoaderReporter);
		
		// If the loading pending, show the progress dialog
		if (currentState == LoadingState.NotLoadedYet || currentState == LoadingState.LoadingInProgress)
		{
			loadingProgressDialog.show();
		}
        
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		outState.putString("filename", filename);
		outState.putInt("imageLoadingProgress_progress", imageLoadingProgress);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		app.getRegistry().forgetImage(image);
    	Log.i("PreciseBitmapViewActivity", "The image has been forgotten");
	}
	
	@Override
	protected void onDestroy()
	{
		// Closing the loading progress dialog
		if (loadingProgressDialog.isShowing())
		{
			loadingProgressDialog.dismiss();
		}
		
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		
		app.getRegistry().unregisterImageLoaderReporter(image, imageLoaderReporter);
    	Log.i("PreciseBitmapViewActivity", "Destroying the activity");
		super.onDestroy();
	}
	
}