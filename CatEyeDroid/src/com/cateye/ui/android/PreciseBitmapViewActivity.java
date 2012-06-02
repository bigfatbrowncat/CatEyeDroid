package com.cateye.ui.android;

import java.io.File;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class PreciseBitmapViewActivity extends Activity 
{
    private RawImage rawImage;
    private File imageFile;
	private PreciseBitmapView preciseBitmapView;
	private ProgressDialog loadingProgressDialog = null;
	private IProgressListener imageLoadingProgressListener = null; 

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
		
	    // Removing title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    // Loading UI
        setContentView(R.layout.raw_viewer_activity);
        preciseBitmapView = (PreciseBitmapView)findViewById(R.id.preciseBitmapView);

        final CatEyeApplication application = ((CatEyeApplication) getApplication());
        if (savedInstanceState != null)
        {
        	Log.i("PreciseBitmapViewActivity", "Restoring the view...");
        	imageFile = new File(savedInstanceState.getString("filename"));
        	rawImage = application.getImageById(savedInstanceState.getInt("imageId"));
        }
        else
        {
        	Log.i("PreciseBitmapViewActivity", "Creating the view from scratch...");
	        Intent intent = getIntent();
	        if (intent.getAction().equals(Intent.ACTION_VIEW))
	        {
	        	Log.i("PreciseBitmapViewActivity", "Received ACTION_VIEW intent. Loading the image...");
	        	String filename = getIntent().getData().getPath();
	        	imageFile = new File(filename);
	        	rawImage = application.loadImage(filename);
	        	
	        	
	        }
	        else
	        {
	        	Log.i("PreciseBitmapViewActivity", "Intent action isn't ACTION_VIEW ...");
	        }
        }

    	if (rawImage != null) 
    	{
    		Log.i("PreciseBitmapViewActivity", "Showing Loading Popup window ...");
    		
    	    final ProgressDialog loadingProgressDialog = new ProgressDialog(this);
    	    loadingProgressDialog.setMax(100);
    		loadingProgressDialog.setMessage("Image " + imageFile.getName() + " is being loaded (0%)...");
    		loadingProgressDialog.setTitle("Please wait");
    		loadingProgressDialog.setCancelable(false);
    		this.loadingProgressDialog = loadingProgressDialog;
    		loadingProgressDialog.show();

    		final IProgressListener imageLoadingProgressListener = new IProgressListener()
    		{
    			@Override
    			public boolean invoke(Object sender, final float progress)
    			{
    				PreciseBitmapViewActivity.this.runOnUiThread(new Runnable()
    				{
    					@Override
    					public void run()
    					{
    						int p = (int)(progress * 100);
    						loadingProgressDialog.setProgress(p);
    			    		loadingProgressDialog.setMessage("Image " + imageFile.getName() + " is being loaded (" + p + "%)...");
    					}
    				});
    				
    				return true;
    			}
    		};
    		
    		application.getImageLoader().addProgressListener(imageLoadingProgressListener);
    		
    		
    		Runnable imageLoadingRunnable = new Runnable()
			{
				@Override
				public void run() 
				{
					IPreciseBitmap pb = null;
		    		try 
		    		{
						pb = rawImage.getBitmap();
					} 
		    		catch (ImageLoaderException e) 
		    		{
						e.printStackTrace();
						return;
					}
		    		
					loadingProgressDialog.dismiss();
					

					final IPreciseBitmap preciseBitmap = pb;
					PreciseBitmapViewActivity.this.runOnUiThread(new Runnable() 
					{
						
						@Override
						public void run() 
						{
							preciseBitmapView.setPreciseBitmap(preciseBitmap);
				    		Log.i("PreciseBitmapViewActivity", "Hiding Loading Popup window ...");
						}
						
					});
		    		
				}	
			};
			
			new Thread(imageLoadingRunnable).start();
    	}
        
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		outState.putInt("imageId", app.getIdOfImage(rawImage));
		outState.putString("filename", imageFile.getPath());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
    	if (rawImage != null)
    	{
    		CatEyeApplication app = ((CatEyeApplication) getApplication());
			app.forgetImage(rawImage);
	    	Log.i("PreciseBitmapViewActivity", "The image has been forgotten");
    	}
	}
	
	@Override
	protected void onDestroy()
	{
		final CatEyeApplication application = ((CatEyeApplication) getApplication());
		if (loadingProgressDialog != null) 
		{
			loadingProgressDialog.dismiss();
		}
		if (imageLoadingProgressListener != null)
		{
			application.getImageLoader().removeProgressListener(imageLoadingProgressListener);
		}
		
    	Log.i("PreciseBitmapViewActivity", "Destroying the activity");
		super.onDestroy();
	}
	
}