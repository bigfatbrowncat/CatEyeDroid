package com.cateye.ui.android;

import android.app.Activity;
import android.os.Bundle;

public class PreciseBitmapViewActivity extends Activity {
    
private PreciseBitmapView rawView;

/** Called when the activity is first created. */
@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_viewer_activity);
        rawView = (PreciseBitmapView)findViewById(R.id.surfaceView1);
    }
}