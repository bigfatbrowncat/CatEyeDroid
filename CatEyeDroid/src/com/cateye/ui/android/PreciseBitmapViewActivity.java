package com.cateye.ui.android;

import com.cateye.core.Image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PreciseBitmapViewActivity extends Activity 
{
    private Image rawImage;
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
    		rawView.setPreciseBitmap(rawImage.getBitmap());
    	}
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		outState.putInt("imageId", app.getIdOfImage(rawImage));
		super.onSaveInstanceState(outState);
	}

/*	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		onCreate(savedInstanceState);
	}*/
	
	@Override
	public void onBackPressed() {
		CatEyeApplication app = ((CatEyeApplication) getApplication());
		app.forgetImage(rawImage);
		super.onBackPressed();
	}
}