/*
 * EBMessageHandler.java - ActionHooks Plugin
 *
 * Copyright 2004 Ollie Rutherfurd <oliver@rutherfurd.net>
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
 * $Id: EBMessageHandler.java,v 1.1 2004/02/01 19:40:39 orutherfurd Exp $
 */
package actionhooks;

import java.util.Vector;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

/**
 * EBMessageHandler subclasses are needed to support
 * handling EBMessages for different EBMessage classes.
 *
 * ActionHooks dispatches message handling to the
 * appropriate handler for the message type.  If
 * no EBMessageHandler is register for the received
 * message, ActionHooks ignores it.
 *
 * Plugins may provide EBMessageHandler subclasses
 * for messages they provide if they wish to allow
 * ActionHooks to be able to invoke actions in
 * response to those messages.
 */
public abstract class EBMessageHandler
{

	/**
	 * Returns the class name event handles.
	 * The class named must be subclass of EBMessage.
	 *
	 * Example: org.gjt.sp.jedit.msg.BufferUpdate
	 */
	public abstract String getMessageName();


	/**
	 * Returns as list of event names this handler
	 * handles.	 For example:
	 *
	 * <ul>
	 *	 <li>BufferUpdate.LOADED</li>
	 *	 <li>BufferUpdate.CLOSED</li>
	 *	 <li>BufferUpdate.SAVING</li>
	 *	 <li>BufferUpdate.SAVED</li>
	 * </ul>
	 *
	 */
	public abstract String[] getEventNames();


	/**
	 * This message should extract the View
	 * from the EBMessage it handles.  If
	 * this returns null, ActionHooks will
	 * use the active View when firing any actions
	 * or macros.
	 */
	public abstract View getView(EBMessage msg);


	/**
	 * Returns the event name for the current message.
	 *
	 * This is required as some EBMessages have a
	 * getWhat() message which identfies the message
	 * name, but others don't.
	 */
	public abstract String getEventName(EBMessage msg);


	/**
	 * Return true if ActionHooks should skip this message.
	 *
	 * This allows EBMessageHandlers to indicate that a 
	 * message should be skipped.  This is useful in sitations
	 * where actions should not be fired in response to an
	 * event.
	 */
	public boolean skip(EBMessage msg)
	{
		return false;
	}


	/**
	 * Fires actions or macros for the given msg.
	 */
	public final void fireActions(EBMessage msg)
	{
		if(this.skip(msg))
		{
			return;
		}

		Log.log(Log.DEBUG, this, "Handling " + getMessageName()); // ##
		View view = getView(msg);
		String event = getEventName(msg);

		if(event == null)
		{
			Log.log(Log.DEBUG, this, "event unknown for " + msg);
			return;
		}

		// get actions, return if none
		Vector actions = ActionHooksPlugin.getActionNamesForEvent(event);
		if(actions.size() == 0)
			return;

		// use active view if no view returned by EBMessageHandler
		if(view == null)
		{
			Log.log(Log.DEBUG, this, "view is null for " + msg +
						" using active view");
			view = jEdit.getActiveView();
		}

		Log.log(Log.DEBUG, this, "Invoking " + actions.size() 
								 + " actions for " + event);

		for(int i=0; i < actions.size(); i++)
		{
			String name = (String)actions.elementAt(i);
			// XXX special-case macro execution to know about 
			// errors -- is it possible?
			try
			{
				view.getInputHandler().invokeAction(name);
			}
			catch(NullPointerException npe)
			{
				// XXX offer to unbind action or do automatically?
				Log.log(Log.ERROR, this,
						"Trying to invoke action: " + name);
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR, this, 
					"Error executing " + name 
					+ " for event " + event);
				e.printStackTrace();
			}
		}
	}
}

// :collapseFolds=1:noTabs=false:lineSeparator=\n:tabSize=4:indentSize=4:deepIndent=true:folding=explicit:
