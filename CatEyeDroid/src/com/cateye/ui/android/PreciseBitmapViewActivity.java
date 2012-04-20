package com.cateye.ui.android;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PreciseBitmapViewActivity extends Activity {
    
private PreciseBitmapView rawView;

/** Called when the activity is first created. */
@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_viewer_activity);
        rawView = (PreciseBitmapView)findViewById(R.id.surfaceView1);
        
        CatEyeApplication app = ((CatEyeApplication) getApplication());
        rawView.setPreciseBitmap(app.getPreciseBitmap());
        
        //rawView.setOnTouchListener((View.OnTouchListener)this);
    }
}