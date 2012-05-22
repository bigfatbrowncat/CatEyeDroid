package com.cateye.core;

public class RestrictedImageCoordinatesTransformer extends ImageCoordinatesTransformer 
{
	protected void applyConstraints()
	{
		PointD imageSizeOnScreen = imageSizeOnScreen();
		boolean panCenterHorizontally = imageSizeOnScreen.getX() < getScreenSize().getX();
		boolean panCenterVertically = imageSizeOnScreen.getY() < getScreenSize().getY();
		
//		System.out.println(imageSizeOnScreen().getX() + ", " + imageSizeOnScreen().getY());
		
		PointD currentPan = super.getPan();
		if (panCenterHorizontally && panCenterVertically) 
		{
			super.setPan(new PointD(0, 0));
			System.out.print("F");
		}
		else if (panCenterHorizontally)
		{
			super.setPan(new PointD(0, currentPan.getY()));
			System.out.print("H");
		}
		else if (panCenterVertically)
		{
			super.setPan(new PointD(currentPan.getX(), 0));
			System.out.print("V");
		}
	}
	
	@Override
	public void addPan(PointD panDelta) 
	{

		super.addPan(panDelta);
		applyConstraints();
	}
	
	@Override
	public void zoomBy(double zoomDelta) 
	{

		super.zoomBy(zoomDelta);
		applyConstraints();
	}
	
	@Override
	public void zoomUponScreenPoint(PointD zoomCenter, double zoomDelta) 
	{

		super.zoomUponScreenPoint(zoomCenter, zoomDelta);
		applyConstraints();
	}
	
	@Override
	public void setImageSize(PointD value) 
	{

		super.setImageSize(value);
		applyConstraints();
	}
	
	@Override
	public void setScreenSize(PointD value)
	{

		super.setScreenSize(value);
		applyConstraints();
	}
	
	@Override
	public void setPan(PointD value)
	{

		super.setPan(value);
		applyConstraints();
	}
	
	@Override
	public void setZoom(double value)
	{

		super.setZoom(value);
		applyConstraints();
	}
	
}
