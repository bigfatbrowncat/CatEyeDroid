package com.cateye.ui.android;

import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PreciseBitmapViewActivity extends Activity 
{
    private RawImage rawImage;
	private PreciseBitmapView rawView;

/** Called when the activity is first created. */
@Override
    public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_viewer_activity);
        rawView = (PreciseBitmapView)findViewById(R.id.surfaceView1);
        
        CatEyeApplication app = ((CatEyeApplication) getApplication());
        if (savedInstanceState != null)
        {
        	Log.i("PreciseBitmapViewActivity", "Restoring the view...");
        	rawImage = app.getImageById(savedInstanceState.getInt("imageId"));
        }
        else
        {
        	Log.i("PreciseBitmapViewActivity", "Creating the view from scratch...");
	        Intent intent = getIntent();
	        if (intent.getAction().equals(Intent.ACTION_VIEW))
	        {
	        	Log.i("PreciseBitmapViewActivity", "Received ACTION_VIEW intent. Loading the image...");
	        	String path = getIntent().getData().getPath();
	        	rawImage = app.loadImage(path);
	        }
	        else
	        {
	        	Log.i("PreciseBitmapViewActivity", "Intent action isn't ACTION_VIEW ...");
	        }
        }
    	if (rawImage != null) 
    	{
    		try {
				rawView.setPreciseBitmap(rawImage.getBitmap());
			} catch (ImageLoaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		outState.putInt("imageId", app.getIdOfImage(rawImage));
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
	protected void onDestroy() {
    	Log.i("PreciseBitmapViewActivity", "Destroying the activity");
		super.onDestroy();
	}
	
}