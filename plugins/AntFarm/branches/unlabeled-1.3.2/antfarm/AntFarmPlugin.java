/*
 *  AntFarmPlugin.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
 *  Modified for jEdit 4.0 by John Gellene (jgellene@nyc.rr.com)
 *
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
package antfarm;

import console.*;
import errorlist.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;
import plugin.integration.*;

import org.gjt.sp.util.Log;

import projectviewer.*;

public class AntFarmPlugin extends EditPlugin
{

	final static String NAME = "antfarm";
	final static String OPTION_PREFIX = "options." + NAME + ".";
	final static String ANT_HOME = "ant.home";
	final static AntFarmShell ANT_SHELL = new AntFarmShell();

	private static DefaultErrorSource _errorSource;


	public static DefaultErrorSource getErrorSource()
	{
		return _errorSource;
	}


	static Console getConsole( View view )
	{
		return getConsole( view, true );
	}


	static Console getConsole( View view, boolean bringToFront )
	{
		DockableWindowManager mgr = view.getDockableWindowManager();
		String CONSOLE = "console";
		// Get the current console instance
		Console console =
			(Console) mgr.getDockable( CONSOLE );
		if(console == null)
		{
			mgr.addDockableWindow( CONSOLE );
			console = (Console) mgr.getDockable( CONSOLE );
		}

		console.setShell( AntFarmPlugin.ANT_SHELL );

		if ( !bringToFront )
			view.getDockableWindowManager().getDockable( NAME );

		return console;
	}


	public void createMenuItems( Vector menuItems )
	{
		menuItems.addElement( GUIUtilities.loadMenuItem( "antfarm" ) );
	}


	public void createOptionPanes( OptionsDialog od )
	{
		od.addOptionPane( new AntFarmOptionPane() );
	}


	public void start()
	{
		IntegrationManager integration = new IntegrationManager( this );
		integration.addBridge( "projectviewer.ProjectPlugin", "antfarm.ProjectBridge" );

		// initalize error source
		_errorSource = new DefaultErrorSource( NAME );
		ErrorSource.registerErrorSource(_errorSource);
		// check whether tools.jar is available on JDK 1.2 or higher:
		if ( !MiscUtilities.isToolsJarAvailable() ) {
			Log.log( Log.WARNING, this,
				"This will cause problems when trying to use the modern and classic compilers.\n" +
				"If you want AntFarm to work properly, please make sure tools.jar is\n" +
				"in one of the above locations, and restart jEdit." );
		}
		// check for certain classes
/*		JARClassLoader loader = getJAR().getClassLoader();
		String className = "javax.xml.parsers.SAXParserFactory";
		try
		{
			loader.loadClass(className, true);
			className = "org.apache.xerces.jaxp.SAXParserFactoryImpl";
			loader.loadClass(className, true);
			Log.log(Log.DEBUG,this,"loaded XML parsing factory classes from xerces.jar");
		}
		catch(ClassNotFoundException e)
		{
			Log.log(Log.ERROR,this, "Could not find " + className);
			Log.log(Log.WARNING,this, "This will prevent AntFarm from parsing a build file.\n" +
				"If you want AntFarm to work properly, please make sure xerces.jar is\n" +
				"on the classpath you are using, and restart jEdit." );
			Log.log(Log.ERROR,this,e);
		}
*/
		Shell.registerShell( ANT_SHELL );

		System.getProperties().put( ANT_HOME, getAntFarmPath() );

		jEdit.propertiesChanged();
	}


	String getAntFarmPath()
	{
		EditPlugin.JAR jar = getJAR();
		String path = MiscUtilities.getParentOfPath( jar.getPath() );
		Log.log( Log.DEBUG, this, "path: " + path );
		return MiscUtilities.constructPath( path, "AntFarm" );
	}


	boolean useProjectBridge()
	{
		return jEdit.getBooleanProperty(
			AntFarmPlugin.OPTION_PREFIX + "use-project-bridge"
			 );
	}

}

