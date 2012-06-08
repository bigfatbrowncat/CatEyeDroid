package com.cateye.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.SWT;

public class ImageWindowStackComposite extends Composite 
{
	public enum ActiveScreen { Loading, View }
	
	private LoadingPageComposite loadingScreen;
	private ImageViewPageComposite preciseBitmapViewComposite;
	private StackLayout mainStackLayout;
	private ActiveScreen activeScreen;
	
	public ImageWindowStackComposite(Composite parent, int style) 
	{
		super(parent, style);
		mainStackLayout = new StackLayout(); 
		setLayout(mainStackLayout);
		
		loadingScreen = new LoadingPageComposite(this, SWT.NONE);
		preciseBitmapViewComposite = new ImageViewPageComposite(this, SWT.NONE);
	}

	public void setActiveScreen(ActiveScreen value)
	{
		activeScreen = value;
		
		switch (activeScreen)
		{
		case Loading:
			mainStackLayout.topControl = loadingScreen;
			break;
		case View:
			mainStackLayout.topControl = preciseBitmapViewComposite;
			break;
		default:
			throw new RuntimeException("Strange case");
		}
		
		this.layout();
	}
	
	public ActiveScreen getActiveScreen()
	{
		return activeScreen;
	}
	
	@Override
	protected void checkSubclass() 
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public LoadingPageComposite getLoadingScreen() 
	{
		return loadingScreen;
	}

	public ImageViewPageComposite getPreciseBitmapViewComposite()
	{
		return preciseBitmapViewComposite;
	}
}
