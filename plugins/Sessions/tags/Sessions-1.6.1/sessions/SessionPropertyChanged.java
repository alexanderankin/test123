/*
 * SessionPropertyChanged.java - EditBus message
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


import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;


public final class SessionPropertyChanged extends EBMessage
{

	SessionPropertyChanged(
		EBComponent source,
		Session session,
		String key,
		String oldValue,
		String newValue)
	{
		super(source);
		this.session = session;
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}


	public final SessionManager getSessionManager()
	{
		return (SessionManager) getSource();
	}


	public final Session getSession()
	{
		return session;
	}


	public final String getKey()
	{
		return key;
	}


	public final String getOldValue()
	{
		return oldValue;
	}


	public final String getNewValue()
	{
		return newValue;
	}


	public String paramString()
	{
		return super.paramString()
			+ ",session=" + session.getName()
			+ ",key=" + key;
	}


	private Session session;
	private String key;
	private String oldValue;
	private String newValue;

}

