package com.cateye.core;

public class ImageTransformer 
{
	private PointD screenSize, imageSize, pan;
	private double zoom;
	
	private PointD mu;
	private void updateMu()
	{
		mu = new PointD((imageSize.getX() - zoom * screenSize.getX()) / 2 - zoom * pan.getX(),
						(imageSize.getY() - zoom * screenSize.getY()) / 2 - zoom * pan.getY());
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
		return new PointD(r.getX() / zoom + mu.getX(),
		                  r.getY() / zoom + mu.getY());
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
