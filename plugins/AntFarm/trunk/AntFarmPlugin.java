/*
 *  AntFarmPlugin.java - Ant build utility plugin for jEdit
 *  Copyright (C) 2000 Chris Scott
 *  Other contributors: Rick Gibbs
 *
 *  The Apache Software License, Version 1.1
 *
 *  Copyright (c) 1999, 2000 The Apache Software Foundation.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution, if
 *  any, must include the following acknowlegement:
 *  "This product includes software developed by the
 *  Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowlegement may appear in the software itself,
 *  if and wherever such third-party acknowlegements normally appear.
 *
 *  4. The names "The Jakarta Project", "Ant", and "Apache Software
 *  Foundation" must not be used to endorse or promote products derived
 *  from this software without prior written permission. For written
 *  permission, please contact apache@apache.org.
 *
 *  5. Products derived from this software may not be called "Apache"
 *  nor may "Apache" appear in their names without prior written
 *  permission of the Apache Group.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of the Apache Software Foundation.  For more
 *  information on the Apache Software Foundation, please see
 *  <http://www.apache.org/>.
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Component;

import java.util.ArrayList;
import java.util.Vector;

import java.io.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import plugin.integration.*;

/**
 *  The 'Plugin' class is the interface between jEdit and the plugin. Plugins
 *  can either extend EditPlugin or EBPlugin. EBPlugins have the additional
 *  property that they receive EditBus messages.
 *
 *@author     steinbeck
 *@created    27. August 2001
 */

public class AntFarmPlugin extends EBPlugin
{
	private AntBridge bridge;
	private AntFarm antFarm;
	private View theView;
  private boolean foundToolsJar = true;

	/**
	 *  The 'name' of our dockable window.
	 */
	public final static String NAME = "antfarm";

	// save System.out and err in case we need them
	static PrintStream out = System.out;
	static PrintStream err = System.err;

	private static DefaultErrorSource errorSource;


	/**
	 *  Method called by jEdit before exiting. Usually, nothing needs to be done
	 *  here.
	 *
	 *@return    The AntBridge value
	 */
	//public void stop() {}

	/**
	 *  Method called by jEdit before exiting. Usually, nothing needs to be done
	 *  here.
	 *
	 *  Returns an {@link AntBridge}.
	 *
	 *@return    The AntBridge value
	 */
	public AntBridge getAntBridge()
	{
		if (bridge == null)
		{
			bridge = loadAntBridge();
		}
		return bridge;
	}


	/**
	 *  Returns an {@link AntBridge}.
	 *
	 *@return    The AntFarm value
	 */
	public AntFarm getAntFarm()
	{
		return antFarm;
	}


	/**
	 *  Method called by jEdit to initialize the plugin.
	 */
	public void start()
	{
		loadToolsJAR();

		IntegrationManager integration = new IntegrationManager(this);
		integration.addBridge("projectviewer.ProjectPlugin", "ProjectBridge");

		// add our own ErrorSource to the list of error sources
		errorSource = new DefaultErrorSource("antfarm");
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST, errorSource);
		EditBus.addToBus(errorSource);

		// add our dockable to the dockables 'named list'
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, NAME);
	}


	/**
	 *  Method called every time a view is created to set up the Plugins menu.
	 *  Menus and menu items should be loaded using the methods in the GUIUtilities
	 *  class, and added to the list.
	 *
	 *@param  menuItems  Add the menu item here
	 */
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenuItem("antfarm"));
	}


	/**
	 *  Method called every time the plugin options dialog box is displayed. Any
	 *  option panes created by the plugin should be added here.
	 *
	 *@param  message        Description of Parameter
	 *@see                   OptionPane
	 *@see                   OptionsDialog#addOptionPane(OptionPane)
	 */
	//public void createOptionPanes(OptionsDialog optionsDialog) {}

	/**
	 *  Method called every time the plugin options dialog box is displayed. Any
	 *  option panes created by the plugin should be added here.
	 *
	 *  Handles a message sent on the EditBus. The default implementation ignores
	 *  the message.
	 *
	 *@param  message        Description of Parameter
	 */
	public void handleMessage(EBMessage message)
	{
		/*
		 *  upon receiving a CreateDockableWindow, we check if
		 *  the name of the requested window is 'hello-dockable',
		 *  and create it if it is so.
		 */
		if (message instanceof CreateDockableWindow)
		{
			CreateDockableWindow cmsg = (CreateDockableWindow) message;
			theView = cmsg.getView();

			if (cmsg.getDockableWindowName().equals(NAME))
			{
				antFarm = new AntFarm(this, cmsg.getView());
				cmsg.setDockableWindow(antFarm);
			}
		}
	}


	/**
	 *  Handle all of ANT's build messages including System.out and System.err Each
	 *  build message filename, line no, column, of the error that it is reporting
	 *  if it in fact is an Error. You can use this info some how to publish an
	 *  event to the Error List TODO: handle System.err messages with a different
	 *  color.
	 *
	 *@param  antFarm  Description of Parameter
	 *@param  message  Description of Parameter
	 */
	void handleBuildMessage(AntFarm antFarm, BuildMessage message)
	{
		handleBuildMessage(antFarm, message, null);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  antFarm    Description of Parameter
	 *@param  message    Description of Parameter
	 *@param  lineColor  Description of Parameter
	 */
	void handleBuildMessage(AntFarm antFarm, BuildMessage message, Color lineColor)
	{
		if (message.isError())
		{
			// publish this message to the ErrorList
			addError(ErrorSource.ERROR, message.getAbsoluteFilename(),
					message.getLine(), message.getColumn(),
					message.toString());
			//message.getMessage() );
			// "error-list" should be ErrorListPlugin.NAME but the
			// jar could be in a couple of different places...
			// theView.getDockableWindowManager().addDockableWindow("error-list");
		}
		else if (message.isWarning())
		{
			// publish this message to the ErrorList
			addError(ErrorSource.WARNING, message.getAbsoluteFilename(),
					message.getLine(), message.getColumn(),
					message.toString());
			//message.getMessage() );
			// "error-list" should be ErrorListPlugin.NAME but the
			// jar could be in a couple of different places...
			// theView.getDockableWindowManager().addDockableWindow("error-list");
		}
		else
		{
			// publish this message to the buildResults text area
			//out.println("[MESSAGE]: " + message.toString() );
			if (lineColor != null)
			{
				antFarm.appendToTextArea(message.toString(), lineColor);
			}
			else
			{
				antFarm.appendToTextArea(message.toString());
			}
		}
	}


	/**
	 *  Adds a feature to the Error attribute of the AntFarmPlugin object
	 *
	 *@param  type     The feature to be added to the Error attribute
	 *@param  file     The feature to be added to the Error attribute
	 *@param  line     The feature to be added to the Error attribute
	 *@param  column   The feature to be added to the Error attribute
	 *@param  message  The feature to be added to the Error attribute
	 */
	void addError(int type, String file, int line, int column, String message)
	{
		errorSource.addError(type, file, line - 1, 0, 0, message);
	}


	/**
	 *  Description of the Method
	 */
	void clearErrors()
	{
		errorSource.clear();
	}


	/**
	 *  Load an {@link AntBridge}.
	 *
	 *@return    Description of the Returned Value
	 */
	private AntBridge loadAntBridge()
	{
		//ClassLoader cl = createClassLoader(new File( jEdit.getProperty( "ant.home" ), "lib" ) );
		try
		{
			AntBridge bridge = (AntBridge) new SimpleAntBridge();
			bridge.setPlugin(this);
			return bridge;
		}
		catch (Exception e)
		{
			Log.log(Log.WARNING, this, e);
			return null;
		}
	}


	/**
	 * If on JDK 1.2 or higher, make sure tools.jar is added to the
	 * CLASSPATH.
	 */
	public void loadToolsJAR()
	{
		// Used to store the search paths
		ArrayList paths = new ArrayList();

		String javaVersion = System.getProperty("java.version");
		if (MiscUtilities.compareVersions(javaVersion, "1.2") < 0)
		{
			return;
		}

		Log.log(Log.DEBUG, this, "JDK 1.2 or higher detected, searching for " +
			"tools.jar...");

		String toolsPath = System.getProperty("java.home");

		if (toolsPath.toLowerCase().endsWith(File.separator + "jre"))
		{
			toolsPath = toolsPath.substring(0, toolsPath.length() - 4);
		}

		toolsPath = MiscUtilities.constructPath(toolsPath, "lib", "tools.jar");
		paths.add(toolsPath);

		if (!(new File(toolsPath).exists()))
		{
			toolsPath = MiscUtilities.constructPath(toolsPath.substring(0, 3),
				"jdk" + System.getProperty("java.version"), "lib");
			toolsPath = MiscUtilities.constructPath(toolsPath, "tools.jar");
			paths.add(toolsPath);
		}

		if (!(new File(toolsPath).exists()))
		{
			Log.log(Log.WARNING, this, "Could not find tools.jar. Searched in the " +
				"following locations: \n" + "\t" + paths + "\n" +
				"This will cause problems when trying to use the modern and classic " +
				"compilers.\n" +
				"If you want AntFarm to work properly, please make sure tools.jar is\n"+
				"in one of the above locations, and restart jEdit.");
			return;
		}

		EditPlugin.JAR jar = jEdit.getPluginJAR(toolsPath);
		if (jar == null)
		{
			Log.log(Log.DEBUG, this, "Adding " + toolsPath + " to jEdit plugins.");
			try
			{
				jEdit.addPluginJAR(new EditPlugin.JAR(toolsPath,
																							new JARClassLoader(toolsPath)));
			}
			catch (IOException ioex)
			{
				Log.log(Log.ERROR, this, "Could not add tools.jar to jEdit plugins, " +
					"reason follows...");
				Log.log(Log.ERROR, this, ioex);
			}
		}
	}

}

