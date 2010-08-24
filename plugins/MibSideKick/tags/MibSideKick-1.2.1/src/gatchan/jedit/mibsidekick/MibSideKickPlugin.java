/*
* MibSideKickPlugin.java - The Mib sidekick plugin
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2009 Matthieu Casanova
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package gatchan.jedit.mibsidekick;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import java.util.HashMap;
import java.util.Map;

/**
 * The MibSidekick plugin
* @author Matthieu Casanova
*/
public class MibSideKickPlugin extends EBPlugin
{
	@Override
	public void start()
	{
		MibSidekickParser.propertiesChanged();
	}

	@Override
	public void handleMessage(EBMessage message)
	{
		if (message instanceof PropertiesChanged)
		{
			MibSidekickParser.propertiesChanged();
		}
	}
}
