/*
 * ConsoleShellPluginPart.java - Manages console shell
 * Copyright (C) 2000 Slava Pestov
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import java.util.Hashtable;
import java.util.Vector;

public class ConsoleShellPluginPart extends EBPlugin
{
	public static final String NAME = "Console";

	public void start()
	{
		errorSource = new DefaultErrorSource(NAME);
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
		EditBus.addToBus(errorSource);
		EditBus.addToNamedList(Shell.SHELLS_LIST,NAME);
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof CreateShell)
		{
			CreateShell createShell = (CreateShell)msg;
			if(createShell.getShellName().equals(NAME))
			{
				createShell.setShell(new ConsoleShell());
			}
		}
	}

	// package-private members
	static void addError(int type, String file, int lineIndex, String error)
	{
		errorSource.addError(type,file,lineIndex,0,0,error);
	}

	static void clearErrors()
	{
		errorSource.clear();
	}

	// private members
	private static DefaultErrorSource errorSource;
}
