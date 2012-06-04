package com.cateye.procedures.hsb;

public class HSBStageOperation
{
	private double brightness = 1;
	private double hue = 0;
	private double saturation = 1;
	private double saturationCompressionPower = 5;
	
	public double getBrightness()
	{
		return brightness;
	}
	
	public void setBrightness(double value)
	{
		if (value < 0.01d || value > 100d)
		{
			//throw new ArgumentOutOfRangeException("brightness");
		}
		
		if (Math.abs(value - this.brightness) >= Double.MIN_NORMAL)
		{
			this.brightness = value;
			//fireOnPropertyChanged("brightness", value);
		}
	}

	public double getHue()
	{
		return hue;
	}

	public void setHue(double value)
	{
		if (value < -1d || value > 1d)
		{
			//throw new ArgumentOutOfRangeException("hue");
		}
		
		if (Math.abs(value - this.hue) >= Double.MIN_NORMAL)
		{
			this.hue = value;
			//fireOnPropertyChanged("hue", value);
		}
	}

	public double getSaturation()
	{
		return saturation;
	}

	public void setSaturation(double value)
	{
		if (value < 0d || value > 100d)
		{
			//throw new ArgumentOutOfRangeException("saturation");
		}
		
		if (Math.abs(value - this.saturation) >= Double.MIN_NORMAL)
		{
			this.saturation = value;
			//fireOnPropertyChanged("saturation", value);
		}
	}

	public double getSaturationCompressionPower()
	{
		return saturationCompressionPower;
	}

	public void setSaturationCompressionPower(double value)
	{
		if (value < 0d || value > 100d)
		{
			//throw new ArgumentOutOfRangeException("saturationCompressionPower");
		}
		
		if (Math.abs(value - this.saturationCompressionPower) >= Double.MIN_NORMAL)
		{
			this.saturationCompressionPower = value;
			//fireOnPropertyChanged("saturationCompressionPower", value);
		}	
	}
}
