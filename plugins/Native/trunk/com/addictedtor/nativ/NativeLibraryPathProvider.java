package com.addictedtor.nativ ;

/**
 * native library path provider. This is a jedit service
 * plugins can implement.
 */
public abstract class NativeLibraryPathProvider {
	
	/**
	 * Returns the path of the native library
	 */
	public abstract String[] getNativeLibraries( ) ;
	
}

