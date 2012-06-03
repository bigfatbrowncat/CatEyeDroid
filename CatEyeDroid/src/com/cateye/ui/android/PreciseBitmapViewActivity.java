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
import com.cateye.ui.android.CatEyeApplication.LoadingState;

public class PreciseBitmapViewActivity extends Activity 
{
    private String filename;
	private PreciseBitmapView preciseBitmapView;
	private ProgressDialog loadingProgressDialog = null;
//	private IProgressListener imageLoadingProgressListener = null; 

	ImageLoaderReporter imageLoaderReporter = new ImageLoaderReporter() 
	{
		@Override
		public void reportSuccess(IPreciseBitmap preciseBitmap)
		{
			loadingProgressDialog.dismiss();
			PreciseBitmapViewActivity.this.preciseBitmapView.setPreciseBitmap(preciseBitmap);
		}
		
		@Override
		public void reportException(ImageLoaderException e) 
		{
			loadingProgressDialog.dismiss();
			// TODO Handle exception correctly!
		}

		@Override
		public void reportProgress(int progress)
		{
			if (!loadingProgressDialog.isShowing())
			{
				loadingProgressDialog.show();
			}

			final File imageFile = new File(filename); 
			loadingProgressDialog.setProgress(progress);
    		loadingProgressDialog.setMessage("Loading image " + imageFile.getName() + " (" + progress + "%)...");			
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
        
		// Start the image loading process or just connect to it
    	try 
    	{
			LoadingState currentState = application.loadImageForActivity(this, filename, imageLoaderReporter);
			
			// If the loading pending, show the progress dialog
			if (currentState == LoadingState.NotLoadedYet || currentState == LoadingState.LoadingInProgress)
			{
				loadingProgressDialog.show();
			}
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		outState.putString("filename", filename);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		try 
		{
			app.forgetImage(filename);
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i("PreciseBitmapViewActivity", "The image has been forgotten");
	}
	
	@Override
	protected void onDestroy()
	{
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		
		try 
		{
			app.unregisterImageLoaderReporter(app.requestImageFromRegistry(filename), imageLoaderReporter);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i("PreciseBitmapViewActivity", "Destroying the activity");
		super.onDestroy();
	}
	
}