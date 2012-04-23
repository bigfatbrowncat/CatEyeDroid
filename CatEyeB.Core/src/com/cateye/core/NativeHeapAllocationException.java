package com.cateye.core;

public class NativeHeapAllocationException extends RuntimeException {
	public NativeHeapAllocationException(String message)
	{
		super(message);
	}
	private static final long serialVersionUID = 1L;
}
