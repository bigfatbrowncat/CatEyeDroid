package com.cateye.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.SWT;

public class MainComposite extends Composite 
{
	public enum ActiveScreen { Loading, View }
	
	private LoadingScreenComposite loadingScreen;
	private PreciseBitmapView preciseBitmapView;
	private StackLayout mainStackLayout;
	private ActiveScreen activeScreen;
	
	public MainComposite(Composite parent, int style) 
	{
		super(parent, style);
		mainStackLayout = new StackLayout(); 
		setLayout(mainStackLayout);
		
		loadingScreen = new LoadingScreenComposite(this, SWT.NONE);
		preciseBitmapView = new PreciseBitmapView(this);
		
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
			mainStackLayout.topControl = preciseBitmapView;
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

	public PreciseBitmapView getPreciseBitmapView() {
		return preciseBitmapView;
	}
}
