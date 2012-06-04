package com.cateye.procedures.downsample;

public class DownsampleStageOperation 
{
	private int rate;
	
	public double getRate()
	{
		return rate;
	}
	
	public void setRate(int value)
	{
		if (value < 2 || value > 10)
		{
			//throw new ArgumentOutOfRangeException("rate");
		}
		
		if (value != this.rate)
		{
			this.rate = value;
			//fireOnPropertyChanged("rate", value);
		}
	}

}
