package com.cateye.procedures.compressor;

public class CompressorStageOperation
{
	private double curve = 0.7;
	private double noiseGate = 0.1;
	private double pressure = 0.4;
	private double contrast = 0.85;
	
	public double getCurve()
	{
		return curve;
	}
	
	public void setCurve(double value)
	{
		if (value < 0d || value > 1d)
		{
			//throw new Exception("curve");
		}
		
		if (Math.abs(value - this.curve) >= Double.MIN_NORMAL)
		{
			this.curve = value;
		}
	}

	public double getNoiseGate()
	{
		return noiseGate;
	}
	
	public void setNoiseGate(double value)
	{
		if (value < 0.0d || value > 1d)
		{
			//throw new ArgumentOutOfRangeException("noiseGate");
		}
		
		if (Math.abs(value - this.noiseGate) >= Double.MIN_NORMAL)
		{
			this.noiseGate = value;
		}
	}
	
	public double getPressure()
	{
		return pressure;
	}
	
	public void setPressure(double value)
	{
		if (value < 0.0d || value > 1d)
		{
			//throw new ArgumentOutOfRangeException("pressure");
		}
		
		if (Math.abs(value - this.pressure) >= Double.MIN_NORMAL)
		{
			this.pressure = value;
			//fireOnPropertyChanged("pressure", value);
		}
	}
	
	public double getContrast()
	{
		return contrast;
	}
	
	public void setContrast(double value)
	{
		if (value < 0.00d || value > 1d)
		{
			//throw new ArgumentOutOfRangeException("contrast");
		}
		
		if (Math.abs(value - this.contrast) >= Double.MIN_NORMAL)
		{
			this.contrast = value;
			//fireOnPropertyChanged("contrast", value);
		}
	}
}
