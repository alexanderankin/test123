/*
 *  IntegrationManager.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
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
package plugin.integration;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

/**
 *  A manager for checking possible plugin to plugin integration.
 *
 * @author     steinbeck
 * @created    27. August 2001
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
	 * @param  aSrcPlugin  The plugin which wants to communicate with another plugin
	 */
	public IntegrationManager( EditPlugin aSrcPlugin )
	{
		srcPlugin = aSrcPlugin;
		bridges = new HashMap( 2 );
		loadedBridges = new ArrayList( 2 );
		listening = false;
		editorStarted = false;
	}


	/**
	 *  Handles a message sent on the EditBus.
	 *
	 * @param  message  The message
	 */
	public void handleMessage( EBMessage message )
	{
		if ( message instanceof EditorStarted ) {
			checkBridges();
			editorStarted = true;
		}

		if ( !editorStarted ) {
			return;
		}

		if ( message instanceof ViewUpdate ) {
			ViewUpdate update = (ViewUpdate) message;
			if ( update.getWhat() == ViewUpdate.CREATED ) {
				enableBridges( update.getView() );
			}
		}

		if ( message instanceof CreateDockableWindow ) {
			enableBridges( ( (CreateDockableWindow) message ).getView() );
		}
	}


	/**
	 *  Add a bridge. A bridge is an object that bridges the APIs of the source
	 *  plugin to the target plugin. The specified bridge will be ignored if the
	 *  target plugin does not exist.
	 *
	 * @param  targetPlugin  The feature to be added to the Bridge attribute
	 * @param  bridgeClass   The feature to be added to the Bridge attribute
	 */
	public void addBridge( String targetPlugin, String bridgeClass )
	{
		if ( editorStarted && jEdit.getPlugin( targetPlugin ) == null ) {
			return;
		}
		bridges.put( targetPlugin, bridgeClass );
		listen();
	}


	/**
	 *  Returns <code>true</code> if the given key identifies a loaded bridge.
	 *
	 * @param  pluginKey  The key identifying the bridge
	 * @return            True, if the bridge is loaded
	 */
	private boolean isLoadedBridge( String pluginKey )
	{
		return loadedBridges.contains( pluginKey );
	}


	/**
	 *  Check bridges to see if the required plugin exists. If not, throw away
	 *  the bridge.
	 */
	private void checkBridges()
	{
		for ( Iterator i = bridges.keySet().iterator(); i.hasNext();  ) {
			if ( jEdit.getPlugin( (String) i.next() ) == null ) {
				i.remove();
			}
		}
		if ( loadedBridges.size() == bridges.size() ) {
			ignore();
		}
	}


	/**
	 *  Check for possible bridge points, enabling the bridges if the target plugins
	 *  are present.
	 *
	 * @param  view  A view ?!
	 */
	private void enableBridges( View view )
	{
		for ( Iterator i = bridges.keySet().iterator(); i.hasNext();  ) {
			String each = (String) i.next();
			if ( isLoadedBridge( each ) ) {
				continue;
			}

			EditPlugin plugin = jEdit.getPlugin( each );

			try {
				if ( plugin != null ) {
					enableBridge( each, plugin, view );
				}
			}
			catch ( jEditException e ) {
				Log.log( Log.WARNING, this, e );
			}
		}
	}


	/**
	 *  Listen to the bus.
	 */
	private void listen()
	{
		if ( !listening ) {
			EditBus.addToBus( this );
		}
		listening = true;
		//Log.log( Log.DEBUG, this, "Listening to the bus..." );
	}


	/**
	 *  Ignore the bus.
	 */
	private void ignore()
	{
		EditBus.removeFromBus( this );
		//Log.log( Log.DEBUG, this, "Ignoring to the bus..." );
	}


	/**
	 *  Enable a bridge.
	 *
	 * @param  pluginKey           A key for the plugin
	 * @param  tgtPlugin           The target plugin
	 * @param  view                The view ?!
	 * @exception  jEditException  An exception if something goes wrong.
	 */
	private void enableBridge( String pluginKey, EditPlugin tgtPlugin, View view )
		 throws jEditException
	{
		PluginBridge bridge = loadBridge( pluginKey );
		if ( bridge.enable( srcPlugin, tgtPlugin, view ) ) {
			loadedBridges.add( pluginKey );
			//Log.log( Log.DEBUG, this, srcPlugin.getClass() + " to " + tgtPlugin.getClass() + " enabled" );
			if ( loadedBridges.size() == bridges.size() ) {
				ignore();
			}
		}
	}


	/**
	 *  Load a bridge.
	 *
	 * @param  pluginKey           A plugin key
	 * @return                     The plugin bridge for the given key
	 * @exception  jEditException  An Exception if something goes wrong
	 */
	private PluginBridge loadBridge( String pluginKey )
		 throws jEditException
	{
		try {
			String bridgeClass = (String) bridges.get( pluginKey );
			return (PluginBridge) Class.forName( bridgeClass ).newInstance();
		}
		catch ( Throwable t ) {
			throw new jEditException( "Unable to load plugin bridge", t );
		}
	}

}

