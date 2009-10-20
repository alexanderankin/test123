package com.addictedtor.orchestra.rengine.jri;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.JRI.JRIEngine;

import com.addictedtor.orchestra.rengine.REngineService;

public class JRIEngineService extends REngineService {

	@Override
	public REngine getEngine() {
		if( engine == null ){
			initEngine() ;
		} 
		return engine ;
	}

	public JRIEngineService(){
		/* nothing so far */ 
	}
	
	/* cached */
	private static JRIEngine engine = null ;
	
	@SuppressWarnings("unchecked")
	private static void initEngine(){
		
		/* so that System.load("jri") failing does not cause System.exit(1), see Rengine */
		System.setProperty("jri.ignore.ule", "yes" ) ; 		
	
		/* TODO: ask these to R using RCmdBatch */
		try{
			String[] libs = new String[]{
				"/usr/local/lib/R/lib",
				"/usr/local/lib/R/bin", 
				"/usr/local/lib/R/library/rJava/libs", 
				"/usr/local/lib/R/library/rJava/jri"
			};
			addLibPaths( libs ) ;
			
		} catch( Exception e){ 
			e.printStackTrace() ;
		}
		
		System.loadLibrary( "R" ) ;
		
		/* use reflection to load the RJavaClassLoader */
		Class RJCL = null ; 
		Constructor<?> cons = null ; 
		ClassLoader rjcl = null ;
		try{
			/* the RJavaClassLoader class */
			RJCL =  Class.forName("RJavaClassLoader" ) ;
			
			Class<?>[] clazzes = new Class<?>[]{ String.class, String.class } ;
			cons = 	RJCL.getConstructor( clazzes ) ; 
			
			rjcl = (ClassLoader) cons.newInstance( 
					"/usr/local/lib/R/library/rJava" , 
					"/usr/local/lib/R/library/rJava/libs" ) ;
			
			/* use the rjcl to load the jri library without relying on java.library.path */
			
			Method findlib = RJCL.getDeclaredMethod( "findLibrary", new Class<?>[]{ String.class } ) ;
			boolean access = findlib.isAccessible() ;
			findlib.setAccessible(true ) ;

			String lib = (String) findlib.invoke( rjcl, new Object[]{ "rJava" } ) ;
			System.load( lib ) ;
			
			/* restore accessibility */
			findlib.setAccessible( access ) ;
			
		} catch( Exception e){
			e.printStackTrace() ;
		}
		
		/* otherwise JRIEngine won't load */
		Rengine.jriLoaded = true ;
		
		JRIEngine eng = null ; 
		String[] args = { "--save" } ;
		try{
			eng = new JRIEngine( args ) ;
			eng.parseAndEval( "{ require( rJava ); .jinit() }" ) ;
		} catch( Exception e){
			e.printStackTrace() ;
		}
		
		Rengine rni = null ; 
		try{
			rni = eng.getRni() ;
			rni.setName( "R engine" ) ;
		} catch( NullPointerException e){
			System.out.println( "could not get the Rengine" ) ;
		}
		
		engine = eng ; 
		
		String orchestra_rpackage = null ;
		try{
			orchestra_rpackage = engine.parseAndEval( "system.file( package = 'orchestra' )" ).asString() ;
		} catch( Exception e){}
		
		String SEP = File.separator ;
		jEdit.addPluginJAR( orchestra_rpackage + SEP + "java" + SEP + "R.jar" ) ;
		
	}
	
	public static void addLibPaths(String[] newlibs ) throws IOException {
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
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
			
			System.setProperty( "java.library.path" , buf.toString() ) ;
			
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}

	
}
