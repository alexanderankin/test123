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
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import console.utils.StringList;

// }}}


public abstract class ProcessRunner
{
	// {{{ Data Members
	
	// Each ProcessRunner holds onto a processBuilder.
	ProcessBuilder processBuilder;
	
	private static ProcessRunner instance;
	// }}}
	
	// {{{ abstract shellExpandsGlobs() 
	abstract boolean shellExpandsGlobs();
	// }}}
	
	// {{{ abstract isCaseSensitive ()
	abstract boolean isCaseSensitive();
	// }}}
	
	// {{{ abstract shellPrefix()
	/**
	 * @return the default/preferred system command interpreter
	 * for the given operating system.
	 * @since console 4.2.5
	 * 
	 * 
	 * Examples:
	 * 	linux:   	bash -c
	 * 	winnt:	cmd /c
	 * 	win95:	command.com /c
	 * 
	 */
	abstract String shellPrefix();
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
	
	// {{{ setUpDefaultAliases (stub)  
	void setUpDefaultAliases(Hashtable <String, String> aliases)
	{
	}
	/**
	 * Takes a string and prepends it to PATH
	 *
	 */
	public void prependUserPath()  {
		if (processBuilder == null) return;
		String extra = jEdit.getProperty("console.shell.pathdirs");
		String oldPath = processBuilder.environment().get("PATH");
		String newPath = extra + File.pathSeparator + oldPath;
		processBuilder.environment().put("PATH", newPath);
	}
	
	// {{{ exec()
	/**
	 * 
	 * @since Java 1.5 - this has many similar features to the
	 *        ProcessRunner, and eventually we should refactor this code.
	 */
	Process exec(String[] args, ProcessBuilder pBuilder, String dir) throws IOException
	{

		String prefix = jEdit.getProperty("console.shell.prefix");
		if (prefix == null || prefix.length() < 1) {
			prefix = instance.shellPrefix();
		}
		StringList arglist = StringList.split(prefix, "\\s+");
		if (arglist.get(0).equals("none")) {
			arglist.clear();
		}
		/* bash needs a single argument with quoted strings around the parts
		 * with spaces.
		 */
		else if (arglist.get(0).equals("bash")) {
			StringList qargs = new StringList();
			// put quotes around the strings with spaces
			for (String a: args) 
			{
				if (a.contains(" ")) {
					qargs.add("\"" + a + "\"");
				}
				else {
					qargs.add(a);
				}
				
			}
			String cmd = StringList.join(qargs, " ");
			arglist.add(cmd);
		}
		/* cmd, in contrast, can accept multiple arguments */
		else {
			arglist.addAll(args);
		}
		processBuilder = pBuilder;
		processBuilder.directory(new File(dir));
		// Merge stdout and stderr
		boolean merge = jEdit.getBooleanProperty( "console.processrunner.mergeError", true);
		processBuilder.redirectErrorStream( merge );
		processBuilder.command(arglist.toArray());
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
	public static ProcessRunner getProcessRunner()
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

		@Override
		String shellPrefix()
		{
			return "";
		}
	} // }}}

	// {{{ Unix class
	static class Unix extends ProcessRunner
	{
		// {{{ setUpDefaultAliases (stub)  
		void setUpDefaultAliases(Hashtable <String, String> aliases)
		{
			aliases.put("del", "rm");
			aliases.put("copy", "cp");
			aliases.put("ren", "mv");
		}
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

		@Override
		String shellPrefix()
		{
			return "/bin/bash -c";
		}
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
		void setUpDefaultAliases(Hashtable<String, String> aliases)
		{
			String[] builtins = { "md", "rd", "del", "dir", "copy", "move", "erase",
				"mkdir", "rmdir", "start", "echo", "path", "ver", "vol", "ren",
				"type" };
			for (int i = 0; i < builtins.length; i++)
			{
				aliases.put(builtins[i], "%" + builtins[i]);
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

		@Override
		String shellPrefix()
		{
			return "command.com /c";
		}
	} // }}}

	// {{{ WindowsNT class
	static class WindowsNT extends Windows
	{
		// {{{ setUpDefaultAliases() method
		void setUpDefaultAliases(Hashtable<String, String> aliases)
		{
			aliases.put("pwd", "%pwd");
			aliases.put("aliases", "%aliases");
			aliases.put("alias", "%alias");
			aliases.put("mv", "ren");
			aliases.put("cp", "copy");
			aliases.put("ls", "dir");
			aliases.put("cat", "type");

		} // }}}

		@Override
		String shellPrefix()
		{
			return "cmd /c";
		}
	} // }}}
}

