/*
 * OperatingSystem.java - Abstracts away OS-specific stuff
 * Copyright (C) 2001 Slava Pestov
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

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.util.Log;

abstract class OperatingSystem
{
	abstract String[] getExtensionsToTry();

	abstract boolean shellExpandsGlobs();

	abstract boolean supportsEnvironmentVariables();

	abstract Hashtable getEnvironmentVariables();

	abstract void setUpDefaultAliases(Hashtable aliases);

	abstract boolean cdCommandAvailable();

	abstract Process exec(String[] args, String[] env, String dir)
			throws Exception;

	static OperatingSystem getOperatingSystem()
	{
		if(os == null)
		{
			String osName = System.getProperty("os.name");
			if(osName.startsWith("Windows 9"))
				os = new Windows9x();
			else if(osName.startsWith("Windows"))
				os = new WindowsNT();
			else if(File.separatorChar == '/'
				&& (osName.indexOf("MacOS") == -1
				|| osName.indexOf("MacOS X") != -1))
				os = new Unix();
			else
				os = new Generic();
		}

		return os;
	}

	private static OperatingSystem os;

	static class Generic extends OperatingSystem
	{
		Generic()
		{
			try
			{
				Class[] classes = { String[].class, String[].class, File.class };
				java13exec = Runtime.class.getMethod("exec",classes);
			}
			catch(Exception e)
			{
				// use Java 1.1/1.2 code instead
			}
		}

		String[] getExtensionsToTry()
		{
			return new String[] { "" };
		}

		boolean shellExpandsGlobs()
		{
			return true;
		}

		boolean supportsEnvironmentVariables()
		{
			return false;
		}

		Hashtable getEnvironmentVariables()
		{
			return new Hashtable();
		}

		void setUpDefaultAliases(Hashtable aliases)
		{
		}

		boolean cdCommandAvailable()
		{
			return java13exec != null;
		}

		Process exec(String[] args, String[] env, String dir)
			throws Exception
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
					if(java13exec != null)
					{
						Object[] methodArgs = { args,env,
							new File(dir) };
						return (Process)java13exec.invoke(
							Runtime.getRuntime(),methodArgs);
					}
					else
					{
						return Runtime.getRuntime().exec(args,env);
					}
				}
				catch(InvocationTargetException ite)
				{
					if(i == extensionsToTry.length - 1)
						throw (Exception)e.getTargetException();
				}
				catch(Exception e)
				{
					if(i == extensionsToTry.length - 1)
						throw e;
				}
			}

			// can't happen
			return null;
		}

		private Method java13exec;
	}

	static class Unix extends Generic
	{
		boolean supportsEnvironmentVariables()
		{
			return true;
		}

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
		}
	}

	abstract static class Windows extends Generic
	{
		String[] getExtensionsToTry()
		{
			if(extensionsToTry == null)
				getEnvironmentVariables();

			return extensionsToTry;
		}

		boolean shellExpandsGlobs()
		{
			return false;
		}

		boolean supportsEnvironmentVariables()
		{
			return true;
		}

		Hashtable getEnvironmentVariables()
		{
			Hashtable vars = new Hashtable();

			// run env, extract output
			try
			{
				Process env = Runtime.getRuntime().exec(
					getBuiltInPrefix() + "set");
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

			String pathext = (String)vars.get("PATHEXT");
			if(pathext != null)
			{
				Vector _extensionsToTry = new Vector();

				StringTokenizer st = new StringTokenizer(pathext,"; ");
				while(st.hasMoreTokens())
				{
					_extensionsToTry.addElement(
						'.' + st.nextToken());
				}

				extensionsToTry = new String[_extensionsToTry.size()];
				_extensionsToTry.copyInto(extensionsToTry);
			}
			else
			{
				extensionsToTry = new String[] { ".cmd",
					".bat", ".exe", ".com" };
			}

			return vars;
		}

		abstract String getBuiltInPrefix();

		void setUpDefaultAliases(Hashtable aliases)
		{
			String[] builtins  = { "md", "rd", "del", "dir", "copy",
				"move", "erase", "mkdir", "rmdir", "start",
				"path", "ver", "vol", "ren", "type"};
			for(int i = 0; i < builtins.length; i++)
			{
				aliases.put(builtins[i],getBuiltInPrefix() + builtins[i]);
			}
		}

		private String[] extensionsToTry;
	}

	static class Windows9x extends Windows
	{
		String getBuiltInPrefix()
		{
			return "command.com /c ";
		}
	}

	static class WindowsNT extends Windows
	{
		String getBuiltInPrefix()
		{
			return "cmd.exe /c ";
		}
	}
}
