package com.cateye.ui.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main 
{
	public static void main(String[] args)
	{
		System.out.println("CatEye started");
		
		Display display = new Display();
		
		Shell mainShell = new Shell(display);
		mainShell.setText("CatEye");
		mainShell.setSize(640, 480);
		
		mainShell.open();
		
		while (!mainShell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		
		display.dispose();
		
		System.out.println("CatEye finished");
	}
}
