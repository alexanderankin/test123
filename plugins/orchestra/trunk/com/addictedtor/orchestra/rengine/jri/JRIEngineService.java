package com.addictedtor.orchestra.rengine.jri;

import java.io.*  ;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.JRI.JRIEngine;

import com.addictedtor.orchestra.rengine.REngineService;
import com.addictedtor.orchestra.OrchestraPlugin ;

import org.gjt.sp.util.Log ;

public class JRIEngineService extends REngineService {

	private static final String R_HOME = getR_HOME() ;
	
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
		
		/* this only works if R_HOME/lib is in the LD_LIBRARY_PATH already */ 
		if( OrchestraPlugin.jeditStartedWithOrchestraScript() ){
			try{
				NativeLibraryHack.addLibPaths( getJriPath() ) ;
			} catch( IOException e){
				e.printStackTrace() ;
			}
		}
		
		JRIEngine eng = null ; 
		String[] args = { "--save" } ;
		try{
			eng = new JRIEngine( args ) ;
			eng.parseAndEval( "{ require( rJava ); .jinit() ; require( orchestra ) }" ) ;
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
		
		/* now load the plugin that is on the R package side */
		String orchestra_rpackage = null ;
		try{
			orchestra_rpackage = engine.parseAndEval( "system.file( package = 'orchestra' )" ).asString() ;
		} catch( Exception e){}
		
		String SEP = File.separator ;
		jEdit.addPluginJAR( orchestra_rpackage + SEP + "java" + SEP + "R.jar" ) ;
		
	}
	
	
	private static String getJriPath(){
		/* try the obvious first */
		String JRI = System.getenv( "JRI_PATH" ) ;
		if( JRI != null ){
			return JRI ; /* TODO: check it is valid */
		}
		
		/* try the obvious library first (less expensive) */
		JRI = getPackagePath("rJava") + "jri" ;
		if( (new File( JRI + "/JRI.jar" ) ).exists() ){
			return JRI ;
		}
		
		/* TODO : use RCmdBatch.getInstalledPackInfo(String pack) to query harder */
		return JRI ;
	}
	
  private static String getR_HOME(){
  	return "/usr/local/lib/R/" ;
  }
  
  /**
   * Returns the full path of an installed R package
   */
  private static String getPackagePath(String pack){
  	/* TODO : make it better, cache results, ask R if it is ready, etc ... */
  	return R_HOME + "library/" + pack + "/" ;
  }
  
}
