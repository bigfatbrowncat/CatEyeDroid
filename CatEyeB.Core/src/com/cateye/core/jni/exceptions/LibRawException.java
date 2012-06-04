package com.cateye.core.jni.exceptions;

import com.cateye.core.exceptions.ImageLoaderException;

// TODO [Plan] There should be no error code constants in public
// we should have a special exception for each error code.

// When error has occured during processing with the libraw library, it should call
// CreateSpecificException to obtain an object of the correct exception
// class and then throw it with env->Throw()

public class LibRawException extends ImageLoaderException
{
	public static final int UNSPECIFIED_ERROR = -1;
	public static final int FILE_UNSUPPORTED = -2;
	public static final int REQUEST_FOR_NONEXISTENT_IMAGE = -3;
	public static final int OUT_OF_ORDER_CALL = -4;
	public static final int NO_THUMBNAIL = -5;
	public static final int UNSUPPORTED_THUMBNAIL = -6;
	public static final int UNSUFFICIENT_MEMORY = -100007;
	public static final int DATA_ERROR = -100008;
	public static final int IO_ERROR = -100009;
	public static final int CANCELLED_BY_CALLBACK = -100010;
	public static final int BAD_CROP = -100011;

	public static String messageFromCode(int errorCode) 
	{
		switch (errorCode)
		{
		case UNSPECIFIED_ERROR:
			return "Unspecified error";
			
		case FILE_UNSUPPORTED:
			return "File type is unsupported";
			
		case REQUEST_FOR_NONEXISTENT_IMAGE:
			return "Image does not exist";
			
		case OUT_OF_ORDER_CALL:
			return "Procedure call out of order";
			
		case NO_THUMBNAIL:
			return "No thumbnail present";
			
		case UNSUPPORTED_THUMBNAIL:
			return "Thumbnail type is unsupported";
			
		case UNSUFFICIENT_MEMORY:
			return "Insufficient memory";
			
		case DATA_ERROR:
			return "Data error";
			
		case IO_ERROR:
			return "Input/output error";
			
		case CANCELLED_BY_CALLBACK:
			return "Processing cancelled by a callback";
			
		case BAD_CROP:
			return "Bad crop";
			
		default:
			return "Unknown error";
		}
	}

	private int errorCode;

	protected LibRawException(int errorCode) 
	{
		super(messageFromCode(errorCode));
		this.errorCode = errorCode;
	}

	public int getErrorCode() 
	{
		return errorCode;
	}
	
	// This method should be called only from JNI,
	// so it is marked "private".
	@SuppressWarnings("unused")
	public static LibRawException createSpecificException(int errorCode)
	{
		// TODO Make an exception for each error code
		return new LibRawException(errorCode);
	}
}
