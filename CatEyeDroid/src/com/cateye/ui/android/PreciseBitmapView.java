package com.cateye.ui.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.jni.PreciseBitmap;

public class PreciseBitmapView extends View
{
	public PreciseBitmapView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public PreciseBitmapView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PreciseBitmapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private IPreciseBitmap pb;
    private volatile float deltaX, deltaY;
    private volatile float panX, panY;
    private volatile PointF fingerStartPosition = new PointF(0,0), currentFingerPosition = new PointF(0,0);
    private volatile Bitmap image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private volatile int[] pixels;
    private volatile int bitmapWidth = 1, bitmapHeight = 1;
    private Thread updatingThread = null;
	private volatile boolean updaterCancelPending = false;
	
	private void ensureUpdaterStopped()
	{
		if (updatingThread != null && updatingThread.isAlive())
		{
			try
			{
        		Log.i("PreciseBitmapView", "Joining updater...");
        		updaterCancelPending = true;
				updatingThread.join();
	    		Log.i("PreciseBitmapView", "Joined.");				
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				updatingThread = null;
				updaterCancelPending = false;
			}
		}		
	}
	
	private void startUpdater()
	{
		ensureUpdaterStopped();
		
		updatingThread = new Thread(new Runnable() {
			
			public void run() 
			{
				while (!updaterCancelPending)
				{
					bitmapWidth = getWidth();
					bitmapHeight = getHeight();

					Log.i("PreciseBitmapView", "pb is null? " + (pb == null));
					if (pb != null && bitmapWidth > 0 && bitmapHeight > 0)
					{
						float deltaXOld = deltaX;
						float deltaYOld = deltaY;
						panX -= deltaXOld;
						panY -= deltaYOld;
						
						Bitmap newImage = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
						pixels = pb.getPixels(pixels, (int)(panX), (int)(panY), bitmapWidth, bitmapHeight, 1000, 0.5f);
						if (newImage != null && !newImage.isRecycled())
						{
							newImage.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
						}
						
				       	synchronized (this) 
				       	{
					       	image = newImage;
							deltaX -= deltaXOld;
							deltaY -= deltaYOld;
							fingerStartPosition = new PointF(currentFingerPosition.x - deltaX,
							                                 currentFingerPosition.y - deltaY);
						}
				       	PreciseBitmapView.this.postInvalidate();
					}
					else
					{
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				Log.i("PreciseBitmapView", "Updating thread stopped");
			}
		});
		
		Log.i("PreciseBitmapView", "Starting updater...");
		updatingThread.start();
	}
	
	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		pb = value; 
	}
	
	@Override
	protected void onDetachedFromWindow() {
		ensureUpdaterStopped();
		super.onDetachedFromWindow();
	}
	
	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		startUpdater();
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		/*
		Paint pnt = new Paint();
		pnt.setStyle(Paint.Style.FILL_AND_STROKE);
		pnt.setARGB(128, 128, 128, 192);
		canvas.drawLine(0, 0, getWidth(), getHeight(), pnt);
		canvas.drawBitmap(image, 0, 0, null);*/
		
        synchronized (this)
        {
        	canvas.drawBitmap(image, deltaX, deltaY, null);
        }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) 
		{
			updaterCancelPending = true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN ||
		    event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
		{
	        synchronized (this)
	        {
	        	fingerStartPosition = new PointF(event.getX(0), event.getY(0));
	        	currentFingerPosition = fingerStartPosition;
	        }
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
		{
	        synchronized (this)
	        {
	        	int k = 0;
	        	if (k == event.getActionIndex()) k++;
	        	fingerStartPosition = new PointF(event.getX(k), event.getY(k));
	        	currentFingerPosition = fingerStartPosition;
	        }
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
		{
	        synchronized (this)
	        {
				currentFingerPosition = new PointF(event.getX(), event.getY());
				deltaX = currentFingerPosition.x - fingerStartPosition.x;
				deltaY = currentFingerPosition.y - fingerStartPosition.y;
	        }
			
			invalidate();
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_UP)
		{
			invalidate();
			return true;
		}

		
		return super.onTouchEvent(event);
	}

}