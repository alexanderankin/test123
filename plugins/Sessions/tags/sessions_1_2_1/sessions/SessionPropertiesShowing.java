/*
 * SessionPropertiesShowing.java - EditBus message
 * Copyright (c) 2001 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
 */


package sessions;


import java.util.Vector;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;


/**
 * This message is sent out on EditBus before the "Session Properties"
 * dialog is being displayed for the current session.
 * It allows third parties to add custom sesion property panes
 * to the dialog, using <code>addPropertyPane()</code> and
 * <code>addPropertyGroup()</code>.
 */
public final class SessionPropertiesShowing extends EBMessage
{

	SessionPropertiesShowing(EBComponent source, Session session)
	{
		super(source);
		this.session = session;
		rootGroup = new SessionPropertyGroup("All Current Session Properties");
	}


	public final SessionManager getSessionManager()
	{
		return (SessionManager) getSource();
	}


	/**
	 * Return the session bound to this message.
	 */
	public final Session getSession()
	{
		return session;
	}


	/**
	 * Add a session property pane to the Session Properties dialog
	 * originating from this message.
	 * Note that the same property pane instance cannot be added twice.
	 */
	public final void addPropertyPane(SessionPropertyPane pane)
	{
		rootGroup.addPane(pane);
	}



	/**
	 * Add a session property group to the Session Properties dialog
	 * originating from this message.
	 * Note that the same property group instance cannot be added twice.
	 */
	public final void addPropertyGroup(SessionPropertyGroup group)
	{
		rootGroup.addGroup(group);
	}


	public String paramString()
	{
		return super.paramString() + ",session=" + session;
	}


	SessionPropertyGroup getRootGroup()
	{
		return rootGroup;
	}


	private Session session;
	private SessionPropertyGroup rootGroup;

}

