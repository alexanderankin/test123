/*
 * ActionHooksPlugin.java - ActionHooks Plugin
 *
 * Copyright 2003,2004 Ollie Rutherfurd <oliver@rutherfurd.net>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id: ActionHooksPlugin.java,v 1.6 2004/02/01 20:12:14 orutherfurd Exp $
 */
package actionhooks;

//{{{ import
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
//}}}

public class ActionHooksPlugin extends EBPlugin
{

    //{{{ start() method
    public void start()
    {
		setEnabled(jEdit.getBooleanProperty("actionhooks.enabled",
											false));

		actionMap = new HashMap();
		msgMap = new HashMap();

		registerHandler(new actionhooks.handler.BufferUpdateHandler());
		registerHandler(new actionhooks.handler.EditPaneUpdateHandler());
		registerHandler(new actionhooks.handler.ViewUpdateHandler());
		registerHandler(new actionhooks.handler.PropertiesChangedHandler());

    } //}}}

    //{{{ stop() method
    public void stop()
    {
		actionMap = null;
		msgMap = null;
    } //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{

		// exit quickly if not enabled
		if(enabled == false)
			return;

		// make sure a view has been created
		if(jEdit.getFirstView() == null)
			return;

		EBMessageHandler handler = (EBMessageHandler)msgMap.get(msg.getClass().getName());
		if(handler != null)
		{
			Log.log(Log.DEBUG, this, "using " + handler + " to fireAction..."); // ##
			handler.fireActions(msg);
			return;
		}

		// This may not be needed any since we're not
		// keeping references to EditActions anymore.
		// However, rather than generating a warning each
		// time an action is invoked, it's cleaner to
		// remove the actions.
		if(msg instanceof PluginUpdate)
		{
			PluginUpdate pu = (PluginUpdate)msg;
			if(pu.getWhat().equals(PluginUpdate.UNLOADED))
			{
				if(pu.isExiting())
					return;

				EditPlugin plugin = pu.getPluginJAR().getPlugin();

				Log.log(Log.DEBUG, ActionHooksPlugin.class,
					"Checking for actions to unbind from " 
					+ plugin.getClassName());

				ActionSet actionSet = pu.getPluginJAR().getActionSet();
				String[] names = actionSet.getActionNames();
				Collection actionSets = getActionMap().values();
				
				for(int i=0; i < names.length; i++)
				{
					String name = names[i];

					// iterate through all action sets
					for(Iterator iter = actionSets.iterator(); iter.hasNext();)
					{
						Vector actions = (Vector)iter.next();
						// compare against each action in the set
						for(int j = actions.size()-1; j >= 0; j--)
						{
							String action = (String)actions.elementAt(j);
							if(name.equals(action))
							{
								Log.log(Log.NOTICE, this,
									"Unloading action " + name);
								actions.remove(action);
							}
						}
					}
				}
			}
		}

	} //}}}

	//{{{ registerHandler() method
	/**
	 * Registers an EBMessageHandler and loads all
	 * actions bound to events provided by the handler.
	 */
	public void registerHandler(EBMessageHandler handler)
	{
		Log.log(Log.DEBUG, this, "registering handler for " 
								 + handler.getMessageName()); // ##

		String msg = handler.getMessageName();
		if(msgMap.get(msg) != null)
		{
			Log.log(Log.ERROR, this, "EBMessageHandler for " + msg
									 + " already registered");
			return;
		}
		msgMap.put(msg,handler);

		String[] events = handler.getEventNames();
		for(int i=0; i < events.length; i++)
			loadActions(events[i]);
	} //}}}

	//{{{ unregisterHandler() method
	/**
	 * Removes an EBMessageHandler and all actions bound
	 * to events handled by the handler.
	 */
	public void unregisterHandler(EBMessageHandler handler)
	{
		Log.log(Log.DEBUG, this, "unregistering handler for "
								 + handler.getMessageName());
		String[] events = handler.getEventNames();
		HashMap actionMap = getActionMap();
		for(int i=0; i < events.length; i++)
			actionMap.remove(events[i]);
		msgMap.remove(handler.getMessageName());
	} //}}}

    //{{{ getEvents() method
    /**
     * Returns event names that this plugin handles.
     */
    public static Vector getEvents()
    {
		Vector events = new Vector(actionMap.keySet().size());
		for(Iterator i = actionMap.keySet().iterator(); i.hasNext();)
		{
			String event = (String)i.next();
			Log.log(Log.DEBUG, ActionHooksPlugin.class,
				"event: " + event);
			events.addElement(event);
		}
		return events;
    } //}}}

    //{{{ getActionNamesForEvent() method
    /**
     * returns a Vector of action names for the given event.
     */
    public static Vector getActionNamesForEvent(String event)
    {
		Vector names = (Vector)getActionMap().get(event);
		if(names == null)
			names = new Vector();
		return names;
    } //}}}

	//{{{ getActionNamesForEvent() method
	/**
	 * Returns EditActions bound to <code>event</event>.
	 * @param event name of event, for example BufferUpdate.SAVED
	 */
	public static Vector getActionsForEvent(String event)
	{
		Vector names = getActionNamesForEvent(event);
		Vector actions = new Vector(names.size());
		for(int i=0; i < names.size(); i++)
		{
			String name = (String)names.elementAt(i);
			EditAction action = jEdit.getAction(name);
			if(action == null)
				Log.log(Log.ERROR, ActionHooksPlugin.class,
						"Couldn't get action: " + name);
			else
				actions.addElement(action);
		}
		return actions;
	} //}}}

	//{{{ setActionsForEvent() method
	public static void setActionsForEvent(String event, Vector actions)
	{
		getActionMap().put(event,actions);
	} //}}}

    //{{{ getActionMap() method
    public static HashMap getActionMap()
    {
		return actionMap;
    } //}}}

	//{{{ loadActions(String event)
	/**
	 * Loads names of EditActions bound to event.
	 */
	public void loadActions(String event)
	{
		Vector actions = new Vector();
		Log.log(Log.DEBUG, this,
			"Loading actions for " + event);
		String propval = jEdit.getProperty(event + ".actions","");
		StringTokenizer tokens = new StringTokenizer(propval, ",");
		while(tokens.hasMoreTokens())
		{
			String name = tokens.nextToken();
			Log.log(Log.DEBUG, this, "adding action " + name); // ##
			actions.add(name);
		}
		actionMap.put(event,actions);
	} //}}}

	//{{{ loadActions() method
	/**
	 * Loads all bound EditActions for all events
	 * from all registered handlers.
	 */
	public void loadActions()
	{
		Vector events = ActionHooksPlugin.getEvents();
		for(int i=0; i < events.size(); i++)
		{
			String event = (String)events.elementAt(i);
			loadActions(event);
		}
	} //}}}

	//{{{ getEnabled() method
	public static boolean getEnabled()
	{
		return enabled;
	} //}}}

	//{{{ setEnabled() method
	public static void setEnabled(boolean enabled)
	{
		ActionHooksPlugin.enabled = enabled;
	} //}}}

    //{{{ private declarations
    private static HashMap msgMap;
	private static HashMap actionMap;
	private static boolean enabled;
    //}}}
}

// :collapseFolds=1:noTabs=false:lineSeparator=\n:tabSize=4:indentSize=4:deepIndent=false:folding=explicit:
