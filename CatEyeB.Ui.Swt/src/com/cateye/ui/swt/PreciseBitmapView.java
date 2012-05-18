package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.cateye.core.IPreciseBitmap;

public class PreciseBitmapView extends Canvas
{
    private int[] pixels;
    private int cacheWidth, cacheHeight;
    
    private ImageData imgData; 
	private IPreciseBitmap preciseBitmap;

	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
		updateImageCache();
	}
	
	public void updateImageCache()
	{
		int w = preciseBitmap.getWidth();
		int h = preciseBitmap.getHeight();
		if (cacheWidth < w || cacheHeight < h)
		{
			// Recreating the pixels array if the image has grown
			pixels = new int[w * h];
		}

		pixels = preciseBitmap.getPixels(pixels, true, 0, 0, w, h, 500, 0.5f);
		imgData = new ImageData(w, h, 24, new PaletteData(0x0000FF, 0x00FF00, 0xFF0000));	// This palette data is the most native for Windows
		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				imgData.setPixel(i, j, pixels[j * w + i]);
			}
		}
		
		cacheWidth = w;
		cacheHeight = h;
	}
	
	@Override
	public void dispose()
	{
		//if (img != null) img.dispose();
		super.dispose();
	}
	
	public PreciseBitmapView(Composite parent)
	{
		super(parent, SWT.NO_BACKGROUND);

		addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e)
			{
				if (preciseBitmap != null)
				{
					Image img = new Image(getDisplay(), imgData);
					e.gc.drawImage(img, 0, 0);
					img.dispose();
				}
			}
		});
	}

}
