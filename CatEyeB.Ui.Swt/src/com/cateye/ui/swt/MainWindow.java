package com.cateye.ui.swt;

import java.awt.Composite;

import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cateye.core.IPreciseBitmap;

public class MainWindow
{
	private Shell shell;
	private FillLayout mainLayout;
	private PreciseBitmapView mainView;
	
	private void prepareContent(IPreciseBitmap pbmp)
	{
		shell.setText("CatEye");
		shell.setSize(640, 480);
		
		mainLayout = new FillLayout();
		shell.setLayout(mainLayout);
		
		mainView = new PreciseBitmapView(shell);
		
		mainView.setPreciseBitmap(pbmp);
	}
	
	public void start()
	{
		shell.open();
		
		while (!shell.isDisposed())
		{
			if (!shell.getDisplay().readAndDispatch())
			{
				shell.getDisplay().sleep();
			}
		}
	}
	
	public MainWindow(IPreciseBitmap pbmp) 
	{
		shell = new Shell();
		prepareContent(pbmp);
	}

	public MainWindow(Display arg0, int arg1, IPreciseBitmap pbmp) 
	{
		shell = new Shell(arg0, arg1);
		prepareContent(pbmp);
	}

	public MainWindow(Display arg0, IPreciseBitmap pbmp) 
	{
		shell = new Shell(arg0);
		prepareContent(pbmp);
	}

	public MainWindow(int arg0, IPreciseBitmap pbmp) 
	{
		shell = new Shell(arg0);
		prepareContent(pbmp);
	}

	public MainWindow(Shell arg0, int arg1, IPreciseBitmap pbmp) 
	{
		shell = new Shell(arg0, arg1);
		prepareContent(pbmp);
	}

	public MainWindow(Shell arg0, IPreciseBitmap pbmp)
	{
		shell = new Shell(arg0);
		prepareContent(pbmp);
	}
	
	
}
