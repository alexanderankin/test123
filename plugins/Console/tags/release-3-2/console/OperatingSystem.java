/*
 * OperatingSystem.java - Abstracts away OS-specific stuff
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Slava Pestov
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
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

abstract class OperatingSystem
{
	abstract boolean shellExpandsGlobs();

	abstract boolean supportsEnvironmentVariables();

	abstract Hashtable getEnvironmentVariables();

	abstract void setUpDefaultAliases(Hashtable aliases);

	abstract Process exec(String[] args, String[] env, String dir)
			throws Exception;

	//{{{ getOperatingSystem() method
	static OperatingSystem getOperatingSystem()
	{
		if(os == null)
		{
			if(org.gjt.sp.jedit.OperatingSystem.isWindows9x())
				os = new Windows9x();
			else if(org.gjt.sp.jedit.OperatingSystem.isWindowsNT())
				os = new WindowsNT();
			else if(org.gjt.sp.jedit.OperatingSystem.isUnix())
				os = new Unix();
			else
				os = new Generic();
		}

		return os;
	} //}}}

	private static OperatingSystem os;

	//{{{ Generic class
	static class Generic extends OperatingSystem
	{
		//{{{ shellExpandsGlobs() method
		boolean shellExpandsGlobs()
		{
			return true;
		} //}}}

		//{{{ supportsEnvironmentVariables() method
		boolean supportsEnvironmentVariables()
		{
			return false;
		} //}}}

		//{{{ getEnvironmentVariables() method
		Hashtable getEnvironmentVariables()
		{
			return new Hashtable();
		} //}}}

		//{{{ setUpDefaultAliases() method
		void setUpDefaultAliases(Hashtable aliases)
		{
		} //}}}

		//{{{ exec() method
		Process exec(String[] args, String[] env, String dir)
			throws Exception
		{
			return Runtime.getRuntime().exec(args,env,new File(dir));
		} //}}}
	} //}}}

	//{{{ Unix class
	static class Unix extends Generic
	{
		//{{{ supportsEnvironmentVariables() method
		boolean supportsEnvironmentVariables()
		{
			return true;
		} //}}}

		//{{{ getEnvironmentVariables() method
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
	} //}}}

	//{{{ Windows class
	abstract static class Windows extends Generic
	{
		//{{{ shellExpandsGlobs() method
		boolean shellExpandsGlobs()
		{
			return false;
		} //}}}

		//{{{ exec() method
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
					return super.exec(args,env,dir);
				}
				catch(Exception e)
				{
					if(i == extensionsToTry.length - 1)
					{
						// throw a new exception cos
						// Windows error messages are
						// a bit cryptic
						throw new Exception(
							jEdit.getProperty(
							"console.shell.not-found-win",
							new String[] { commandName, }));
					}
				}
			}

			// can't happen
			return null;
		} //}}}

		abstract String getBuiltInPrefix();

		abstract String[] getExtensionsToTry();

		//{{{ setUpDefaultAliases() method
		void setUpDefaultAliases(Hashtable aliases)
		{
			String[] builtins  = { "md", "rd", "del", "dir", "copy",
				"move", "erase", "mkdir", "rmdir", "start", "echo",
				"path", "ver", "vol", "ren", "type"};
			for(int i = 0; i < builtins.length; i++)
			{
				aliases.put(builtins[i],getBuiltInPrefix() + builtins[i]);
			}
		} //}}}
	} //}}}

	//{{{ Windows9x class
	static class Windows9x extends Windows
	{
		//{{{ supportsEnvironmentVariables() method
		boolean supportsEnvironmentVariables()
		{
			return false;
		} //}}}

		//{{{ getBuiltInPrefix() method
		String getBuiltInPrefix()
		{
			return "command.com /c ";
		} //}}}

		//{{{ getExtensionsToTry() method
		String[] getExtensionsToTry()
		{
			return new String[] { ".cmd", ".bat", ".exe", ".com" };
		} //}}}
	} //}}}

	//{{{ WindowsNT class
	static class WindowsNT extends Windows
	{
		//{{{ supportsEnvironmentVariables() method
		boolean supportsEnvironmentVariables()
		{
			return true;
		} //}}}

		//{{{ getEnvironmentVariables() method
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
					_extensionsToTry.addElement(st.nextToken());
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
		} //}}}

		//{{{ getBuiltInPrefix() method
		String getBuiltInPrefix()
		{
			return "cmd.exe /c ";
		} //}}}

		//{{{ getExtensionsToTry() method
		String[] getExtensionsToTry()
		{
			if(extensionsToTry == null)
				getEnvironmentVariables();

			return extensionsToTry;
		} //}}}

		String[] extensionsToTry;
	} //}}}
}
