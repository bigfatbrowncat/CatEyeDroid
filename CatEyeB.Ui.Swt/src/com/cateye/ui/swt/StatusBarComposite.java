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
	private Label statusTextLabel;
	private ProgressBar progressBar;

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
		
		progressBar = new ProgressBar(this, SWT.NONE);
		FormData fd_ProgressBar = new FormData();
		fd_ProgressBar.left = new FormAttachment(100, -179);
		fd_ProgressBar.right = new FormAttachment(100);
		fd_ProgressBar.bottom = new FormAttachment(100);
		fd_ProgressBar.top = new FormAttachment(0);
		progressBar.setLayoutData(fd_ProgressBar);
		
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(progressBar, 0, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(progressBar, 0, SWT.TOP);
		fd_composite.right = new FormAttachment(progressBar, -6);
		fd_composite.left = new FormAttachment(0);
		composite.setLayoutData(fd_composite);
		
		statusTextLabel = new Label(composite, SWT.HORIZONTAL);
		statusTextLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		statusTextLabel.setText("Ready");
		statusTextLabel.setCapture(true);

	}
	
	public void setStatusText(String value)
	{
		statusTextLabel.setText(value);
		//this.layout();
	}
	
	public void setProgress(int value)
	{
		progressBar.setSelection(value);
	}
	
	public void setProgressBarVisibility(boolean value)
	{
		progressBar.setVisible(value);
		//this.layout();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
