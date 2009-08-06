package com.addictedtor.jeditr ;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;

/**
 * Main class of the jeditr plugin
 * 
 * @author Romain Francois <francoisromain@free.fr>
 * 
 */
public class JEditRPlugin extends EBPlugin {

	public static final String NAME = "JEditRPlugin";
	
	/**
	 * Prefix for the menu of the plugin
	 */
	public static final String MENU = "jeditr.menu";

	/**
	 * Prefix for the options
	 */
	public static final String OPTION_PREFIX = "options.jeditr.";

	/**
	 * Start method of the plugin. This is where the magic takes place
	 */
	@Override
	public void start() {
		
		String jeditr_home = System.getProperty( "jeditr.home", "" ) ;
		if( jeditr_home.equals("") ){
			// do the installer stuff
		} else {
			// load the jeditr plugin from the R package tree
			jEdit.addPluginJAR( jeditr_home + "/java/R.jar" ) ; 
		}
		
	}

	/**
	 * Stop method of the plugin. Placeholder at the moment.
	 */
	@Override
	public void stop() {
	}

	public void handleMessage( EBMessage message){
		// placeholder
	}
		
	
}

