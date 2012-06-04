package com.cateye.ui.swt;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;
import com.cateye.ui.ImageLoaderReporter;
import com.cateye.ui.ImagesRegistry.LoadingState;
import com.cateye.ui.swt.MainComposite.ActiveScreen;

public class PreciseBitmapViewWindow extends Shell
{
    private String filename;
	private RawImage image;
	private MainComposite mainComposite;
	
	ImageLoaderReporter imageLoaderReporter = new ImageLoaderReporter() 
	{
		@Override
		public void reportSuccess(final IPreciseBitmap preciseBitmap)
		{
			getDisplay().syncExec(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					mainComposite.getPreciseBitmapViewComposite().getPreciseBitmapView().setPreciseBitmap(preciseBitmap);
					mainComposite.setActiveScreen(ActiveScreen.View);
				}
				
			});
		}
		
		@Override
		public void reportException(final ImageLoaderException e)
		{
			getDisplay().syncExec(new Runnable()
			{
				@Override
				public void run() 
				{
					System.out.print("error");
					showError("Can't open the file " + filename + ".\n" + e.getMessage());
				}
			});			
		}

		@Override
		public void reportProgress(final int progress)
		{
			getDisplay().syncExec(new Runnable() {

				@Override
				public void run() 
				{
					mainComposite.getLoadingScreen().setProgress((int)(progress * 100));
					if (mainComposite.getActiveScreen() != ActiveScreen.Loading)
					{
						mainComposite.setActiveScreen(ActiveScreen.Loading);
					}
				}

			});
		}
	};
	
	private void prepareContent()
	{
		setText("CatEye");
		setSize(800, 600);
		setMinimumSize(320, 240);
		
		setLayout(new FillLayout());

		// Creating the main widget
		mainComposite = new MainComposite(this, SWT.NONE);
	}

	private void showError(String message)
	{
		MessageBox err = new MessageBox(PreciseBitmapViewWindow.this, SWT.ERROR);
		err.setText("CatEye");
		err.setMessage(message);
		err.open();
		
		PreciseBitmapViewWindow.this.close();		
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
	
	/**
	 * Opens the window with a file. Returns when the window is closed.
	 * @param filename Image file name to load from
	 */
	public void openWithFile(final String filename)
	{
		// Setting the filename
		this.filename = filename;
		File file = new File(filename);
		mainComposite.getLoadingScreen().setFilename(file.getName());
		
		CatEyeApplication application = CatEyeApplication.getInstance();

		// Adding the image to registry
		if (checkImageFile(filename))
		{
			try 
			{
				image = application.getRegistry().requestOrLoadImage(filename);
			} 
			catch (IOException e)
			{
				showError("An unknown I/O error occured when loading " + filename);
				e.printStackTrace();
			}

			// Opening the window
			open();

			// Loading the image
			LoadingState currentState = application.getRegistry().loadImage(image, imageLoaderReporter);
			
			// If the loading pending, show the progress dialog
			if (currentState == LoadingState.NotLoadedYet || currentState == LoadingState.LoadingInProgress)
			{
				mainComposite.setActiveScreen(ActiveScreen.Loading);
			}			
			
			// Entering the message loop
			while (!isDisposed())
			{
				if (!getDisplay().readAndDispatch())
				{
					getDisplay().sleep();
				}
			}
		}
	}
	
	public PreciseBitmapViewWindow() 
	{
		prepareContent();
	}
	
	@Override
	protected void checkSubclass() 
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
