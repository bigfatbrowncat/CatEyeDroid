package com.cateye.core;

public class IncorrectImageLoaderRelation extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Image image;
	private IImageLoader loader;
	public IncorrectImageLoaderRelation(Image image, IImageLoader loader)
	{
		super("incorrect relations between image and loader");
		this.image = image;
		this.loader = loader;
	}
	public Image getImage() {
		return image;
	}
	public IImageLoader getLoader() {
		return loader;
	}
}
