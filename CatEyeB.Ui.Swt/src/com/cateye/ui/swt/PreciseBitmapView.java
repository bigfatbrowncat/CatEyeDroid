package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.PointD;
import com.cateye.core.RestrictedImageCoordinatesTransformer;

public class PreciseBitmapView extends Composite
{
	private IPreciseBitmap preciseBitmap;
	private RestrictedImageCoordinatesTransformer imageTransformer = new RestrictedImageCoordinatesTransformer();
	private boolean down;
	private SwtPreciseBitmapViewCache[] cache = new SwtPreciseBitmapViewCache[]
	{
		new SwtPreciseBitmapViewCache(2, false, imageTransformer),	
		new SwtPreciseBitmapViewCache(1, true, imageTransformer)	
	};
    private static final int LQ = 0; 
    private static final int HQ = 1;
    
    private volatile int activeImageIndex = LQ;
	
	private Thread polishingDrawingThread = null;
	
	private final Runnable polishingDrawingRunnable = new Runnable()
	{
		@Override
		public void run() 
		{
			if (cache[HQ].update())
			{
	        	activeImageIndex = HQ;
	        	if (!isDisposed())
	        	{
		        	getDisplay().asyncExec(new Runnable() 
		        	{
						@Override
						public void run() 
						{
							PreciseBitmapView.this.redraw();
						}
					});
	        	}
	        }
		}
	};
    
	public PreciseBitmapView(Composite parent)
	{
		super(parent, SWT.NO_BACKGROUND);

		addPaintListener(paintListener);
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseMoveListener);
		addControlListener(controlListener);

		getDisplay().addFilter(SWT.MouseWheel, mouseWheelListener);		
	}
	
	public void setPreciseBitmap(IPreciseBitmap value) 
	{
		preciseBitmap = value;
		
		// Setting imageTransformer's imageSize
		PointD imageSize = new PointD(preciseBitmap.getWidth(), preciseBitmap.getHeight());
		imageTransformer.setImageSize(imageSize);
		
		for (int i = 0; i < 2; i++)
		{
			cache[i].setPreciseBitmap(value);
		}

		cache[LQ].update();
		activeImageIndex = LQ;
		PreciseBitmapView.this.redraw();
		PreciseBitmapView.this.update();
	}
	
	ControlListener controlListener = new ControlListener() 
	{		
		@Override
		public void controlResized(ControlEvent arg0)
		{
			PointD screenSize = new PointD(getClientArea().width, getClientArea().height); 
			imageTransformer.setScreenSize(screenSize);
			
			for (int i = 0; i < 2; i++)
			{
				cache[i].setViewSize(screenSize);
			}
		
			cache[LQ].update();
			activeImageIndex = LQ;
			PreciseBitmapView.this.redraw();
			PreciseBitmapView.this.update();

		}
		
		@Override
		public void controlMoved(ControlEvent arg0) {}
	};
	
	PaintListener paintListener = new PaintListener()
	{
		@Override
		public void paintControl(PaintEvent e)
		{
			cache[activeImageIndex].draw(e.gc);
			if (activeImageIndex == 0) polish();

		}
	};
	
	MouseListener mouseListener = new MouseListener() 
	{
		@Override
		public void mouseUp(MouseEvent arg0) 
		{
			down = false;
		}
		
		@Override
		public void mouseDown(MouseEvent arg0) 
		{
			for (int i = 0; i < 2; i++)
			{
				cache[i].setCenter(new PointD(arg0.x, arg0.y));
			}
			
			down = true;
		}
		
		@Override
		public void mouseDoubleClick(MouseEvent arg0) {}
	};
	
	MouseMoveListener mouseMoveListener = new MouseMoveListener() 
	{		
		@Override
		public void mouseMove(MouseEvent arg0) 
		{
			if (down)
			{
				int x1 = arg0.x;
				int y1 = arg0.y;
				imageTransformer.addPan(new PointD(x1 - cache[activeImageIndex].getCenter().getX(),
													y1 - cache[activeImageIndex].getCenter().getY()));
				for (int i = 0; i < 2; i++)
				{
					cache[i].setCenter(new PointD(x1, y1));
				}
				cache[LQ].update();
				activeImageIndex = LQ;
				PreciseBitmapView.this.redraw();
				PreciseBitmapView.this.update();
			}
		}
	};
	
	Listener mouseWheelListener = new Listener() 
	{
		@Override
		public void handleEvent(Event arg0) 
		{
			double newDispersion = Math.pow(3, (double)arg0.count / 50) * cache[activeImageIndex].getDispersion();
			
			imageTransformer.zoomUponScreenPoint(new PointD(arg0.x, arg0.y), newDispersion / cache[activeImageIndex].getDispersion());
			
			for (int i = 0; i < 2; i++)
			{
				cache[i].setDispersion(newDispersion);
			}
			
			cache[LQ].update();
			activeImageIndex = LQ;
			PreciseBitmapView.this.redraw();
			PreciseBitmapView.this.update();
		}
	};

	private void polish()
	{
		if (polishingDrawingThread != null && polishingDrawingThread.isAlive())
		{
			polishingDrawingThread.interrupt();
			try 
			{
				if (polishingDrawingThread.isAlive()) polishingDrawingThread.join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		polishingDrawingThread = new Thread(this.polishingDrawingRunnable);
		polishingDrawingThread.start();
	}
	
	@Override
	public void dispose() 
	{
		getDisplay().removeFilter(SWT.MouseWheel, mouseWheelListener);
		super.dispose();
	}

	
}
