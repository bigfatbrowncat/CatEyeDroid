package com.cateye.ui.android;

import java.util.ArrayList;
import java.util.Currency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.PointD;
import com.cateye.core.PreciseBitmapGetPixelsCallback;
import com.cateye.core.RestrictedImageCoordinatesTransformer;

public class PreciseBitmapView extends View
{
	private IPreciseBitmap preciseBitmap;
	private PointD[] centers = new PointD[] { new PointD(0, 0), new PointD(0, 0) };
	private double[] dispersions = new double[] { 1, 1 };	// To avoid NaNs
	private RestrictedImageCoordinatesTransformer[] imageTransformers = new RestrictedImageCoordinatesTransformer[2];
    private PointF fingerStartPosition = new PointF(0, 0);
    private ArrayList<PointF> currentFingers = new ArrayList<PointF>();

    private volatile int activeImageIndex = 0;
    private Bitmap[] image = new Bitmap[2];
    private int[][] pixels = new int[2][];
    private int[] downscales = new int[] { 4, 1 };
	
    private ArrayList<PointF> oldFingers = new ArrayList<PointF>();

	private volatile int viewWidth, viewHeight;
	
	//pixels = preciseBitmap.getPixelsBGRIntoIntBuffer(pixels, (int)panX, (int)panY, bitmapWidth, bitmapHeight, 1000, 0.5f);
	
	private Thread polishingDrawingThread = null;
	
	//private final PreciseBitmapGetPixelsCallback preciseBitmapGetPixelsCallback = 
	
	/**
	 * Drawing the image with the specified quality.
	 * @param k This is the quality factor. <code>k = 0</code> means low quality, 
	 * <code>k = 1</code> means high quality
	 */
	private boolean drawOrPolish(int k)
	{
		viewWidth = getWidth();
		viewHeight = getHeight();
		
		if (image[k] == null)
		{
			pixels[k] = new int[(viewWidth / downscales[k]) * (viewHeight / downscales[k])];
			image[k] = Bitmap.createBitmap(
					viewWidth / downscales[k], 
					viewHeight / downscales[k], 
					Bitmap.Config.ARGB_8888);
		}

		PointD lt = imageTransformers[k].screenToImage(new PointD(0, 0));
		Log.i("PreciseBitmapView", "lt = " + lt.toString());
		
		boolean res = preciseBitmap.getPixelsBGRIntoIntBuffer(pixels[k], 
				(int)lt.getX(), (int)lt.getY(),
				viewWidth / downscales[k], viewHeight / downscales[k], 
				1000, 
				(float)(imageTransformers[k].getZoom()), 
				new PreciseBitmapGetPixelsCallback() {
					@Override
					public boolean report() 
					{
						return !Thread.interrupted();
					}
				}
			);
		if (!res) return false;
		
        image[k].setPixels(pixels[k], 0, viewWidth / downscales[k], 0, 0, viewWidth / downscales[k], viewHeight / downscales[k]);
        return true;
	}
	
	private final Runnable polishingDrawingRunnable = new Runnable()
	{
		@Override
		public void run() 
		{
			if (drawOrPolish(1))
			{
				Log.i("PreciseBitmapView", "polished");
	        	activeImageIndex = 1;
		        PreciseBitmapView.this.postInvalidate();
	        }
		}
	};
	
	void changeFingersCount(ArrayList<PointF> newFingers)
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
	
	PointD getCenter(ArrayList<PointF> points)
	{
    	float cX = 0, cY = 0;
    	for (int i = 0; i < points.size(); i++)
    	{
    		cX += points.get(i).x; 
    		cY += points.get(i).y;
    	}
    	cX /= points.size();
    	cY /= points.size();
    	return new PointD(cX, cY);
	}
	
	float getDispersion(ArrayList<PointF> points)
	{
		PointD center = getCenter(points);
		
    	float d = 0;
    	for (int i = 0; i < points.size(); i++)
    	{
    		double dx = points.get(i).x - center.getX(); 
    		double dy = points.get(i).y - center.getY();
    		d += Math.sqrt(dx * dx + dy * dy);	// distance
    	}
    	d /= points.size();		// average distance
    	
		return d;
	}
	
	private void createImageTransformers() 
	{
		for (int i = 0; i < 2; i++)
		{
			imageTransformers[i] = new RestrictedImageCoordinatesTransformer();
			imageTransformers[i].setZoom(1.0 / downscales[i]);
		}
	}
	
	public PreciseBitmapView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		createImageTransformers();
	}

	public PreciseBitmapView(Context context)
	{
		super(context);
		createImageTransformers();
	}

	public PreciseBitmapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		createImageTransformers();
	}


	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
		Log.i("PreciseBitmapView", "Precise bitmap changed. Updating");
		
		for (int i = 0; i < 2; i++)
		{
			PointD imageSize = new PointD(preciseBitmap.getWidth(), preciseBitmap.getHeight());
			Log.i("PreciseBitmapView", "imageSize = " + imageSize.toString());
			imageTransformers[i].setImageSize(imageSize);
		}
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("PreciseBitmapView", "Size changed. Updating");
		
		for (int i = 0; i < 2; i++)
		{
			PointD screenSize = new PointD(this.getWidth() / downscales[i], this.getHeight() / downscales[i]);
			imageTransformers[i].setScreenSize(screenSize);
			Log.i("PreciseBitmapView", "screenSize = " + screenSize.toString());
		}
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		synchronized (this)
		{
			if (image[activeImageIndex] != null)
			{
				Log.i("PreciseBitmapView", "drawing downscale = " + downscales[activeImageIndex]);
				Rect src = new Rect(0, 0, viewWidth / downscales[activeImageIndex], viewHeight / downscales[activeImageIndex]);
				Rect dst = new Rect(0, 0, viewWidth, viewHeight);
				canvas.drawBitmap(image[activeImageIndex], src, dst, null);
			}
		}
		if (activeImageIndex == 0) polish();
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN ||
		    event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
		{
			currentFingers = extractFingers(event);
			for (int i = 0; i < 2; i++)
			{
				centers[i] = PointD.multiply(getCenter(currentFingers), 1.0 / downscales[i]);
				dispersions[i] = getDispersion(currentFingers) / downscales[i];
			}
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_UP ||
		         event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
		{
			currentFingers = extractFingers(event);
			if (currentFingers.size() > 0)
			{
				for (int i = 0; i < 2; i++)
				{
					centers[i] = PointD.multiply(getCenter(currentFingers), 1.0 / downscales[i]);
					dispersions[i] = getDispersion(currentFingers) / downscales[i];
				}
			}
			//invalidate();
			return true;
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
		{
			currentFingers = extractFingers(event);
			
			for (int i = 0; i < 2; i++)
			{
				PointD newCenter = PointD.multiply(getCenter(currentFingers), 1.0 / downscales[i]);
				float newDispersion = getDispersion(currentFingers) / downscales[i];

				imageTransformers[i].addPan(new PointD(newCenter.getX() - centers[i].getX(), newCenter.getY() - centers[i].getY()));
				
				if (currentFingers.size() > 1)
				{
					// More than one finger -- zooming
					imageTransformers[i].zoomUponScreenPoint(newCenter, newDispersion / dispersions[i]);
				}
	
				Log.i("PreciseBitmapView", "pan = " + imageTransformers[i].getPan().toString());
				Log.i("PreciseBitmapView", "zoom = " + imageTransformers[i].getZoom());

				centers[i] = newCenter;
				dispersions[i] = newDispersion;
			}
			
			drawOrPolish(0);
			activeImageIndex = 0;
			invalidate();
			
			return true;
		}
		
		return super.onTouchEvent(event);
	}
	
	private void polish()
	{
		if (polishingDrawingThread != null && polishingDrawingThread.isAlive())
		{
			polishingDrawingThread.interrupt();
			try {
				polishingDrawingThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		polishingDrawingThread = new Thread(this.polishingDrawingRunnable);
		polishingDrawingThread.start();
	}

}