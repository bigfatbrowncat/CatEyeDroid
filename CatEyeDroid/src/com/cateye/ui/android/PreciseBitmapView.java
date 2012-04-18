package com.cateye.ui.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class PreciseBitmapView extends View
{
    public PreciseBitmapView(Context context)
    {
    	super(context);
    	// TODO Auto-generated constructor stub
    }

    public PreciseBitmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
private Bitmap mImage;
    private SurfaceHolder holder;
    private int format, width, height;

@Override
protected void onDraw(Canvas canvas) {
Paint pnt = new Paint();
pnt.setStyle(Paint.Style.FILL_AND_STROKE);
pnt.setARGB(128, 128, 128, 192);
canvas.drawLine(0, 0, this.getWidth(), getHeight(), pnt);
}

}