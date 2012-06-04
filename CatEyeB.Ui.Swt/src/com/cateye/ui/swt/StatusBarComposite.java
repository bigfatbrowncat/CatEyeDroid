package com.cateye.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class StatusBarComposite extends Composite 
{

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StatusBarComposite(Composite parent, int style) 
	{
		super(parent, style);
		FormLayout formLayout = new FormLayout();
		formLayout.marginTop = 3;
		formLayout.marginRight = 3;
		formLayout.marginLeft = 3;
		formLayout.marginBottom = 3;
		setLayout(formLayout);
		
		ProgressBar statusProgressBar = new ProgressBar(this, SWT.NONE);
		FormData fd_statusProgressBar = new FormData();
		fd_statusProgressBar.left = new FormAttachment(100, -179);
		fd_statusProgressBar.right = new FormAttachment(100);
		fd_statusProgressBar.bottom = new FormAttachment(100);
		fd_statusProgressBar.top = new FormAttachment(0);
		statusProgressBar.setLayoutData(fd_statusProgressBar);
		
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(statusProgressBar, 0, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(statusProgressBar, 0, SWT.TOP);
		fd_composite.right = new FormAttachment(statusProgressBar, -6);
		fd_composite.left = new FormAttachment(0);
		composite.setLayoutData(fd_composite);
		
		Label label = new Label(composite, SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		label.setText("Ready");
		label.setCapture(true);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
