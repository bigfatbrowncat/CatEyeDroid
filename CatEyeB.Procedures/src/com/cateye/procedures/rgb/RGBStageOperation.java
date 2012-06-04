package com.cateye.procedures.rgb;

public class RGBStageOperation
{
	private double r = 1;
	private double g = 1;
	private double b = 1;
	
	public double getR()
	{
		return r;
	}
	
	public void setR(double value)
	{
		if (value < 0.0d || value > 1000d)
		{
			//throw new ArgumentOutOfRangeException("r");
		}
		
		if (Math.abs(value - this.r) >= Double.MIN_NORMAL)
		{
			this.r = value;
			//fireOnPropertyChanged("r", value);
		}
	}

	public double getG()
	{
		return g;
	}

	public void setG(double value)
	{
		if (value < 0.0d || value > 1000d)
		{
			//throw new ArgumentOutOfRangeException("g");
		}
		
		if (Math.abs(value - this.g) >= Double.MIN_NORMAL)
		{
			this.g = value;
			//fireOnPropertyChanged("g", value);
		}
	}

	public double getB()
	{
		return b;
	}

	public void setB(double value)
	{
		if (value < 0.0d || value > 1000d)
		{
			//throw new ArgumentOutOfRangeException("b");
		}
		
		if (Math.abs(value - this.b) >= Double.MIN_NORMAL)
		{
			this.b = value;
			//fireOnPropertyChanged("b", value);
		}
	}
}
