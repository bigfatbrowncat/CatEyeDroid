package com.cateye.core;

public class PointD 
{
	private double x;
	private double y;
	
	public PointD(double x, double y)
	{
		this.x = x; this.y = y;
	}
	public double getX() 
	{
		return x;
	}
	public double getY() 
	{
		return y;
	}
	
	public static PointD multiply(PointD point, double multiplicator)
	{
		return new PointD(point.x * multiplicator, point.y * multiplicator);
	}
	
	@Override
	public String toString() 
	{
		return "(" + x + ", " + y + ")";
	}
}
