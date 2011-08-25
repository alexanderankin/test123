/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 jEdit contributors
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

package com.kpouer.jedit.remotecontrol.executionengine;

import com.kpouer.jedit.remotecontrol.CommandResponse;
import com.kpouer.jedit.remotecontrol.RemoteClient;
import com.kpouer.jedit.remotecontrol.RemoteServer;
import com.kpouer.jedit.remotecontrol.SingleLineCommandHandler;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.awt.*;

/**
 * @author Matthieu Casanova
 */
public class BeanshellEngine implements Engine
{
	@Override
	public void execute(final RemoteClient client, final String script, final String transactionId)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				NameSpace ns = new NameSpace(
						BeanShell.getNameSpace(),
						"RemoteClient namespace");

				Object ret;
				try
				{
					ret = BeanShell.eval(jEdit.getActiveView(), ns, script);
				}
				catch (Throwable e)
				{
					Log.log(Log.ERROR, this, "Remote command failed:" + script, e);
					ret = e;
				}
				if (transactionId != null)
				{
					CommandResponse response = new CommandResponse(transactionId, ret);
					client.sendObject(response);
				}
			}
		});
	}
}
