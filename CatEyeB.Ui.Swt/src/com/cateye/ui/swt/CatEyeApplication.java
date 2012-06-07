package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.cateye.ui.ImagesRegistry;

public class CatEyeApplication 
{
	private static CatEyeApplication application = new CatEyeApplication();
	
	/**
	 * <code>CatEyeApplication</code> class is a singleton. It
	 * should mimic the same-named class from Android version. 
	 * @return The one and only <code>CatEyeApplication</code> object.
	 */
	public static CatEyeApplication getInstance() { return application; }
	
	private ImagesRegistry registry = new ImagesRegistry();
	
	public ImagesRegistry getRegistry()
	{
		return registry;
	}
	
	
	public static void main(String[] args)
	{
		// Entry point. Creating the main window
		PreciseBitmapViewWindow mainWindow = new PreciseBitmapViewWindow();
		
		if (args.length == 0)
		{
			System.out.println("CatEye started without a file");
			
			// Showing error
			MessageBox noFileMessageBox = new MessageBox(mainWindow, SWT.ICON_WARNING);
			noFileMessageBox.setText("CatEye");
			noFileMessageBox.setMessage("No filename specified in the command line. CatEye should be started with an image file.\nTo open an image you can drag and drop it to the CatEye icon.");
			
			noFileMessageBox.open();
		}
		else
		{
			System.out.println("CatEye started with file " + args[0]);
			mainWindow.openWithFile(args[0]);
		}

		/*
		if (getInstance().getRegistry().getState() == State.Working)
		{
			System.out.println("Registry is busy now. Let's wait for it...");
			while (getInstance().getRegistry().getState() == State.Working)
			{
				try {
					Thread.sleep(100);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
					break;	// If someone tries to finish us immediately, let him do it
				}
			}
		}*/
		
		System.out.println("CatEye finished");
	}
}
