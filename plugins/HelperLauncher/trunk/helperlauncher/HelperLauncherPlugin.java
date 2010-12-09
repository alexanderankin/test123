/*
 *  HelperLauncherPlugin.java - HelperLauncher plugin
 *  Copyright (C) 2003 Carmine Lucarelli
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package helperlauncher;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Properties;


/**
 *  A plugin for launching external applications
 *
 *@author    <A HREF="mailto:carmine.lucarelli@lombard.ca">Carmine Lucarelli</A>
 */
public class HelperLauncherPlugin extends EditPlugin
{
	
	public static void launch(Buffer buffer)
	{
		launch(buffer.getPath());
	}
	
	public static void launch(String path)
	{
		try
		{
			//if(OperatingSystem.isWindows())
			if(jEdit.getBooleanProperty(HelperLauncherOptionPane.PROPERTY + "UseWindows"))
				Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + 
					"\"" + path + "\"");
			else
			{
				String fileExt = MiscUtilities.getFileExtension(path);
				int count = 1;
				String glob = jEdit.getProperty(HelperLauncherOptionPane.PROPERTY + 
					count + HelperLauncherOptionPane.NAME);
				while(glob != null)
				{
					Pattern pattern = Pattern.compile(StandardUtilities.globToRE(glob),
						Pattern.CASE_INSENSITIVE);
					if(pattern != null && pattern.matcher(fileExt).matches())
					{
						String app = jEdit.getProperty(HelperLauncherOptionPane.PROPERTY + 
							count + HelperLauncherOptionPane.VALUE);
						if(app == null)
						{
							// TODO: Choose Executable
							String[] args = {"Blank/Null", "glob " + glob};
							GUIUtilities.error(jEdit.getActiveView(), 
								"HelperLauncher.error.noexecutable",
								args);
							return;
						}
						Runtime.getRuntime().exec(new String[] {app, path}, null);
						return;
					}
					count++;
					glob = jEdit.getProperty(HelperLauncherOptionPane.PROPERTY + 
						count + HelperLauncherOptionPane.NAME);
				}
				String[] args = {"No", fileExt};
				GUIUtilities.error(jEdit.getActiveView(), 
					"HelperLauncher.error.noexecutable",
					args);
				return;
			}
		}
		catch(Exception whoops)
		{
			try
			{
				Log.log(Log.ERROR, Class.forName("helperlauncher.HelperLauncherPlugin").
					getName(), whoops);
				String[] args = {whoops.getMessage()};
				GUIUtilities.error(jEdit.getActiveView(), 
					"HelperLauncher.error.exception",
					args);
			}
			catch(Exception e)
			{}
		}
	}
	
	static Properties getGlobalProperties()
	{
		Properties properties = new Properties();

		String name;
		int counter = 1;
		while ((name = jEdit.getProperty(HelperLauncherOptionPane.PROPERTY + counter + HelperLauncherOptionPane.NAME)) != null) {
			properties.put(name, jEdit.getProperty(HelperLauncherOptionPane.PROPERTY + counter + HelperLauncherOptionPane.VALUE));
			counter++;
		}
		return properties;
	}
}

