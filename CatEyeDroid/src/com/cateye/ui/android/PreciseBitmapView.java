package com.cateye.ui.android;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
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
    private float posX, posY;
    private volatile float deltaX, deltaY;
    private volatile float panX, panY;
    private volatile PointF fingerStartPosition = new PointF(0,0);//, currentFingerPosition = new PointF(0,0);
    private volatile Bitmap image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private volatile int[] pixels;
    private volatile int bitmapWidth = 1, bitmapHeight = 1;
    private Thread updatingThread = null;
	private volatile boolean updaterCancelPending = false;
	private ArrayList<PointF> oldFingers = new ArrayList<PointF>();
	
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

					if (pb != null && bitmapWidth > 0 && bitmapHeight > 0)
					{
						float deltaXOld = 0;
						float deltaYOld = 0;
				       	synchronized (this) 
				       	{
				       		deltaXOld = deltaX;
				       		deltaYOld = deltaY;
				       	}
						panX -= deltaXOld;
						panY -= deltaYOld;
						
						Bitmap newImage = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
						pixels = pb.getPixels(pixels, (int)(panX), (int)(panY), bitmapWidth, bitmapHeight, 1000, 0.5f);
						if (newImage != null && !newImage.isRecycled() && pixels != null)
						{
							newImage.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
						}
						
				       	synchronized (this) 
				       	{
					       	image = newImage;
							deltaX -= deltaXOld;
							deltaY -= deltaYOld;
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
		Log.i("PreciseBitmapView", "onDetachedFromWindow");
		ensureUpdaterStopped();
		super.onDetachedFromWindow();
	}
	
	@Override
	protected void onAttachedToWindow()
	{
		Log.i("PreciseBitmapView", "onAttachedToWindow");
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
	
	synchronized void changeFingersCount(ArrayList<PointF> newFingers)
	{
		Log.i("PreciseBitmapView", "There were " + oldFingers.size() + " fingers, but now there are " + newFingers.size());
		if (newFingers.size() > 0)
		{
			float oldX = 0, oldY = 0;
			if (oldFingers.size() > 0)
			{			
				for (int i = 0; i < oldFingers.size(); i++)
				{
					oldX += oldFingers.get(i).x;
					oldY += oldFingers.get(i).y;
				}
				oldX /= oldFingers.size();
				oldY /= oldFingers.size();
			}
			
			float newX = 0, newY = 0;
			for (int i = 0; i < newFingers.size(); i++)
			{
				newX += newFingers.get(i).x;
				newY += newFingers.get(i).y;
			}
			newX /= newFingers.size();
			newY /= newFingers.size();
			
			if (oldFingers.size() > 0)
			{			
				float dX = newX - oldX;
				float dY = newX - oldY;
				fingerStartPosition = new PointF(fingerStartPosition.x + dX,
				                                 fingerStartPosition.y + dY);
			}
			else
			{
				fingerStartPosition = new PointF(newX, newY);
			}
		}
	}
	
	ArrayList<PointF> arrayToFingers(MotionEvent event)
	{
    	ArrayList<PointF> curFingers = new ArrayList<PointF>();
    	for (int i = 0; i < event.getPointerCount(); i++)
    	{
    		PointerCoords pc = new PointerCoords();
    		event.getPointerCoords(i, pc);
    		
    		if ((event.getActionMasked() != MotionEvent.ACTION_UP &&
   		        event.getActionMasked() != MotionEvent.ACTION_POINTER_UP) ||
   		        event.getActionIndex() != i)		// to remove fingers which are not here already
    		{
    			curFingers.add(new PointF(pc.x, pc.y));
    		}
    	}
    	return curFingers;
	}
	
	PointF getCenter(ArrayList<PointF> points)
	{
    	float cX = 0, cY = 0;
    	for (int i = 0; i < points.size(); i++)
    	{
    		cX += points.get(i).x; 
    		cY += points.get(i).y;
    	}
    	cX /= points.size();
    	cY /= points.size();
    	return new PointF(cX, cY);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN ||
		    event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
		{
			ArrayList<PointF> f = arrayToFingers(event);
			PointF cen = getCenter(f);
			posX = cen.x; posY = cen.y;
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_UP ||
		         event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
		{
			ArrayList<PointF> f = arrayToFingers(event);
			if (f.size() > 0)
			{
				PointF cen = getCenter(f);
				posX = cen.x; posY = cen.y;
			}
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
		{
			ArrayList<PointF> f = arrayToFingers(event);
			PointF cen = getCenter(f);

			synchronized (this)
			{
				deltaX += cen.x - posX;
				deltaY += cen.y - posY;
			}
			
			posX = cen.x; posY = cen.y;
			
			invalidate();
			return true;
		}
		
		return super.onTouchEvent(event);
	}

}