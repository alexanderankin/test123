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

//{{{ Imports
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import console.utils.StringList;
//}}}

abstract class ProcessRunner
{
	abstract boolean shellExpandsGlobs();

	final boolean supportsEnvironmentVariables() {return true; }

	HashMap<String, String> presetVars = null;

	final Map<String, String> getEnvironmentVariables() {
		return processBuilder.environment();
	}


	void setUpDefaultAliases(Hashtable aliases) {}

	abstract boolean isCaseSensitive();

	/**
	 * 
	 * @since New to Java 1.5 - this has many similar features to
	 * the ProcessRunner, and eventually we should refactor
	 * this code. 
	 */
	ProcessBuilder processBuilder;
	
	Process exec(String[] args, ProcessBuilder pBuilder,  String dir)
		throws IOException
	{
		
		String prefix = jEdit.getProperty("console.shell.prefix");
		StringList arglist = StringList.split(prefix, "\\s+");
		arglist.addAll(args);
		processBuilder = pBuilder;
		processBuilder.directory(new File(dir));
		// Merge stdout and stderr
		processBuilder.redirectErrorStream(true);
		processBuilder.command(arglist);
		try {
			long before = System.currentTimeMillis();
			Process retval = processBuilder.start();
			long after = System.currentTimeMillis();
			Log.log(Log.WARNING, retval, "Elapsed: " + (after - before) + " miliseconds.");
			return retval;
		}
		catch (Exception e) {
			Log.log(Log.ERROR, ProcessRunner.class, e);
		}
		return null;
		/*
		return Runtime.getRuntime().exec(args,env,new File(dir));
		*/ 
	}


	//{{{ getProcessRunner() method
	static ProcessRunner getProcessRunner()
	{
		if(instance == null)
		{
			if(OperatingSystem.isWindows9x())
				instance = new Windows9x();
			else if(OperatingSystem.isWindowsNT())
				instance = new WindowsNT();
			else if(OperatingSystem.isUnix())
				instance = new Unix();
			else
			{
				Log.log(Log.WARNING,ProcessRunner.class,
					"Unknown operating system");
				instance = new Generic();
			}
		}

		return instance;
	} //}}}

	private static ProcessRunner instance;

	//{{{ Generic class
	static class Generic extends ProcessRunner
	{
		boolean shellExpandsGlobs()
		{
			return true;
		}

		/*
		boolean supportsEnvironmentVariables()
		{
			return false;
		}
		*/
/*
		Hashtable getEnvironmentVariables()
		{
			return new Hashtable();
		}
*/
		boolean isCaseSensitive()
		{
			return true;
		}
	} //}}}

	//{{{ Unix class
	static class Unix extends ProcessRunner
	{
		//{{{ shellExpandsGlobs() method
		boolean shellExpandsGlobs()
		{
			return true;
		} //}}}

		//{{{ supportsEnvironmentVariables() method
		/*
		boolean supportsEnvironmentVariables()
		{
			return true;
		} //}}}
		*/
		//{{{ getEnvironmentVariables() method
		/*
		Hashtable getEnvironmentVariables()
		{
			Hashtable vars = new Hashtable();

			// run env, extract output
			try
			{
				Process env = Runtime.getRuntime().exec("env");
				BufferedReader in = new BufferedReader(
					new InputStreamReader(
					env.getInputStream()));

				String line;
				while((line = in.readLine()) != null)
				{
					Log.log(Log.DEBUG,this,line);
					int index = line.indexOf('=');
					if(index != -1)
					{
						vars.put(line.substring(0,index),
							line.substring(index + 1));
					}
				}

				in.close();
			}
			catch(IOException io)
			{
				Log.log(Log.ERROR,this,io);
			}

			return vars;
		} //}}}
        */
		//{{{ isCaseSensitive() method
		boolean isCaseSensitive()
		{
			return true;
		} //}}}
	} //}}}

	//{{{ Windows class
	abstract static class Windows extends ProcessRunner
	{
		//{{{ shellExpandsGlobs() method
		boolean shellExpandsGlobs()
		{
			return false;
		} //}}}

		//{{{ isCaseSensitive() method
		boolean isCaseSensitive()
		{
			return false;
		} //}}}
	} //}}}

	//{{{ Windows9x class
	static class Windows9x extends Windows
	{
		//{{{ supportsEnvironmentVariables() method
		/*boolean supportsEnvironmentVariables()
		{
			return false;
		} //}}}
         */
		//{{{ getEnvironmentVariables() method
		/*
		Hashtable getEnvironmentVariables()
		{
			return new Hashtable();
		} //}}}
        */
		/* //{{{ exec() method
		Process exec(String[] args, String[] env, String dir)
			throws Exception
		{
			String[] prefix = new String[] { "command.com", "/c" };
			String[] actualArgs = new String[prefix.length
				+ args.length];
			System.arraycopy(prefix,0,actualArgs,0,prefix.length);
			System.arraycopy(args,0,actualArgs,prefix.length,
				args.length);

			return super.exec(actualArgs,env,dir);
		} //}}} */

		//{{{ setUpDefaultAliases() method
		void setUpDefaultAliases(Hashtable aliases)
		{
			String[] builtins  = { "md", "rd", "del", "dir", "copy",
				"move", "erase", "mkdir", "rmdir", "start", "echo",
				"path", "ver", "vol", "ren", "type"};
			for(int i = 0; i < builtins.length; i++)
			{
				aliases.put(builtins[i],"command.com /c " + builtins[i]);
			}
		} //}}}

		//{{{ exec() method
		Process exec(String[] args, String[] env, String dir)
			throws IOException
		{
			String commandName = args[0];

			String[] extensionsToTry;
			if(commandName.indexOf('.') == -1)
				extensionsToTry = getExtensionsToTry();
			else
				extensionsToTry = new String[] { "" };

			for(int i = 0; i < extensionsToTry.length; i++)
			{
				args[0] = commandName + extensionsToTry[i];

				try
				{
//					processBuilder = new ProcessBuilder(args);
					processBuilder.directory(new File(dir));
					processBuilder.command(args);
					processBuilder.redirectErrorStream(true);
					return processBuilder.start();
					/*
					return Runtime.getRuntime().exec(args,null,new File(dir));
					*/
				}
				catch(Exception e)
				{
					if(i == extensionsToTry.length - 1)
					{
						// throw a new exception cos
						// Windows error messages are
						// a bit cryptic
						throw new RuntimeException(
							jEdit.getProperty(
							"console.shell.not-found-win",
							new String[] { commandName, }));
					}
				}
			}

			// can't happen
			return null;
		} //}}}

		//{{{ getExtensionsToTry() method
		String[] getExtensionsToTry()
		{
			return new String[] { ".exe", ".com" };
		} //}}}
	} //}}}

	//{{{ WindowsNT class
	static class WindowsNT extends Windows
	{
		//{{{ supportsEnvironmentVariables() method
		/*
		boolean supportsEnvironmentVariables()
		{
			return true;
		} //}}}
        */
		//{{{ getEnvironmentVariables() method
		/*
		Hashtable getEnvironmentVariables()
		{
			Hashtable vars = new Hashtable();

			// run env, extract output
			try
			{
				Process env = Runtime.getRuntime().exec(
					"cmd.exe /c set");
				BufferedReader in = new BufferedReader(
					new InputStreamReader(
					env.getInputStream()));

				String line;
				while((line = in.readLine()) != null)
				{
					Log.log(Log.DEBUG,this,line);
					int index = line.indexOf('=');
					if(index != -1)
					{
						vars.put(line.substring(0,index),
							line.substring(index + 1));
					}
				}

				in.close();
			}
			catch(IOException io)
			{
				Log.log(Log.ERROR,this,io);
			}

			return vars;
		} //}}}
         */
		//{{{ exec() method
/*		Process exec(String[] args, String[] env, String dir)
			throws IOException
		{
			String[] prefix = new String[] { "cmd.exe", "/c" };
			String[] actualArgs = new String[prefix.length
				+ args.length];
			System.arraycopy(prefix,0,actualArgs,0,prefix.length);
			System.arraycopy(args,0,actualArgs,prefix.length,
				args.length);

			return super.exec(actualArgs, processBuilder, dir);
		} //}}}
		*/
	} //}}}
}
