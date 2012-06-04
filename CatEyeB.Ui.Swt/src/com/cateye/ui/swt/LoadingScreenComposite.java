package com.cateye.ui.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class LoadingScreenComposite extends Composite 
{
	Label lblFileName;
	ProgressBar progressBar;
	private Composite composite_client;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LoadingScreenComposite(Composite parent, int style) 
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		composite_client = new Composite(this, SWT.BORDER);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		composite_client.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		FillLayout fl_composite_client = new FillLayout(SWT.VERTICAL);
		fl_composite_client.marginHeight = 5;
		fl_composite_client.marginWidth = 5;
		composite_client.setLayout(fl_composite_client);
		
		Label lblLoadingImage = new Label(composite_client, SWT.NONE);
		lblLoadingImage.setAlignment(SWT.CENTER);
		lblLoadingImage.setText("   Loading image...");
		
		lblFileName = new Label(composite_client, SWT.WRAP);
		lblFileName.setText("<file name>");
		lblFileName.setAlignment(SWT.CENTER);
		
		// Setting the font to bold
		Font fnt = lblFileName.getFont();
		FontData fd = fnt.getFontData()[0];
		fd.setStyle(fd.getStyle() | SWT.BOLD);
		lblFileName.setFont(new Font(getDisplay(), fd));
		
		progressBar = new ProgressBar(composite_client, SWT.NONE);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
	}

	public void setFilename(String filename)
	{
		lblFileName.setText(filename);
	}
	
	public void setProgress(int percent)
	{
		progressBar.setSelection(percent);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
