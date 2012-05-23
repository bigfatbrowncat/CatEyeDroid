package com.cateye.core;

/**
 * <p><b>RestrictedImageCoordinatesTransformer</b> extends {@link ImageCoordinatesTransformer}</p>
 * <p>This class converts screen coordinates to image coordinates and vice versa.</p>
 * <p>Additionally it constraints panning and zooming with some rules:</p>
 * <ol>
 * 		<li>
 * 			If the image width or height (with the current zoom factor) is smaller than screen's one, 
 * 			it will be centered in that direction
 * 		</li>
 * 		<li>
 * 			In other cases you are unable to pan screen view out of image.
 * 		</li>
 * </ol>
 * 
 * @author Ilya Mizus
 */

public class RestrictedImageCoordinatesTransformer extends ImageCoordinatesTransformer 
{
	/**
	 * <p><b>For internal usage</b></p>
	 * <p>This function is called every time when panning or zooming
	 * changed.</p>
	 */
	private void applyConstraints()
	{
		PointD imageSizeOnScreen = imageSizeOnScreen();
		
		// Hard restrictions
		boolean panCenterHorizontally = imageSizeOnScreen.getX() < getScreenSize().getX();
		boolean panCenterVertically = imageSizeOnScreen.getY() < getScreenSize().getY();

		PointD currentPan = super.getPan();
		if (panCenterHorizontally && panCenterVertically) 
		{
			super.setPan(new PointD(0, 0));
		}
		else if (panCenterHorizontally)
		{
			super.setPan(new PointD(0, currentPan.getY()));
		}
		else if (panCenterVertically)
		{
			
			super.setPan(new PointD(currentPan.getX(), 0));
		}
		
		// Soft restrictions
		PointD scrLT = imageToScreen(new PointD(0, 0));
		PointD scrRB = imageToScreen(getImageSize());
		
		if (!panCenterHorizontally)
		{
			if (scrLT.getX() > 0) 
			{
				super.addPan(new PointD(-scrLT.getX(), 0));
			}
			if (scrRB.getX() < getScreenSize().getX())
			{
				super.addPan(new PointD(getScreenSize().getX() - scrRB.getX(), 0));
			}
		}
		if (!panCenterVertically)
		{
			if (scrLT.getY() > 0) 
			{
				super.addPan(new PointD(0, -scrLT.getY()));
			}
			if (scrRB.getY() < getScreenSize().getY())
			{
				super.addPan(new PointD(0, getScreenSize().getY() - scrRB.getY()));
			}
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
