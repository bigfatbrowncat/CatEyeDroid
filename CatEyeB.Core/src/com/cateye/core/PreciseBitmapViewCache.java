package com.cateye.core;

public class PreciseBitmapViewCache
{
	private PointD downscaledCenter = new PointD(0, 0);
	private double downscaledDispersion = 1;	// To avoid NaNs
	private ImageCoordinatesTransformer imageTransformer;
    private int downscale;
    private int viewWidth, viewHeight;
    private IPreciseBitmap preciseBitmap;
    
	public ImageCoordinatesTransformer getImageTransformer()
	{
		return imageTransformer;
	}
    public void setImageTransformer(ImageCoordinatesTransformer imageTransformer)
    {
    	this.imageTransformer = imageTransformer;
    }
    
    public PreciseBitmapViewCache(int downscale, ImageCoordinatesTransformer imageTransformer)
    {
    	this.downscale = downscale;
    	this.imageTransformer = imageTransformer;
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

		/*PointD imageSize = new PointD(preciseBitmap.getWidth(), preciseBitmap.getHeight());
		imageTransformer.setImageSize(imageSize);*/
	}
	public void setViewSize(PointD viewSize)
	{
		/*PointD screenSize = new PointD(viewSize.getX() / downscale, viewSize.getY() / downscale);
		imageTransformer.setScreenSize(screenSize);*/
    	this.viewWidth = (int)viewSize.getX();
    	this.viewHeight = (int)viewSize.getY();
	}
}
