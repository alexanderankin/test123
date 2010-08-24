/*
* MibSideKickPlugin.java - The Mib sidekick plugin
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2009, 2010 Matthieu Casanova
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

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibLoaderLog;
import net.percederberg.mibble.browser.MibTreeBuilder;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.text.Segment;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Collection;

/**
 * @author Matthieu Casanova
 */
public class MibSidekickParser extends SideKickParser
{
	private static Collection<File> searchPath;

	public MibSidekickParser()
	{
		super("MibParser");
	}

	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
	{
		MibLoader loader = new MibLoader();
		try
		{
			if (buffer.isDirty())
			{
				// the buffer is dirty, we cannot parse it
				return null;
			}
			File file = new File(buffer.getPath());
			loader.addDir(file.getParentFile());
			for (File path : searchPath)
			{
				if (path.isDirectory())
					loader.addDir(path);
			}
//			loader.addResourceDir("mibs/iana");
//			loader.addResourceDir("mibs/ietf");
			final Mib mib = loader.load(file);
			final SideKickParsedData datas = new SideKickParsedData(buffer.getPath());
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					MibTreeBuilder tree = MibTreeBuilder.getInstance();
					tree.addMib(mib);
					TreeModel model = tree.getTree().getModel();
					Object root = model.getRoot();
					int count = model.getChildCount(root);
					for (int i = 0; i < count; i++)
					{
						Object child = model.getChild(root, i);
						datas.root.add((MutableTreeNode) child);
					}
				}
			};
			EventQueue.invokeAndWait(runnable);
			return datas;
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, e, e);
		}
		catch (MibLoaderException e)
		{
			MibLoaderLog mibLoaderLog = e.getLog();
			Iterator<MibLoaderLog.LogEntry> iterator = mibLoaderLog.entries();
			while (iterator.hasNext())
			{
				MibLoaderLog.LogEntry logEntry =  iterator.next();
				if (logEntry.getLineNumber() == -1 ||
				    !logEntry.getFile().getAbsolutePath().equals(buffer.getPath()))
				{
					continue;
				}
				int line = logEntry.getLineNumber() - 1;
				int endColumn = buffer.getLineLength(line);
				int type = logEntry.getType() == MibLoaderLog.LogEntry.WARNING ? ErrorSource.WARNING : ErrorSource.ERROR;
				errorSource.addError(type,
				                     buffer.getPath(),
				                     line,
				                     logEntry.getColumnNumber() - 1,
				                     endColumn,
				                     logEntry.getMessage());
			}


		}
		catch (InterruptedException e)
		{
		}
		catch (InvocationTargetException e)
		{
		}
		return null;
	}

	static void propertiesChanged()
	{
		searchPath = MibOptionPane.getPaths();
	}
}
