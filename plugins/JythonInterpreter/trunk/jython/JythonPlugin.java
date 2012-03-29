/*
 *  JythonPlugin.java - Jython plugin main class
 *  Copyright (C) 2001 Carlos Quiroz
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jython;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.Macros;

/**
 *  JythonInterpreter plugin class
 *
 * @author     Carlos Quiorz
 * @version    $Id: JythonPlugin.java,v 1.25 2004/01/07 21:46:34 tibu Exp $
 */
public class JythonPlugin extends EBPlugin {
	//private PythonParser parser = null;

	/**
	 * Starts the plugin
	 */
	public void start()
	{
		// Registers JythonHandler for Macro Handling
		Macros.registerHandler(new JythonHandler());

		System.setProperty("sun.awt.exception.handler", "jython.JythonExceptionHandler");

		//SideKickPlugin.registerParser(new PythonParser());
	}


	/**
	*	Handle EditBus messages
	*
	*	@param msg EditBus message
	*/
	public void handleMessage(EBMessage msg)
	{
		// HACK: trying to workaround bootstrapping problems
		//		 we can't call jEdit.getPlugin until after the
		//		 plugin has been started, as we need to get the
		//		 jar for execPlugin -- or it won't find the Python
		//		 file.
		/*if(msg instanceof EditorStarted)
		{
			long current = System.currentTimeMillis();
			JythonExecutor.execPlugin("jython.JythonPlugin",
				"jython", "PyParser", "__registerParser", null);
			System.err.println("PY it takes " + (System.currentTimeMillis()
			- current));
			parserRegistered = true;
		}*/
	}


	/**
	*	jEdit shutting down plugin
	*/
	public void end()
	{
		/*if (parserRegistered)
		{
			// unregister parser from SideKick
			JythonExecutor.execPlugin("jython.JythonPlugin",
					"jython", "PyParser", "__unregisterParser", null);
		}*/
	}

}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
