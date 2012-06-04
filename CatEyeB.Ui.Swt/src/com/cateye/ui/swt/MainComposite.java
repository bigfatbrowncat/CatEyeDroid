package com.cateye.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.SWT;

public class MainComposite extends Composite 
{
	public enum ActiveScreen { Loading, View }
	
	private LoadingScreenComposite loadingScreen;
	private PreciseBitmapViewComposite preciseBitmapViewComposite;
	private StackLayout mainStackLayout;
	private ActiveScreen activeScreen;
	
	public MainComposite(Composite parent, int style) 
	{
		super(parent, style);
		mainStackLayout = new StackLayout(); 
		setLayout(mainStackLayout);
		
		loadingScreen = new LoadingScreenComposite(this, SWT.NONE);
		preciseBitmapViewComposite = new PreciseBitmapViewComposite(this, SWT.NONE);
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

	public LoadingScreenComposite getLoadingScreen() 
	{
		return loadingScreen;
	}

	public PreciseBitmapViewComposite getPreciseBitmapViewComposite()
	{
		return preciseBitmapViewComposite;
	}
}
