package com.cateye.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.cateye.core.IPreciseBitmap;

public class PreciseBitmapViewComposite extends Composite
{
	private PreciseBitmapView preciseBitmapView;
	private StatusBarComposite statusBarComposite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreciseBitmapViewComposite(Composite parent, int style) 
	{
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		preciseBitmapView = new PreciseBitmapView(this);
		preciseBitmapView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		statusBarComposite = new StatusBarComposite(this, SWT.NONE);
		statusBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
	}

	public PreciseBitmapView getPreciseBitmapView()
	{
		return preciseBitmapView;
	}
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
