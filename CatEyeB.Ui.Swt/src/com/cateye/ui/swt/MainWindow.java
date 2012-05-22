package com.cateye.ui.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.IProgressListener;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;
import com.cateye.core.jni.RawImageLoader;
import com.cateye.ui.swt.MainComposite.ActiveScreen;

public class MainWindow extends Shell
{
	private RawImage image;
	private MainComposite mainComposite;
	private File imageFile;
	private RawImageLoader imageLoader;
	
	IProgressListener loadingProgressListener = new IProgressListener()
	{
		@Override
		public boolean invoke(Object sender, final float progress)
		{
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
		imageFile = new File(filename);
		
		this.setText(imageFile.getName() + " – CatEye");
		mainComposite.getLoadingScreen().setFilename(imageFile.getName());
	}
	
	public File getImageFile()
	{
		return imageFile;
	}
	
	private void showError(String message)
	{
		MessageBox err = new MessageBox(MainWindow.this, SWT.ERROR);
		err.setText("CatEye");
		err.setMessage(message);
		err.open();
		
		MainWindow.this.close();		
	}
	
	private boolean checkImageFile(String filename)
	{
		// Checking if the file is valid
		File file = new File(filename);
		if (!file.exists())
		{
			showError("The image file " + filename + " doesn't exist");
			return false;
		}
		if (!file.isFile())
		{
			showError("The name doesn't point to a file:\n" + filename);
			return false;
		}
		if (!file.canRead())
		{
			showError("You don't have rights to open the image file for reading: \n" + filename);
			return false;
		}
		return true;
	}
	
	public void startWithFile(final String filename)
	{
		setFilename(filename);
		if (checkImageFile(filename))
		{
			open();
			
			Runnable imageLoadingRunnable = new Runnable()
			{
				private void showErrorInUi(final String message)
				{
					getDisplay().syncExec(new Runnable() {
						
						@Override
						public void run() 
						{
							showError(message);
						}
					});			
				}
				
				@Override
				public void run() 
				{
					// Loading the image into view
					image = imageLoader.createImageFromFile(filename);
					IPreciseBitmap pb = null;
					try 
					{
						pb = image.getBitmap();
					} 
					catch (final ImageLoaderException e) 
					{
						showErrorInUi("Can't open the file " + filename + ".\n" + e.getMessage());
						return;
					}

					final IPreciseBitmap preciseBitmap = pb;
					getDisplay().syncExec(new Runnable() 
					{
						
						@Override
						public void run() 
						{
							mainComposite.getPreciseBitmapView().setPreciseBitmap(preciseBitmap);
							mainComposite.setActiveScreen(ActiveScreen.View);
						}
						
					});
			
				}
			};
			
			new Thread(imageLoadingRunnable).start();
			
			while (!isDisposed())
			{
				if (!getDisplay().readAndDispatch())
				{
					getDisplay().sleep();
				}
			}
		}
	}
	
	public MainWindow(RawImageLoader imageLoader) 
	{
		this.imageLoader = imageLoader;
		prepareContent();
	}
	
	@Override
	protected void checkSubclass() 
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
