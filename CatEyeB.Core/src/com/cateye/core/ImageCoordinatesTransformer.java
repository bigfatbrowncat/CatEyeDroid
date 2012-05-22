package com.cateye.core;

public class ImageCoordinatesTransformer 
{
	/* ************** Published fields ********************************************************* */
	
	private PointD screenSize, imageSize, pan;
	private double zoom;
	
	
	/* ************** Private precalculated fields ********************************************* */
	
	/**
	 * This factor depends on Pan, Zoom, ScreenSize and ImageSize values 
	 */
	private PointD mu;

	
	/* ************** Precalculators *********************************************************** */
	
	/**
	 * As far as Mu factor depends on Pan, Zoom, ScreenSize and ImageSize values,
	 * this function should be called in their mutators 
	 */
	private void calculateMu()
	{
		mu = new PointD((imageSize.getX() - screenSize.getX() / zoom) / 2 - pan.getX() / zoom,
						(imageSize.getY() - screenSize.getY() / zoom) / 2 - pan.getY() / zoom);
	}
	
	
	/* ************** Getters ****************************************************************** */
	
	/**
	 * Returns the full zoom value
	 */
	public final double getZoom()
	{
		return zoom;
	}	

	/**
	 * Returns the full pan value
	 */
	public final PointD getPan()
	{
		return pan;
	}
	
	/**
	 * Returns the Screen size in screen coordinates (pixels)
	 * @return
	 */
	public final PointD getScreenSize()
	{
		return screenSize;
	}

	/**
	 * Returns the Image size in image coordinates (pixels)
	 * @return
	 */
	public final PointD getImageSize() 
	{
		return imageSize;
	}
	
	/**
	 * Converts screen coordinates to image coordinates. 
	 * Answers the question <i>"Which pixel of the image appears at the (x, y) 
	 * position of the screen?"</i>
	 * 
	 * @param screenPoint Screen coordinates value
	 * @return Image coordinates value
	 */
	public PointD screenToImage(PointD screenPoint)
	{
		double imgX = screenPoint.getX() * zoom + mu.getX();
		double imgY = screenPoint.getY() * zoom + mu.getY();
		
		return new PointD(imgX, imgY);
	}
	
	/**
	 * Converts image coordinates to screen coordinates. 
	 * Answers the question <i>"Where on the screen I could see this &#151; (x, y) pixel of the image?"</i>
	 * 
	 * @param imagePoint Image coordinates value
	 * @return Screen coordinates value
	 */
	public PointD imageToScreen(PointD imagePoint)
	{
		double scrX = (imagePoint.getX() - mu.getX()) / zoom;
		double scrY = (imagePoint.getY() - mu.getY()) / zoom;
		
		return new PointD(scrX, scrY);
	}

	/**
	 * Calculates the width and the height of the image in screen coordinates.
	 * Answers the question <i>"What size will this image have on the screen? What width and height?"</i>
	 * @return
	 */
	public PointD imageSizeOnScreen()
	{
		PointD scrLT = imageToScreen(new PointD(0, 0));
		PointD scrRB = imageToScreen(imageSize);

		System.out.println(imageToScreen(new PointD(0, 0)).getX() + ", " + imageToScreen(new PointD(0, 0)).getY());
		
		return new PointD(scrRB.getX() - scrLT.getX(),
		                  scrRB.getY() - scrLT.getY());
	}
	
	/* ************** Constructors ************************************************************* */
	
	/**
	 * Default constructor. Sets zero-sized screen, zero-sized image and zoom equal to 1 
	 */
	public ImageCoordinatesTransformer()
	{
		this(new PointD(0, 0), new PointD(0, 0), new PointD(0, 0), 1);
	}
	
	/**
	 * Constructs ImageCoordinateTransformer object
	 * @param screenSize Screen size in pixels
	 * @param imageSize Image size in pixels
	 * @param pan Pan vector in screen pixels
	 * @param zoom Zoom factor value
	 */
	public ImageCoordinatesTransformer(PointD screenSize, PointD imageSize, PointD pan, double zoom)
	{
		this.screenSize = screenSize;
		this.imageSize = imageSize;
		this.pan = pan;
		this.zoom = zoom;
		calculateMu();
	}
	

	/* ************** Setters / mutators ******************************************************* */

	/**
	 * Adds some delta vector to Pan
	 * @param panDelta
	 */
	public void addPan(PointD panDelta)
	{
		this.pan = new PointD(this.pan.getX() + panDelta.getX(),
		                      this.pan.getY() + panDelta.getY());
		calculateMu();
	}

	/**
	 * Zooms the image by some delta value.
	 * Zooming center is the center of the image.
	 * @param zoomDelta
	 */
	public void zoomBy(double zoomDelta)
	{
		this.zoom *= zoomDelta;
		calculateMu();
	}

	/**
	 * Zooms the image by some delta value upon a point 
	 * on the screen.
	 * @param zoomCenter The zooming center point in screen coordinates 
	 * @param zoomDelta The zooming delta value
	 */
	public void zoomUponScreenPoint(PointD zoomCenter, double zoomDelta)
	{
		this.pan = new PointD(
				pan.getX() * zoomDelta + (1 - zoomDelta) * (zoomCenter.getX() - 0.5 * screenSize.getX()), 
				pan.getY() * zoomDelta + (1 - zoomDelta) * (zoomCenter.getY() - 0.5 * screenSize.getY())
		);
		this.zoom *= zoomDelta;
		calculateMu();
	}

	/**
	 * Sets the new screen size in screen coordinates (screen pixels)
	 * <code>super.setScreenSize()</code> should be called in descendants.</code>
	 */
	public void setScreenSize(PointD value)
	{
		screenSize = value;
		calculateMu();
	}

	/**
	 * Sets the new image size in image coordinates (image pixels) 
	 * <code>super.setImageSize()</code> should be called in descendants.</code>
	 */
	public void setImageSize(PointD value)
	{
		imageSize = value;
		calculateMu();
	}

	/**
	 * Sets the new Pan value
	 * <code>super.setPan()</code> should be called in descendants.</code>
	 */
	public void setPan(PointD value)
	{
		pan = value;
		calculateMu();
	}
	
	/**
	 * Sets the new Zoom value 
	 * <code>super.setZoom()</code> should be called in descendants.</code>
	 */
	public void setZoom(double value)
	{
		zoom = value;
		calculateMu();
	}


}
