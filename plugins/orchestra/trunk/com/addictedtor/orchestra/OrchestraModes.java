package com.addictedtor.orchestra;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;

import org.af.commons.io.FileTransfer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;

/**
 * Utility class to deal with edit modes that are shipped 
 * with the orchestra plugin
 * 
 * @author Romain Francois <francoisromain@free.fr>
 */
public class OrchestraModes {

	private Vector<String> modes ;
	private String plugin_path ;
	File modes_dir ;
	
	public OrchestraModes(){
		plugin_path = OrchestraPlugin.getPluginHomePath() ;
		modes_dir = new File( plugin_path + "/modes" ) ;
		if( !modes_dir.exists() ){
			modes_dir.mkdirs() ;
		}
		initModes(); 
	}

	private void initModes(){
		modes = new Vector<String>() ;
		StringTokenizer tokens = new StringTokenizer( jEdit.getProperty("orchestra.rmodes") );
		while( tokens.hasMoreElements() ){
			modes.add( tokens.nextToken() ) ;
		}
	}
	
	/**
	 * Reads the orchestra.rmodes property, copy the modes from the orchestra.jar file
	 * into the plugin home path
	 * 
	 * @throws IOException when the file transfer does not work
	 */
	public void deployModes() throws IOException {
		
		for( String mode: modes){
			FileTransfer.copyResourceToLocalDir( 
					OrchestraPlugin.class.getResource("/modes/"+ mode + ".xml"),
					mode + ".xml", modes_dir );

		}
		FileTransfer.copyResourceToLocalDir( 
				OrchestraPlugin.class.getResource("/modes/catalog"), "catalog", modes_dir );

	}


	/**
	 * load the modes into jedit
	 */
	public void loadModes(){

		File catalog = new File(MiscUtilities.constructPath(plugin_path,"modes","catalog"));
		
		// we need to call: jEdit.loadModeCatalog(userCatalog.getPath(),false);
		// but this is private, so reflection is used
		
		Method[] methodz = jEdit.class.getDeclaredMethods() ;
		Method method = null ;
		for( int i=0; i<methodz.length; i++){
			method = methodz[i] ;
			if( "loadModeCatalog".equals(method.getName()) ){
				break ;
			}
		}
		
		boolean access = method.isAccessible() ;
		try{
			method.setAccessible( true ) ;
			method.invoke(null, new Object[]{ catalog.getAbsolutePath(), false } ) ;
		} catch( Exception e){
		} finally {
			method.setAccessible(access) ;
		}
		
		
	}

}
