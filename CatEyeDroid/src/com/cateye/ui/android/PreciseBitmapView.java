package com.cateye.ui.android;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
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

	private IPreciseBitmap preciseBitmap;
    
	// Panning variables
	private PointF center = new PointF(0, 0);
    private volatile float panDeltaX, panDeltaY;
    private volatile float panX, panY;
    
    // Zooming variables
    private float dispersion;
    private volatile float zoomDelta = 1;
    private volatile float zoom = 1;
    
    private volatile PointF fingerStartPosition = new PointF(0, 0);//, currentFingerPosition = new PointF(0,0);
    private volatile Bitmap image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private volatile int[] pixels;
    private volatile int bitmapWidth = 1, bitmapHeight = 1;
    private volatile boolean updatePending = false;
    private Thread updatingThread = null;
	private ArrayList<PointF> oldFingers = new ArrayList<PointF>();
	
	private void updateAsync()
	{
		updatePending = true;
		
		if (updatingThread == null || !updatingThread.isAlive())
		{
			updatingThread = new Thread(new Runnable() {
				
				public void run() 
				{
					while (updatePending)
					{
						updatePending = false;
						bitmapWidth = getWidth();
						bitmapHeight = getHeight();
	
						if (preciseBitmap != null && bitmapWidth > 0 && bitmapHeight > 0)
						{
							Log.i("PreciseBitmapView", "Updating image");
							float deltaXOld = 0;
							float deltaYOld = 0;
					       	synchronized (PreciseBitmapView.this) 
					       	{
					       		deltaXOld = panDeltaX;
					       		deltaYOld = panDeltaY;
					       	}
							panX -= deltaXOld;
							panY -= deltaYOld;
							
							Bitmap newImage = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
							pixels = preciseBitmap.getPixels(pixels, false, (int)(panX), (int)(panY), bitmapWidth, bitmapHeight, 1000, 0.5f);
							if (newImage != null && !newImage.isRecycled() && pixels != null)
							{
								newImage.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
							}
							
					       	synchronized (PreciseBitmapView.this) 
					       	{
						       	image = newImage;
								panDeltaX -= deltaXOld;
								panDeltaY -= deltaYOld;
							}
					       	
							Log.i("PreciseBitmapView", "Posting Invalidation message");
					       	PreciseBitmapView.this.postInvalidate();
						}
						else
						{
							if (preciseBitmap == null)
								Log.i("PreciseBitmapView", "Precise bitmap is null");
							else if (bitmapWidth == 0 || bitmapHeight == 0)
								Log.i("PreciseBitmapView", "Width or height is 0");
								
						}
						
					}
					Log.i("PreciseBitmapView", "Updating thread stopped");
				}
			});
			
			Log.i("PreciseBitmapView", "Starting updater...");
			updatingThread.start();
		}
	}

	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
		Log.i("PreciseBitmapView", "Precise bitmap changed. Updating");
		updateAsync();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("PreciseBitmapView", "Size changed. Updating");
		updateAsync();
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
        	//canvas.drawBitmap(image, panDeltaX, panDeltaY, null);
        	Rect src = new Rect(
        			(int)(panDeltaX + center.x), 
        			(int)(panDeltaY + center.y), 
        			(int)(panDeltaX + center.x + bitmapWidth / zoomDelta), 
        			(int)(panDeltaY + center.y + bitmapHeight / zoomDelta)
        	);
        	
        	Rect dst = new Rect(0, 0, bitmapWidth, bitmapHeight);
        	canvas.drawBitmap(image, src, dst, null);
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
	
	ArrayList<PointF> extractFingers(MotionEvent event)
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
	
	float getDispersion(ArrayList<PointF> points)
	{
		PointF center = getCenter(points);
		
    	float d = 0;
    	for (int i = 0; i < points.size(); i++)
    	{
    		double dx = points.get(i).x - center.x; 
    		double dy = points.get(i).y - center.y;
    		d += Math.sqrt(dx * dx + dy * dy);	// distance
    	}
    	d /= points.size();		// average distance
    	
		return d;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN ||
		    event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
		{
			ArrayList<PointF> f = extractFingers(event);
			center = getCenter(f);
			dispersion = getDispersion(f);
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_UP ||
		         event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
		{
			ArrayList<PointF> f = extractFingers(event);
			if (f.size() > 0)
			{
				center = getCenter(f);
				dispersion = getDispersion(f);
			}
			updateAsync();
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
		{
			ArrayList<PointF> f = extractFingers(event);
			PointF newCenter = getCenter(f);
			float newDispersion = getDispersion(f);

			synchronized (this)
			{
				panDeltaX += newCenter.x - center.x;
				panDeltaY += newCenter.y - center.y;
				zoomDelta *= 1.0 + 0.006 * (newDispersion - dispersion);
			}
			
			center = newCenter;
			dispersion = newDispersion;
			
			invalidate();
			updateAsync();
			return true;
		}
		
		return super.onTouchEvent(event);
	}

}