/*
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
package plugin.integration;

import jEditException;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

/**
 *  A manager for checking possible plugin to plugin integration.
 *
 *@author     steinbeck
 *@created    27. August 2001
 */
public class IntegrationManager
		 implements EBComponent
{

	private EditPlugin srcPlugin;
	private Map bridges;
	private List loadedBridges;
	private boolean listening;
	private boolean editorStarted;


	/**
	 *  Create a new <code>IntegrationManager</code>.
	 *
	 *@param  aSrcPlugin  The plugin which wants to communicate with another plugin
	 */
	public IntegrationManager(EditPlugin aSrcPlugin)
	{
		srcPlugin = aSrcPlugin;
		bridges = new HashMap(2);
		loadedBridges = new ArrayList(2);
		listening = false;
		editorStarted = false;
	}


	/**
	 *  Handles a message sent on the EditBus.
	 *
	 *@param  message  The message
	 */
	public void handleMessage(EBMessage message)
	{
		if (message instanceof EditorStarted)
		{
			checkBridges();
			editorStarted = true;
		}

		if (!editorStarted)
		{
			return;
		}

		if (message instanceof ViewUpdate)
		{
			ViewUpdate update = (ViewUpdate) message;
			if (update.getWhat() == ViewUpdate.CREATED)
			{
				enableBridges(update.getView());
			}
		}

		if (message instanceof CreateDockableWindow)
		{
			enableBridges(((CreateDockableWindow) message).getView());
		}
	}


	/**
	 *  Add a bridge. A bridge is an object that bridges the APIs of the source
	 *  plugin to the target plugin. The specified bridge will be ignored if the
	 *  target plugin does not exist.
	 *
	 *@param  targetPlugin  The feature to be added to the Bridge attribute
	 *@param  bridgeClass   The feature to be added to the Bridge attribute
	 */
	public void addBridge(String targetPlugin, String bridgeClass)
	{
		if (editorStarted && jEdit.getPlugin(targetPlugin) == null)
		{
			return;
		}
		bridges.put(targetPlugin, bridgeClass);
		listen();
	}


	/**
	 *  Returns <code>true</code> if the given key identifies a loaded bridge.
	 *
	 *@param  pluginKey  The key identifying the bridge
	 *@return            True, if the bridge is loaded
	 */
	private boolean isLoadedBridge(String pluginKey)
	{
		return loadedBridges.contains(pluginKey);
	}


	/**
	 *  Check bridges to see if the required plugin exists. If not, throw away the
	 *  bridge.
	 */
	private void checkBridges()
	{
		for (Iterator i = bridges.keySet().iterator(); i.hasNext(); )
		{
			if (jEdit.getPlugin((String) i.next()) == null)
			{
				i.remove();
			}
		}
		if (loadedBridges.size() == bridges.size())
		{
			ignore();
		}
	}


	/**
	 *  Check for possible bridge points, enabling the bridges if the target
	 *  plugins are present.
	 *
	 *@param  view  A view ?!
	 */
	private void enableBridges(View view)
	{
		for (Iterator i = bridges.keySet().iterator(); i.hasNext(); )
		{
			String each = (String) i.next();
			if (isLoadedBridge(each))
			{
				continue;
			}

			EditPlugin plugin = jEdit.getPlugin(each);

			try
			{
				if (plugin != null)
				{
					enableBridge(each, plugin, view);
				}
			}
			catch (jEditException e)
			{
				Log.log(Log.WARNING, this, e);
			}
		}
	}


	/**
	 *  Listen to the bus.
	 */
	private void listen()
	{
		if (!listening)
		{
			EditBus.addToBus(this);
		}
		listening = true;
		//Log.log( Log.DEBUG, this, "Listening to the bus..." );
	}


	/**
	 *  Ignore the bus.
	 */
	private void ignore()
	{
		EditBus.removeFromBus(this);
		//Log.log( Log.DEBUG, this, "Ignoring to the bus..." );
	}


	/**
	 *  Enable a bridge.
	 *
	 *@param  pluginKey           A key for the plugin
	 *@param  tgtPlugin           The target plugin
	 *@param  view                The view ?!
	 *@exception  jEditException  An exception if something goes wrong.
	 */
	private void enableBridge(String pluginKey, EditPlugin tgtPlugin, View view)
			 throws jEditException
	{
		PluginBridge bridge = loadBridge(pluginKey);
		if (bridge.enable(srcPlugin, tgtPlugin, view))
		{
			loadedBridges.add(pluginKey);
			//Log.log( Log.DEBUG, this, srcPlugin.getClass() + " to " + tgtPlugin.getClass() + " enabled" );
			if (loadedBridges.size() == bridges.size())
			{
				ignore();
			}
		}
	}


	/**
	 *  Load a bridge.
	 *
	 *@param  pluginKey           A plugin key
	 *@return                     The plugin bridge for the given key
	 *@exception  jEditException  An Exception if something goes wrong
	 */
	private PluginBridge loadBridge(String pluginKey)
			 throws jEditException
	{
		try
		{
			String bridgeClass = (String) bridges.get(pluginKey);
			return (PluginBridge) Class.forName(bridgeClass).newInstance();
		}
		catch (Throwable t)
		{
			throw new jEditException("Unable to load plugin bridge", t);
		}
	}

}

