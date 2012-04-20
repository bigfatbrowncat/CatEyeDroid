package com.cateye.ui.android;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cateye.core.jni.PreciseBitmap;

public class PreciseBitmapView extends View
{
	PreciseBitmap pb;
	PointF fingerStartPosition;
	float deltaX, deltaY;
	float panX, panY;

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
        int[] pixels = pb.getPixels((int)(panX - deltaX), (int)(panY - deltaY), bitmapWidth, bitmapHeight, 500);
        image.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);		
		canvas.drawBitmap(image, 0, 0, null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
		{
		//	Log.i("PreciseBitmapView", "ACTION_DOWN " + event.getX() + ", " + event.getY());
			fingerStartPosition = new PointF(event.getX(), event.getY());
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
		{
		//	Log.i("PreciseBitmapView", "ACTION_MOVE " + event.getX() + ", " + event.getY());
			PointF currentFingerPosition = new PointF(event.getX(), event.getY());
			deltaX = currentFingerPosition.x - fingerStartPosition.x;
			deltaY = currentFingerPosition.y - fingerStartPosition.y;
			invalidate();
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_UP)
		{
		//	Log.i("PreciseBitmapView", "ACTION_UP");
			panX -= deltaX;
			panY -= deltaY;
			
			deltaX = 0;
			deltaY = 0;
			invalidate();
			return true;
		}
		return super.onTouchEvent(event);
	}

}