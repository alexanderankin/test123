/*
 * ProcessRunner.java - Abstracts away OS-specific stuff
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
 * Java 1.5 version (c) 2005 by Alan Ezust
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

package console;

// {{{ Imports
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import console.utils.StringList;

// }}}


abstract class ProcessRunner
{
	// {{{ Data Members 
	ProcessBuilder processBuilder;
	private static ProcessRunner instance;
	// }}}
	
	// {{{ abstract shellExpandsGlobs() 
	abstract boolean shellExpandsGlobs();
	// }}}
	
	// {{{ abstract isCaseSensitive ()
	abstract boolean isCaseSensitive();
	// }}}
	
	// {{{ final supportsEnvironmentVariables() 
	final boolean supportsEnvironmentVariables()
	{
		return true;
	}
	// }}}
	
	// {{{ getEnvironmentVariables() 
	final Map<String, String> getEnvironmentVariables()
	{
		return processBuilder.environment();
	}
	// }}}
	
	// {{{ setupDefaultAliases (stub)  
	void setUpDefaultAliases(Hashtable aliases)
	{
	}

	// }}}
	
	// {{{ exec()
	/**
	 * 
	 * @since New to Java 1.5 - this has many similar features to the
	 *        ProcessRunner, and eventually we should refactor this code.
	 */
	Process exec(String[] args, ProcessBuilder pBuilder, String dir) throws IOException
	{

		String prefix = jEdit.getProperty("console.shell.prefix");
		/** check to ensure that the shell prefix is set for
		     win32 platforms - we use line.separator to determine
		     which platform is being used.  */
		if (prefix == null || prefix.length() < 1) {
			int  ls = (int)System.getProperty("line.separator").charAt(0);
			if (ls == 13) {
				prefix = "cmd /c";
				jEdit.setProperty("console.shell.prefix", prefix);
			}
		}
		StringList arglist = StringList.split(prefix, "\\s+");
		arglist.addAll(args);
		processBuilder = pBuilder;
		processBuilder.directory(new File(dir));
		// Merge stdout and stderr
		processBuilder.redirectErrorStream(true);
		processBuilder.command(arglist);
		try
		{
			return processBuilder.start();
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, e, "Process Runner");
		}
		return null;
		/*
		 * return Runtime.getRuntime().exec(args,env,new File(dir));
		 */
	}
	// }}}

	// {{{ getProcessRunner() method
	static ProcessRunner getProcessRunner()
	{
		if (instance == null)
		{
			if (OperatingSystem.isWindows9x())
				instance = new Windows9x();
			else if (OperatingSystem.isWindowsNT())
				instance = new WindowsNT();
			else if (OperatingSystem.isUnix())
				instance = new Unix();
			else
			{
				Log.log(Log.WARNING, ProcessRunner.class,
					"Unknown operating system");
				instance = new Generic();
			}
		}

		return instance;
	} 
	
	// }}}
	
	// {{{ Generic class
	static class Generic extends ProcessRunner
	{
		boolean shellExpandsGlobs()
		{
			return true;
		}

		boolean isCaseSensitive()
		{
			return true;
		}
	} // }}}

	// {{{ Unix class
	static class Unix extends ProcessRunner
	{
		// {{{ shellExpandsGlobs() method
		boolean shellExpandsGlobs()
		{
			return true;
		} // }}}

		// {{{ isCaseSensitive() method
		boolean isCaseSensitive()
		{
			return true;
		} // }}}
	} // }}}

	// {{{ Windows class
	abstract static class Windows extends ProcessRunner
	{
		// {{{ shellExpandsGlobs() method
		boolean shellExpandsGlobs()
		{
			return false;
		} // }}}

		// {{{ isCaseSensitive() method
		boolean isCaseSensitive()
		{
			return false;
		} // }}}
	} // }}}

	// {{{ Windows9x class
	static class Windows9x extends Windows
	{

		// {{{ setUpDefaultAliases() method
		void setUpDefaultAliases(Hashtable aliases)
		{
			String[] builtins = { "md", "rd", "del", "dir", "copy", "move", "erase",
				"mkdir", "rmdir", "start", "echo", "path", "ver", "vol", "ren",
				"type" };
			for (int i = 0; i < builtins.length; i++)
			{
				aliases.put(builtins[i], "command.com /c " + builtins[i]);
			}
		} // }}}

		// {{{ exec() method
		Process exec(String[] args, String[] env, String dir) throws IOException
		{
			String commandName = args[0];

			String[] extensionsToTry;
			if (commandName.indexOf('.') == -1)
				extensionsToTry = getExtensionsToTry();
			else
				extensionsToTry = new String[] { "" };

			for (int i = 0; i < extensionsToTry.length; i++)
			{
				args[0] = commandName + extensionsToTry[i];

				try
				{
					// processBuilder = new
					// ProcessBuilder(args);
					processBuilder.directory(new File(dir));
					processBuilder.command(args);
					processBuilder.redirectErrorStream(true);
					return processBuilder.start();
				}
				catch (Exception e)
				{
					if (i == extensionsToTry.length - 1)
					{
						// throw a new exception cos
						// Windows error messages are
						// a bit cryptic
						throw new RuntimeException(jEdit.getProperty(
							"console.shell.not-found-win",
							new String[] { commandName, }));
					}
				}
			}

			// can't happen
			return null;
		} // }}}

		// {{{ getExtensionsToTry() method
		String[] getExtensionsToTry()
		{
			return new String[] { ".exe", ".com" };
		} // }}}
	} // }}}

	// {{{ WindowsNT class
	static class WindowsNT extends Windows
	{
	} // }}}
}

