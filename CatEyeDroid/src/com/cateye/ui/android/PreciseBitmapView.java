package com.cateye.ui.android;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.cateye.core.Image;
import com.cateye.core.jni.PreciseBitmap;
import com.cateye.core.jni.RawImageLoader;

public class PreciseBitmapView extends View
{
	PreciseBitmap pb;

	public void setPreciseBitmap(PreciseBitmap value) { pb = value; }
	
    public PreciseBitmapView(Context context)
    {
    	super(context);
    }

    public PreciseBitmapView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
    }
    
    private Bitmap image;

	@Override
	protected void onDraw(Canvas canvas) 
	{
		/*
		Paint pnt = new Paint();
		pnt.setStyle(Paint.Style.FILL_AND_STROKE);
		pnt.setARGB(128, 128, 128, 192);
		canvas.drawLine(0, 0, getWidth(), getHeight(), pnt);
		canvas.drawBitmap(image, 0, 0, null);*/

        int bitmapWidth = getWidth(), bitmapHeight = getHeight();
		image = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        int[] pixels = pb.getPixels(0, 0, bitmapWidth, bitmapHeight, 500);
        image.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);		
		canvas.drawBitmap(image, 0, 0, null);
	}

}