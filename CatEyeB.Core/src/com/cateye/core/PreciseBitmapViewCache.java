package com.cateye.core;

public class PreciseBitmapViewCache
{
	private PointD downscaledCenter = new PointD(0, 0);
	private double downscaledDispersion = 1;	// To avoid NaNs
	private RestrictedImageCoordinatesTransformer imageTransformer = new RestrictedImageCoordinatesTransformer();
    private int downscale;
    private int viewWidth, viewHeight;
    private IPreciseBitmap preciseBitmap;
    
    public PreciseBitmapViewCache(int downscale)
    {
    	this.downscale = downscale;
    	
    	imageTransformer.setZoom(1.0 / downscale);
    }
    
	public PointD getCenter() 
	{
		return PointD.multiply(downscaledCenter, downscale);
	}
	public void setCenter(PointD center) 
	{
		this.downscaledCenter = PointD.multiply(center, 1.0 / downscale);
	}
	public double getDispersion() 
	{
		return downscaledDispersion * downscale;
	}
	public void setDispersion(double dispersion)
	{
		this.downscaledDispersion = dispersion / downscale;
	}
	
	public void pan(PointD newCenter)
	{
		PointD newDownscaledCenter = PointD.multiply(newCenter, 1.0 / downscale);
		imageTransformer.addPan(new PointD(newDownscaledCenter.getX() - downscaledCenter.getX(), newDownscaledCenter.getY() - downscaledCenter.getY()));
		
		this.downscaledCenter = newDownscaledCenter;
	}
	
	public void panAndZoom(PointD center, double dispersion)
	{
		PointD newDownscaledCenter = PointD.multiply(center, 1.0 / downscale);
		double newDownscaledDispersion = dispersion / downscale;

		imageTransformer.addPan(new PointD(newDownscaledCenter.getX() - downscaledCenter.getX(), newDownscaledCenter.getY() - downscaledCenter.getY()));
		imageTransformer.zoomUponScreenPoint(newDownscaledCenter, newDownscaledDispersion / downscaledDispersion);

		this.downscaledCenter = newDownscaledCenter;
		this.downscaledDispersion = newDownscaledDispersion;		
	}
	
	public RestrictedImageCoordinatesTransformer getImageTransformer()
	{
		return imageTransformer;
	}
	public int getDownscale()
	{
		return downscale;
	}
	public void setDownscale(int downscale)
	{
		this.downscale = downscale;
	}
	public int getViewWidth()
	{
		return viewWidth;
	}
	public int getViewHeight() 
	{
		return viewHeight;
	}
	public IPreciseBitmap getPreciseBitmap() 
	{
		return preciseBitmap;
	}
	public void setPreciseBitmap(IPreciseBitmap preciseBitmap)
	{
		this.preciseBitmap = preciseBitmap;

		PointD imageSize = new PointD(preciseBitmap.getWidth(), preciseBitmap.getHeight());
		imageTransformer.setImageSize(imageSize);
	}
	public void setViewSize(PointD viewSize)
	{
		PointD screenSize = new PointD(viewSize.getX() / downscale, viewSize.getY() / downscale);
		imageTransformer.setScreenSize(screenSize);
    	this.viewWidth = (int)viewSize.getX();
    	this.viewHeight = (int)viewSize.getY();
	}
}
