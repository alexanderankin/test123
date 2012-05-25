/*
 * CheckstylePlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009-2012 Matthieu Casanova
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
package com.kpouer.jedit.checkstyle;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.ThreadUtilities;

/**
 * @author Matthieu Casanova
 */
public class CheckstylePlugin extends EBPlugin
{
	private DefaultErrorSource errorSource;
	private boolean runOnSave;

	@Override
	public void start()
	{
		errorSource = new DefaultErrorSource("checkstyle");
		ErrorSource.registerErrorSource(errorSource);
	}

	@Override
	public void stop()
	{
		ErrorSource.unregisterErrorSource(errorSource);
		errorSource = null;
	}

	public static void checkBuffer(Buffer buffer)
	{
		CheckstylePlugin plugin = (CheckstylePlugin) jEdit.getPlugin(CheckstylePlugin.class.getName());
		CheckstyleParse parser = new CheckstyleParse(buffer, plugin.errorSource);
		ThreadUtilities.runInBackground(parser);
	}

	public static void checkAllOpenBuffers()
	{
		CheckstylePlugin plugin = (CheckstylePlugin) jEdit.getPlugin(CheckstylePlugin.class.getName());
		CheckstyleParse parser = new CheckstyleParse(jEdit.getBuffers(), plugin.errorSource);
		ThreadUtilities.runInBackground(parser);
	}

	public static void checkFiles(VFSFile[] files)
	{
		CheckstylePlugin plugin = (CheckstylePlugin) jEdit.getPlugin(CheckstylePlugin.class.getName());
		CheckstyleParse parser = new CheckstyleParse(files, plugin.errorSource);
		ThreadUtilities.runInBackground(parser);
	}

	@Override
	public void handleMessage(EBMessage message)
	{
		if (message instanceof PropertiesChanged)
		{
			runOnSave = jEdit.getBooleanProperty("checkstyle.runonsave");
		}
		else if (message instanceof BufferUpdate)
		{
			if (runOnSave)
			{
				BufferUpdate bufferUpdate = (BufferUpdate) message;
				if (bufferUpdate.getWhat() == BufferUpdate.SAVED)
				{
					Buffer buffer = bufferUpdate.getBuffer();
					if ("java".equals(buffer.getMode().getName()))
						checkBuffer(buffer);
				}
			}
		}
	}
}
