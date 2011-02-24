/*
 * ArchivePlugin.java
 *
 * :tabSize=4:indentSize=4:noTabs=true:
 *
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
 * Portions copyright (C) 2004 Slava Pestov
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


package archive;

import java.io.File;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.VFSUpdate;
import org.gjt.sp.util.Log;

public class ArchivePlugin extends EditPlugin
{
	private static final Object tempLock = new Object();
	private static String tempDirectory;
	private static int tmpFileCount;

	/**
	 * This is not really safe!
	 * Check for null return value.
	 */
	public static String tempFileName()
	{
		synchronized (tempLock)
		{
			if (tempDirectory == null)
				return null;
			else
			{
				tmpFileCount++;
				long time = System.currentTimeMillis();
				return MiscUtilities.constructPath(tempDirectory,
				                                   "cache-" + tmpFileCount + "-" + time + ".tmp");
			}
		}
	}

	public void start()
	{
		String settingsDirectory = jEdit.getSettingsDirectory();
		if (settingsDirectory == null)
		{
			Log.log(Log.WARNING, ArchiveDirectoryCache.class, "-nosettings "
			                                                  + "command line switch specified; archive directories");
			Log.log(Log.WARNING, ArchiveDirectoryCache.class, "will not be cached.");
		}
		else
		{
			File pluginHome = getPluginHome();
			if (pluginHome != null)
			{
				tempDirectory = pluginHome.getAbsolutePath();
				pluginHome.mkdirs();
			}
		}
		EditBus.addToBus(this);
	}

	public void stop()
	{
		EditBus.addToBus(this);
		// Clear cached directory listings
		ArchiveDirectoryCache.clearAllCachedDirectories();
	}

	@EditBus.EBHandler
	public void handleVFSUpdate(VFSUpdate vmsg)
	{
		ArchiveDirectoryCache.clearCachedDirectory(vmsg.getPath());
	}
}

