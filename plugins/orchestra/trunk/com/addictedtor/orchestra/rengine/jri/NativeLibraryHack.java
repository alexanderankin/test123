package com.addictedtor.orchestra.rengine.jri;

import java.io.IOException; 
import java.util.Vector ;
import java.lang.reflect.Field ;
import java.lang.reflect.Method ;

/**
 * Hack allowing to append paths to java.library.path at runtime
 */
public class NativeLibraryHack {

	/**
	 * Returns the current set of paths used by the JVM to 
	 * lookup for native libraries
	 */
	public static String[] getLibPaths( ) {
		String[] paths = null ;
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			boolean access = field.isAccessible() ;
			field.setAccessible(true);
			try{ paths = (String[])field.get(null); } finally{  field.setAccessible(access); }
		} catch( Exception e){
			e.printStackTrace() ;
		} 
		return paths ;
	}
	
	/**
	 * Adds paths to the set of paths used by the JVM to perform 
	 * native library lookup. 
	 *
	 * @param newlibs new library paths
	 */
	public static void addLibPaths(String... newlibs ) throws IOException {
		
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			boolean access = field.isAccessible() ;
			field.setAccessible(true);
			String[] paths = (String[])field.get(null);
			
			Vector<String> vec = new Vector<String>() ;
			StringBuffer buf = new StringBuffer() ;
			int count = 0;
			for( int i=0; i<paths.length; i++){
				vec.add( paths[i] ) ;
				if( count != 0) buf.append( ":" ) ; 
				buf.append( paths[i ] ) ;
				count++ ;
			}
			boolean changeNeeded = false ;
			
			for( String lib : newlibs ){
				if( !vec.contains( lib ) ){
					changeNeeded = true ;
					vec.add( lib ) ;
					if( count != 0 ) buf.append(":") ;
					buf.append( lib ) ;
				}
			}
			if( !changeNeeded ){
				return ;
			}
			String[] tmp = new String[vec.size()] ;
			vec.toArray( tmp ) ;
			field.set(null,tmp);
			field.setAccessible(access);
			
			/* reflect the change in the java.library.path system property */
			System.setProperty( "java.library.path" , buf.toString() ) ;
			
			
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}

}

