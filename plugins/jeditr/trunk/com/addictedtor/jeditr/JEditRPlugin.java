package com.addictedtor.jeditr ;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.jedit.bsh.UtilEvalError;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;

/**
 * Main class of the jeditr plugin
 * 
 * @author Romain Francois <francoisromain@free.fr>
 * 
 */
public class JEditRPlugin extends EBPlugin {

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
	}

	/**
	 * Stop method of the plugin. Placeholder at the moment.
	 */
	@Override
	public void stop() {
	}

}

