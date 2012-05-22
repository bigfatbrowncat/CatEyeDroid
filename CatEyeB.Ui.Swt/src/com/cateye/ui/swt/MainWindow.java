package com.cateye.ui.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;
import com.cateye.core.Image;
import com.cateye.core.jni.RawImageLoader;
import com.cateye.ui.swt.MainComposite.ActiveScreen;

public class MainWindow extends Shell
{
	private Image image;
	private MainComposite mainComposite;
	private String filename;
	private static RawImageLoader imageLoader = new RawImageLoader();
	
	Runnable loaderRunnable = new Runnable()
	{
		@Override
		public void run() 
		{
			// Loading the image into view
			image = imageLoader.loadImageFromFile(filename);
			final IPreciseBitmap pb = image.getBitmap();
			
			getDisplay().syncExec(new Runnable() {
				
				@Override
				public void run() 
				{
					mainComposite.getPreciseBitmapView().setPreciseBitmap(pb);
					mainComposite.setActiveScreen(ActiveScreen.View);
				}
				
			});
		}
	};
	
	IProgressListener loadingProgressListener = new IProgressListener()
	{
		@Override
		public boolean invoke(Object sender, final float progress) {
			getDisplay().syncExec(new Runnable() {

				@Override
				public void run() 
				{
					mainComposite.getLoadingScreen().setProgress((int)(progress * 100));
				}

			});
			
			return true;
		}
	};
	
	private void prepareContent()
	{
		setText("CatEye");
		setSize(800, 600);
		setMinimumSize(320, 240);
		
		setLayout(new FillLayout());
		
		mainComposite = new MainComposite(this, SWT.NONE);
		mainComposite.setActiveScreen(ActiveScreen.Loading);
	
		imageLoader.addProgressListener(loadingProgressListener);
	}
	
	protected void setFilename(String filename)
	{
		// Setting filename
		this.filename = filename;
		
		// Cutting the path away
		int n = filename.lastIndexOf(File.separator);
		String filenameNoPath = filename.substring(n + 1);
		mainComposite.getLoadingScreen().setFilename(filenameNoPath);
		
	}
	
	public void startWithFile(String filename)
	{
		setFilename(filename);
		
		open();
		new Thread(loaderRunnable).start();
		
		while (!isDisposed())
		{
			if (!getDisplay().readAndDispatch())
			{
				getDisplay().sleep();
			}
		}
	}
	
	public MainWindow() 
	{
		prepareContent();
	}
	
	@Override
	protected void checkSubclass() 
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
