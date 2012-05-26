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
import com.cateye.core.RestrictedImageCoordinatesTransformer;

public class PreciseBitmapView extends View
{
	private IPreciseBitmap preciseBitmap;
	private PointD center = new PointD(0, 0);
	private double dispersion = 1;	// To avoid NaNs
	private RestrictedImageCoordinatesTransformer imageTransformer = new RestrictedImageCoordinatesTransformer();
    private PointF fingerStartPosition = new PointF(0, 0);
    private ArrayList<PointF> currentFingers = new ArrayList<PointF>();
    private Bitmap image;// = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private int[] pixels;
	private ArrayList<PointF> oldFingers = new ArrayList<PointF>();

	//pixels = preciseBitmap.getPixelsBGRIntoIntBuffer(pixels, (int)panX, (int)panY, bitmapWidth, bitmapHeight, 1000, 0.5f);
	
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


	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
		Log.i("PreciseBitmapView", "Precise bitmap changed. Updating");
		PointD imageSize = new PointD(preciseBitmap.getWidth(), preciseBitmap.getHeight());
		
		imageTransformer.setImageSize(imageSize);
		Log.i("PreciseBitmapView", "imageSize = " + imageSize.toString());
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("PreciseBitmapView", "Size changed. Updating");
		
		PointD screenSize = new PointD(this.getWidth(), this.getHeight());
		imageTransformer.setScreenSize(screenSize);
		Log.i("PreciseBitmapView", "screenSize = " + screenSize.toString());
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		
		if (image == null)
		{
			pixels = new int[viewWidth * viewHeight];
			image = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
		}

		PointD lt = imageTransformer.screenToImage(new PointD(0, 0));
		Log.i("PreciseBitmapView", "lt = " + lt.toString());
		
		pixels = preciseBitmap.getPixelsBGRIntoIntBuffer(pixels, 
				(int)lt.getX(), (int)lt.getY(),
				viewWidth, viewHeight, 
				1000, 
				(float)(imageTransformer.getZoom()));
		
        Rect src = new Rect(0, 0, viewWidth, viewHeight);
        Rect dst = new Rect(0, 0, viewWidth, viewHeight);
        image.setPixels(pixels, 0, viewWidth, 0, 0, viewWidth, viewHeight);
		canvas.drawBitmap(image, src, dst, null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN ||
		    event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
		{
			currentFingers = extractFingers(event);
			center = getCenter(currentFingers);
			dispersion = getDispersion(currentFingers);
			return true;
		}
		/*else if (event.getActionMasked() == MotionEvent.ACTION_UP ||
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
		}*/
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
		{
			currentFingers = extractFingers(event);
			PointD newCenter = getCenter(currentFingers);
			float newDispersion = getDispersion(currentFingers);

			imageTransformer.addPan(new PointD(newCenter.getX() - center.getX(), newCenter.getY() - center.getY()));
			
			if (currentFingers.size() > 1)
			{
				// More than one finger -- zooming
				imageTransformer.zoomUponScreenPoint(newCenter, newDispersion / dispersion);
			}

			Log.i("PreciseBitmapView", "pan = " + imageTransformer.getPan().toString());
			Log.i("PreciseBitmapView", "zoom = " + imageTransformer.getZoom());

			center = newCenter;
			dispersion = newDispersion;
			
			invalidate();
			
			return true;
		}
		
		return super.onTouchEvent(event);
	}

}