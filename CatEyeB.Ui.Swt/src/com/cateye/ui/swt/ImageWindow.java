package com.cateye.ui.swt;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.exceptions.ImageLoaderException;
import com.cateye.core.jni.RawImage;
import com.cateye.ui.ImageLoaderReporter;
import com.cateye.ui.ImageProcessingReporter;
import com.cateye.ui.ImageProcessor;
import com.cateye.ui.ImagesRegistry.LoadingState;
import com.cateye.ui.swt.ImageWindowStackComposite.ActiveScreen;

public class ImageWindow extends Shell
{
    private String filename;
	private RawImage image;
	private ImageWindowStackComposite mainComposite;
	/**
	 * Flag for other threads and callbacks that the window is going to be closed.
	 * It should be handled as cancel of all pending operations.
	 */
	private volatile boolean closingPending = false; 
	
	private ImageProcessor imageProcessor = new ImageProcessor();
	
	/**
	 * Calls a <code>Runnable</code> from UI thread if possible 
	 * @param r The <code>Runnable</code> to invoke
	 * @return <code>true</code> if succeeded, <code>false</code> otherwise.
	 */
	protected boolean callFromUi(final Runnable r)
	{
		if (!isDisposed() && !getDisplay().isDisposed())
		{
			getDisplay().syncExec(new Runnable()
			{
				@Override
				public void run() 
				{
					if (!isDisposed())
					{
						r.run();
					}
				}
			});
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean canCloseNow()
	{
		// The only cause which prevents us from closing now is image processing
		
		if (imageProcessor.getState() == ImageProcessor.State.Idle && 
			CatEyeApplication.getInstance().getRegistry().getState() == com.cateye.ui.ImagesRegistry.State.Idle)
		{
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Tries to close the shell until it accepts to be closed.
	 * @return thread which spams the shell with close() method every 100 msec.
	 */
	protected Thread closeASAP()
	{
		Thread closureThread = new Thread() 
		{
			public void run() 
			{
				// While window is alive, trying to close it.
				while (!isDisposed())
				{
					// Sending "close" message
					getDisplay().syncExec(new Runnable() 
					{
						
						@Override
						public void run() 
						{
							close();
						}
					});
					
					// Waiting for the next train to arrive...
					try 
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e) 
					{
						break; // If someone cancels our thread... who knows!
					}
				}
			};
		};
		closureThread.start();
		
		return closureThread;
	}
	
	ShellListener shellListener = new ShellListener()
	{
		@Override
		public void shellClosed(ShellEvent arg0) 
		{
			if (canCloseNow())
			{
				// Just closing the shell.
				arg0.doit = true;
			}
			else
			{
				// If we can't close the shell immediately, starting the 
				// thread which will close it ASAP
				if (!closingPending) closeASAP();
				arg0.doit = false;
			}

			// Setting this variable to notify all the other threads
			closingPending = true;
		}
		
		@Override
		public void shellActivated(ShellEvent arg0) {}
		@Override
		public void shellIconified(ShellEvent arg0) {}
		@Override
		public void shellDeiconified(ShellEvent arg0) {}
		@Override
		public void shellDeactivated(ShellEvent arg0) {}
	};
	
	void startBitmapProcessing(IPreciseBitmap bitmap)
	{
		imageProcessor.startProcessingAsync(bitmap, new ImageProcessingReporter()
		{
			
			@Override
			public void reportResult(final IPreciseBitmap result)
			{
				callFromUi(new Runnable() 
				{
					@Override
					public void run() 
					{
						mainComposite.getPreciseBitmapViewComposite().getStatusBarComposite().setProgressBarVisibility(false);
						mainComposite.getPreciseBitmapViewComposite().getStatusBarComposite().setStatusText("Processing complete");
						mainComposite.getPreciseBitmapViewComposite().getPreciseBitmapView().setPreciseBitmap(result);
					}
				});
			}
			
			@Override
			public boolean reportProgress(final float progress)
			{
				if (!closingPending)
				{
					// Showing the current progress in the status bar
					if (callFromUi(new Runnable() 
						{
							@Override
							public void run() 
							{
								mainComposite.getPreciseBitmapViewComposite().getStatusBarComposite().setProgressBarVisibility(true);
								mainComposite.getPreciseBitmapViewComposite().getStatusBarComposite().setProgress((int)(progress * 100));
								mainComposite.getPreciseBitmapViewComposite().getStatusBarComposite().setStatusText("Processing image...");
							}
						}
					))
					{
						return true;
					}
				}
				
				return false;
			}
		});
	}
	
	ImageLoaderReporter imageLoaderReporter = new ImageLoaderReporter() 
	{
		@Override
		public void reportSuccess(final IPreciseBitmap preciseBitmap)
		{
			callFromUi(new Runnable() 
			{
				@Override
				public void run() 
				{
					mainComposite.getPreciseBitmapViewComposite().getPreciseBitmapView().setPreciseBitmap(preciseBitmap);
					mainComposite.setActiveScreen(ActiveScreen.View);
					
					startBitmapProcessing(preciseBitmap);
				}
				
			});
		}
		
		@Override
		public void reportException(final ImageLoaderException e)
		{
			callFromUi(new Runnable()
			{
				@Override
				public void run() 
				{
					showError("Can't open the file " + filename + ".\n" + e.getMessage());
				}
			});
		}

		@Override
		public void reportProgress(final int progress)
		{
			callFromUi(new Runnable() 
			{
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
	
	private void showError(String message)
	{
		MessageBox err = new MessageBox(ImageWindow.this, SWT.ERROR);
		err.setText("CatEye");
		err.setMessage(message);
		err.open();
		
		ImageWindow.this.close();		
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
	
	public ImageWindow() 
	{
		setText("CatEye");
		setSize(800, 600);
		setMinimumSize(320, 240);
		
		setLayout(new FillLayout());

		// Creating the main widget
		mainComposite = new ImageWindowStackComposite(this, SWT.NONE);
		
		// Adding listeners
		addShellListener(shellListener);
	}
	
	@Override
	protected void checkSubclass() 
	{
		// Disable the check that prevents subclassing of SWT components
	}
	


}
