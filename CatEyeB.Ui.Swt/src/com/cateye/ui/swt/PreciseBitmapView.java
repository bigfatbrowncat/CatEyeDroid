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
    private Image img; 
	private IPreciseBitmap preciseBitmap;

	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
		if (preciseBitmap != null)
		{
			pixels = new int[preciseBitmap.getWidth() * preciseBitmap.getHeight()];
			imgData = new ImageData(preciseBitmap.getWidth(), preciseBitmap.getHeight(), 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
			img = new Image(getDisplay(), imgData);
		}
		else
		{
			pixels = null;
			imgData = null;
			if (img != null)
			{
				img.dispose();
				img = null;
			}
		}
	}
	
	@Override
	public void dispose() {
		if (img != null) img.dispose();
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
					Rectangle rect = getClientArea();
					rect.width = Math.min(rect.width, preciseBitmap.getWidth());
					rect.height = Math.min(rect.height, preciseBitmap.getHeight());
					
					pixels = preciseBitmap.getPixels(pixels, 0, 0, rect.width, rect.height, 500, 1f);
					
					for (int i = 0; i < rect.width; i++)
					{
						for (int j = 0; j < rect.height; j++)
						{
							imgData.setPixel(i, j, pixels[j * rect.width + i]);
						}
					}

					e.gc.drawImage(img, 0, 0);
					
				}
			}
		});
	}

}
