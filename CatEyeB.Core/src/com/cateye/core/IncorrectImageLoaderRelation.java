package com.cateye.core;

import com.cateye.core.jni.RawImage;
import com.cateye.core.jni.RawImageLoader;

public class IncorrectImageLoaderRelation extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private RawImage image;
	private RawImageLoader loader;
	public IncorrectImageLoaderRelation(RawImage image, RawImageLoader loader)
	{
		super("incorrect relations between image and loader");
		this.image = image;
		this.loader = loader;
	}
	public RawImage getImage() {
		return image;
	}
	public RawImageLoader getLoader() {
		return loader;
	}
}
