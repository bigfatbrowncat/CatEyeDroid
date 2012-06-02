package com.cateye.ui.swt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.cateye.core.ImageCoordinatesTransformer;
import com.cateye.core.PointD;
import com.cateye.core.PreciseBitmapGetPixelsCallback;
import com.cateye.core.PreciseBitmapViewCache;

public class SwtPreciseBitmapViewCache extends PreciseBitmapViewCache
{
    private ImageData imageData;
    private boolean antialias;
	
	public SwtPreciseBitmapViewCache(int downscale,	boolean antialias, ImageCoordinatesTransformer imageTransformer) 
	{
		super(downscale, imageTransformer);
		this.antialias = antialias;
	}

	public boolean update()
	{
		if (imageData == null || getPreciseBitmap() == null) return false;
		
		PointD lt = getImageTransformer().screenToImage(new PointD(0, 0));
		
		if (!getPreciseBitmap().getPixelsRGBIntoByteBuffer(imageData.data, imageData.bytesPerLine,
				(int)lt.getX(), (int)lt.getY(), 
				getViewWidth() / getDownscale(), getViewHeight() / getDownscale(), 
				1000, 
				(float)(getImageTransformer().getZoom() / getDownscale()),
				antialias,
				new PreciseBitmapGetPixelsCallback() 
				{
					@Override
					public boolean report() 
					{
						return !Thread.interrupted();
					}
				})
			) return false;
		
		return true;		
	}
	
    public void draw(GC gc)
    {
		if (imageData != null)
		{
			Image image = new Image(gc.getDevice(), imageData);
			//gc.setBackground(new Color(gc.getDevice(), new RGB(0, 0, 0)));
			//gc.fillRectangle(0, 0, getViewWidth(), getViewHeight());
			gc.drawImage(image, 
					0, 0, getViewWidth() / getDownscale(), getViewHeight() / getDownscale(), 
					0, 0, getViewWidth(), getViewHeight());
			image.dispose();
		}
    }
	
    public ImageData getImageData()
    {
    	return imageData;
    }
        
	@Override
	public void setViewSize(PointD viewSize) 
	{
		super.setViewSize(viewSize);

		imageData = new ImageData(
				(int)viewSize.getX() / getDownscale(), 
				(int)viewSize.getY() / getDownscale(), 
				24, new PaletteData(0x0000FF, 0x00FF00, 0xFF0000)
		);
	}
}
