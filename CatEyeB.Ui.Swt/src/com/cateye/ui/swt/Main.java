package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.cateye.core.jni.RawImageLoader;

public class Main 
{
	static MainWindow mainWindow;
	
	public static void main(String[] args)
	{
		RawImageLoader imageLoader = new RawImageLoader();
		mainWindow = new MainWindow(imageLoader);
		
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
			mainWindow.startWithFile(args[0]);
		}
		
		System.out.println("CatEye finished");
	}
}
