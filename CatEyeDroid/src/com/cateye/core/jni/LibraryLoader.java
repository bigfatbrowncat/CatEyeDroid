package com.cateye.core.jni;

/**
 * Helper for library loading. It loads libraries from a directory specified in configuration.
 */
public class LibraryLoader
{
	static String getPathToLibrary(String libraryName)
	{
		return new java.io.File(libraryName).getAbsolutePath();
	}
	
	/**
	 * Loads a library and attach it to the native methods of the current class
	 */
	public static void attach(String libraryName)
	{
		System.load(getPathToLibrary(libraryName));
	}
}
