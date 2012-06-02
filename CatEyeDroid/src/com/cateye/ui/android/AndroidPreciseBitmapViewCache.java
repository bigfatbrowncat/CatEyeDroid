package com.cateye.ui.android;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.cateye.core.ImageCoordinatesTransformer;
import com.cateye.core.PointD;
import com.cateye.core.PreciseBitmapGetPixelsCallback;
import com.cateye.core.PreciseBitmapViewCache;
import android.graphics.Canvas;

public class AndroidPreciseBitmapViewCache extends PreciseBitmapViewCache
{
	private Bitmap image;
    private int[] pixels;

    public AndroidPreciseBitmapViewCache(int downscale, ImageCoordinatesTransformer imageTransformer)
    {
		super(downscale, imageTransformer);
	}

    public boolean update()
    {
    	if (image == null || pixels == null || getPreciseBitmap() == null) return false;
    	
		PointD lt = getImageTransformer().screenToImage(new PointD(0, 0));
		
		if (!getPreciseBitmap().getPixelsBGRIntoIntBuffer(pixels, 
				(int)lt.getX(), (int)lt.getY(),
				getViewWidth() / getDownscale(), getViewHeight() / getDownscale(), 
				1000, 
				(float)(getImageTransformer().getZoom() / getDownscale()), 
				new PreciseBitmapGetPixelsCallback() 
				{
					@Override
					public boolean report() 
					{
						return !Thread.interrupted();
					}
				}
			)) return false;
		
        image.setPixels(pixels, 0, getViewWidth() / getDownscale(), 0, 0, getViewWidth() / getDownscale(), getViewHeight() / getDownscale());

		return true;
    }
    
    public void draw(Canvas canvas)
    {
		if (image != null)
		{
			Rect src = new Rect(0, 0, getViewWidth() / getDownscale(), getViewHeight() / getDownscale());
			Rect dst = new Rect(0, 0, getViewWidth(), getViewHeight());
			canvas.drawBitmap(image, src, dst, null);
		}
    }
    
	public Bitmap getImage()
	{
		return image;
	}
	
	@Override
	public void setViewSize(PointD viewSize) 
	{
		super.setViewSize(viewSize);

		pixels = new int[((int)viewSize.getX() / getDownscale()) * ((int)viewSize.getY() / getDownscale())];
		image = Bitmap.createBitmap(
				(int)viewSize.getX() / getDownscale(), 
				(int)viewSize.getY() / getDownscale(), 
				Bitmap.Config.ARGB_8888
		);		
	}
}
