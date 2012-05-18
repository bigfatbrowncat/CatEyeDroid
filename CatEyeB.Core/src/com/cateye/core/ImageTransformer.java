package com.cateye.core;

public class ImageTransformer 
{
	private PointD screenSize, imageSize, pan;
	private double zoom;
	
	private PointD mu;
	private void updateMu()
	{
		mu = new PointD((imageSize.getX() - screenSize.getX() / zoom) / 2 - pan.getX() / zoom,
						(imageSize.getY() - screenSize.getY() / zoom) / 2 - pan.getY() / zoom);
		//System.out.println("mu = " + mu);
	}
	
	public ImageTransformer()
	{
		this(new PointD(0, 0), new PointD(0, 0), new PointD(0, 0), 1);
	}
	
	public ImageTransformer(PointD screenSize, PointD imageSize, PointD pan, double zoom)
	{
		this.screenSize = screenSize;
		this.imageSize = imageSize;
		this.pan = pan;
		this.zoom = zoom;
		updateMu();
	}
	
	public void multiplyPan(PointD pan)
	{
		this.pan = new PointD(this.pan.getX() + pan.getX(),
		                      this.pan.getY() + pan.getY());
		updateMu();
	}

	public void multiplyZoom(double zoom)
	{
		this.zoom *= zoom;
		updateMu();
	}

	public PointD screenToImage(PointD r)
	{
		double x = r.getX() * zoom + mu.getX();
		double y = r.getY() * zoom + mu.getY();
		
		/*x = Math.min(Math.max(0, x), imageSize.getX());
		y = Math.min(Math.max(0, y), imageSize.getY());*/
		
		return new PointD(x, y);
	}
	
	public void zoomUponPoint(PointD zoomCenter, double zoom)
	{
		this.pan = new PointD(
				pan.getX() * zoom + (1 - zoom) * (zoomCenter.getX() - 0.5 * screenSize.getX()), 
				pan.getY() * zoom + (1 - zoom) * (zoomCenter.getY() - 0.5 * screenSize.getY())
		);
		this.zoom *= zoom;
		updateMu();
	}
	
	public void setScreenSize(PointD value)
	{
		screenSize = value;
		updateMu();
	}

	public void setImageSize(PointD value)
	{
		imageSize = value;
		updateMu();
	}

	public void setPan(PointD value)
	{
		pan = value;
		updateMu();
	}

	public void setZoom(double value)
	{
		zoom = value;
		updateMu();
	}
	
	public double getZoom()
	{
		return zoom;
	}
}
