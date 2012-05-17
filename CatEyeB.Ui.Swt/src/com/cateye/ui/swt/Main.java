package com.cateye.ui.swt;

import org.eclipse.swt.widgets.Display;

import com.cateye.core.IPreciseBitmap;
import com.cateye.core.Image;
import com.cateye.core.jni.RawImageLoader;

public class Main 
{
	private static RawImageLoader imageLoader = new RawImageLoader();

	public static void main(String[] args)
	{
		if (args.length == 0)
			System.out.println("CatEye started without file");
		else
			System.out.println("CatEye started with " + args[0]);
			
		
		Display display = new Display();
		
		IPreciseBitmap pbmp = null;
		if (args.length > 0)
		{
			Image img = imageLoader.loadImageFromFile(args[0]);
			pbmp = img.getBitmap();
		}
		
		MainWindow mainWindow = new MainWindow(pbmp);
		
		mainWindow.start();
		
		display.dispose();
		
		System.out.println("CatEye finished");
	}
}
