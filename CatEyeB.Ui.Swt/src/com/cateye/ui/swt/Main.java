package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import tools.Utf8;

public class Main 
{
	public static void main(String[] args)
	{
		MainWindow mainWindow = new MainWindow();
		
		if (args.length == 0)
		{
			System.out.println("CatEye started without a file");
			
			// Showing error
			MessageBox noFileMessageBox = new MessageBox(mainWindow, SWT.ICON_WARNING);
			noFileMessageBox.setText("CatEye");
			noFileMessageBox.setMessage("No filename specified in the command line. CatEye should be started with an image file.");
			
			noFileMessageBox.open();
		}
		else
		{
			System.out.println("CatEye started with file " + args[0]);
			
			String filename = Utf8.fromUtf8(args[0]);
			mainWindow.startWithFile(filename);
		}
		
		System.out.println("CatEye finished");
	}
}
