package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.gdip.BitmapData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IPreviewBitmap;

public class PreciseBitmapView extends Canvas
{
    private int[] pixels;
    private ImageData imgData; 
	private IPreciseBitmap preciseBitmap;

	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
		if (preciseBitmap != null)
		{
			pixels = new int[preciseBitmap.getWidth() * preciseBitmap.getHeight()];
		}
		else
		{
			pixels = null;
		}
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
					Rectangle rect = getClientArea();
					rect.width = Math.min(rect.width, preciseBitmap.getWidth());
					rect.height = Math.min(rect.height, preciseBitmap.getHeight());
					
					pixels = preciseBitmap.getPixels(pixels, 0, 0, rect.width, rect.height, 500, 0.5f);
					
					imgData = new ImageData(rect.width, rect.height, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
					for (int i = 0; i < rect.width; i++)
					{
						for (int j = 0; j < rect.height; j++)
						{
							imgData.setPixel(i, j, pixels[j * rect.width + i]);
						}
					}

					Image img = new Image(e.display, imgData);

					e.gc.drawImage(img, 0, 0);
					
					img.dispose();
				}
			}
		});
	}

}
