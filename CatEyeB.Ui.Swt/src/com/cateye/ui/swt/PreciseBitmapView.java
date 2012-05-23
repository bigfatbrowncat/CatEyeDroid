package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.ImageCoordinatesTransformer;
import com.cateye.core.PointD;
import com.cateye.core.RestrictedImageCoordinatesTransformer;

public class PreciseBitmapView extends Composite
{
	
    private int[] cachePixels = new int[getDisplay().getClientArea().width * getDisplay().getClientArea().height];

    private ImageData imgData; 
	private IPreciseBitmap preciseBitmap;
	
	private int x0, y0;
	private boolean down;
	private RestrictedImageCoordinatesTransformer imageTransformer = new RestrictedImageCoordinatesTransformer();

	PaintListener paintListener = new PaintListener()
	{
		@Override
		public void paintControl(PaintEvent e)
		{
			updateImageCache();

			if (preciseBitmap != null)
			{
				int viewWidth = PreciseBitmapView.this.getClientArea().width;
				int viewHeight = PreciseBitmapView.this.getClientArea().height;

				// TODO Why imageToScreen, and not screenToImage ???
				PointD lt = imageTransformer.screenToImage(new PointD(0, 0));
				PointD rb = imageTransformer.screenToImage(imageTransformer.getScreenSize());
				
				//System.out.println("x=0 on screen => x=" + lt.getX() + " on image; y=0 on screen => y=" + lt.getY() + " on image");
				//System.out.println("x=w on screen => x=" + rb.getX() + " on image; y=h on screen => y=" + rb.getY() + " on image");
				
				preciseBitmap.getPixelsRGBIntoByteBuffer(imgData.data, imgData.bytesPerLine,
						(int)lt.getX(), (int)lt.getY(), 
						viewWidth, viewHeight, 
						500, 
						(float)(1.0 / imageTransformer.getZoom()));
				
				Image img = new Image(getDisplay(), imgData);
				e.gc.drawImage(img, 0, 0);
				img.dispose();
			}
		}
	};
	
	MouseListener mouseListener = new MouseListener() {
		
		@Override
		public void mouseUp(MouseEvent arg0) 
		{
			down = false;
		}
		
		@Override
		public void mouseDown(MouseEvent arg0) 
		{
			x0 = arg0.x;
			y0 = arg0.y;
			down = true;
		}
		
		@Override
		public void mouseDoubleClick(MouseEvent arg0)
		{
			// TODO Add default zooming here
		}
	};
	
	MouseMoveListener mouseMoveListener = new MouseMoveListener() {
		
		@Override
		public void mouseMove(MouseEvent arg0) 
		{
			if (down)
			{
				int x1 = arg0.x;
				int y1 = arg0.y;
				imageTransformer.addPan(new PointD(x1 - x0, y1 - y0));
				x0 = x1; y0 = y1;
				PreciseBitmapView.this.redraw();
			}
		}
	};
	
	Listener mouseWheelListener = new Listener() {
		
		@Override
		public void handleEvent(Event arg0) 
		{
			double dZoom = Math.pow(3, (double)arg0.count / 50);
			//dZoom = Math.min(dZoom, 1.0 / imageTransformer.getZoom());
			imageTransformer.zoomUponScreenPoint(new PointD(arg0.x, arg0.y), dZoom);
			
			PreciseBitmapView.this.redraw();
		}
	};
	
	ControlListener controlListener = new ControlListener() {
		
		@Override
		public void controlResized(ControlEvent arg0) {
			imageTransformer.setScreenSize(new PointD(getClientArea().width, getClientArea().height));
		}
		
		@Override
		public void controlMoved(ControlEvent arg0)
		{
			// Do nothing
			
		}
	};
		
	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
	}
	

	public void updateImageCache()
	{
		int w = preciseBitmap.getWidth();
		int h = preciseBitmap.getHeight();
		imageTransformer.setImageSize(new PointD(w, h));
		imageTransformer.setScreenSize(new PointD(getClientArea().width, getClientArea().height));
		
		imgData = new ImageData(getClientArea().width, getClientArea().height, 24, new PaletteData(0x0000FF, 0x00FF00, 0xFF0000));
	}
	
	public PreciseBitmapView(Composite parent)
	{
		super(parent, SWT.NO_BACKGROUND);

		addPaintListener(paintListener);
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseMoveListener);
		addControlListener(controlListener);

		getDisplay().addFilter(SWT.MouseWheel, mouseWheelListener);		
		
		
	}
	
	@Override
	public void dispose() {
		getDisplay().removeFilter(SWT.MouseWheel, mouseWheelListener);
		super.dispose();
	}

}
