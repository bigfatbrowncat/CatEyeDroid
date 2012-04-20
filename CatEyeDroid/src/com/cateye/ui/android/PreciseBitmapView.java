package com.cateye.ui.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
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
    
    private Bitmap image = null;
    private int[] pixels;
    private int bitmapWidth = 0, bitmapHeight = 0;
    private int eventNumber = 0;

	@Override
	protected void onDraw(Canvas canvas) 
	{
		/*
		Paint pnt = new Paint();
		pnt.setStyle(Paint.Style.FILL_AND_STROKE);
		pnt.setARGB(128, 128, 128, 192);
		canvas.drawLine(0, 0, getWidth(), getHeight(), pnt);
		canvas.drawBitmap(image, 0, 0, null);*/
		
		if (bitmapWidth != getWidth())
		{
        	bitmapWidth = getWidth();
        	image = null;
        	pixels = null;
		}
		if (bitmapHeight != getHeight())
		{
			bitmapHeight = getHeight();
			image = null;
        	pixels = null;
		}
		
		if (image == null)
		{
			updateBitmap();
		}
		canvas.drawBitmap(image, deltaX, deltaY, null);
	}

	protected void updateBitmap()
	{
		image = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        pixels = pb.getPixels(pixels, (int)(panX), (int)(panY), bitmapWidth, bitmapHeight, 500);
        image.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);		
	}
	
	protected void applyDelta()
	{
		panX -= deltaX;
		panY -= deltaY;
		
		deltaX = 0;
		deltaY = 0;
		
		image = null;
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
			PointF currentFingerPosition = new PointF(event.getX(), event.getY());
			deltaX = currentFingerPosition.x - fingerStartPosition.x;
			deltaY = currentFingerPosition.y - fingerStartPosition.y;
			eventNumber ++;
			if (eventNumber % 20 == 0)
			{
				applyDelta();
				fingerStartPosition = new PointF(event.getX(), event.getY());
			}
			
			invalidate();
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_UP)
		{
			applyDelta();
			invalidate();
			return true;
		}
		return super.onTouchEvent(event);
	}

}