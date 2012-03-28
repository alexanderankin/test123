/*
 * SystemShellBuiltIn.java - Commands handled by system shell itself
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

//{{{ Imports

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.help.HelpViewer;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;
import org.gjt.sp.util.StandardUtilities.StringCompare;

import console.SystemShell.ConsoleState;
//}}}

public abstract class SystemShellBuiltIn
{
	//{{{ SystemShellBuiltIn constructor
	public SystemShellBuiltIn()
	{
		name = getClass().getName();
		name = name.substring(name.lastIndexOf('$') + 1);

		help = jEdit.getProperty("console.shell." + name + ".usage");
		if(help == null)
			Log.log(Log.WARNING,this,name + " is missing usage info");
	} //}}}

	//{{{ getOptions() method
	public Option[] getOptions()
	{
		return new Option[0];
	} //}}}

	//{{{ getConsoleState() method
	private static SystemShell.ConsoleState getConsoleState(Console console)
	{
		return ConsolePlugin.getSystemShell().getConsoleState(console);
	} //}}}

	//{{{ Option class
	public class Option
	{
		public char shortName;
		public String longName;
		public boolean takesArgument;

		public Option(char shortName, String longName, boolean takesArgument)
		{
			this.shortName = shortName;
			this.longName = longName;
			this.takesArgument = takesArgument;
		}
	} //}}}

	//{{{ getMinArguments() method
	public int getMinArguments()
	{
		return 0;
	} //}}}

	//{{{ getMaxArgument() method
	public int getMaxArguments()
	{
		// meaning, no maximum
		return -1;
	} //}}}

	//{{{ execute() method
	/** 
	 * Used by executeBuiltIn
	 */
	public void execute(Console console, Output output, Output error, 
		Vector<String> args)
	{
		Hashtable<String, Object> values = new Hashtable<String, Object>();
		Option[] options = getOptions();

		for(int i = 0; i < args.size(); i++)
		{
			String arg = args.elementAt(i);

			//{{{ end of options
			if(arg.equals("--"))
			{
				args.removeElementAt(i);
				break;
			}
			else if(arg.equals("--help"))
			{
				error.print(null,help);
				return;
			} //}}}
			//{{{ long option
			else if(arg.startsWith("--"))
			{
				if(arg.length() == 2)
					continue;

				args.removeElementAt(i);
				i--;

				String longName = arg.substring(2);
				boolean no;
				if(longName.startsWith("no-"))
				{
					no = true;
					longName = longName.substring(3);
				}
				else
					no = false;

				Option option = null;
				for(int j = 0; j < options.length; j++)
				{
					if(options[j].longName.equals(longName))
					{
						option = options[j];
						break;
					}
				}

				if(option == null)
				{
					String[] pp = { longName };
					error.print(console.getErrorColor(),
						jEdit.getProperty("console.shell.bad-opt-long",pp));
					return;
				}

				if(option.takesArgument)
				{
					if(no)
					{
						String[] pp = { longName };
						error.print(console.getErrorColor(),
							jEdit.getProperty("console.shell.no-arg-long",pp));
						return;
					}

					if(i == args.size() - 1)
					{
						String[] pp = { longName };
						error.print(console.getErrorColor(),
							jEdit.getProperty("console.shell.need-arg-long",pp));
						return;
					}

					values.put(longName,args.elementAt(i + 1));
					args.removeElementAt(i + 1);
				}
				else
				{
					if(no)
						values.put(longName,Boolean.FALSE);
					else
						values.put(longName,Boolean.TRUE);
				}
			} //}}}
			//{{{ short option
			else if(arg.startsWith("-") || arg.startsWith("+"))
			{
				if(arg.length() == 1)
					continue;

				args.removeElementAt(i);
				i--;

				boolean no = (arg.charAt(0) == '+');

				for(int j = 1; j < arg.length(); j++)
				{
					char shortName = arg.charAt(j);

					Option option = null;
					for(int k = 0; k < options.length; k++)
					{
						if(options[k].shortName == shortName)
						{
							option = options[k];
							break;
						}
					}

					if(option == null)
					{
						String[] pp = { String.valueOf(shortName) };
						error.print(console.getErrorColor(),
							jEdit.getProperty("console.shell.bad-opt",pp));
						return;
					}

					if(no)
						values.put(option.longName,Boolean.FALSE);
					else
						values.put(option.longName,Boolean.TRUE);
				}
			} //}}}
		}

		int min = getMinArguments();
		int max = getMaxArguments();
		if(args.size() < min
			|| (max != -1 && args.size() > max))
		{
			error.print(console.getErrorColor(),
				jEdit.getProperty("console.shell.bad-args"));
			return;
		}

		execute(console,output,error,args,values);
	} //}}}

	//{{{ execute() method
	protected abstract void execute(Console console, Output output,
		Output error, Vector<String> args, Hashtable<String, Object> values); //}}}

	//}}}

	//{{{ Protected members
	protected String name;
	protected String help;
	//}}}

	//{{{ Inner classes

	//{{{ alias class
	static class alias extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 2;
		}

		public int getMaxArguments()
		{
			return 2;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			Hashtable<String,String> aliases = ConsolePlugin.getSystemShell()
				.getAliases();
			aliases.put(args.elementAt(0),args.elementAt(1));
		}
	} //}}}

	//{{{ aliases class
	static class aliases extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 0;
		}

		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			Hashtable<String, String> aliases = ConsolePlugin.getSystemShell().getAliases();
			StringList returnValue = new StringList();
			for (Map.Entry<String,String> ent: aliases.entrySet()) 
			{
				returnValue.add(ent.getKey() + "=" + ent.getValue());
			}

			Collections.sort(returnValue, new StringCompare<String>(true));
			for(int i = 0; i < returnValue.size(); i++)
			{
				output.print(null,returnValue.get(i));
			}
		}
	} //}}}

	//{{{ browse class
	static class browse extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 1;
		}

		public Option[] getOptions()
		{
			return new Option[] {
				new Option('n',"new-window",false)
			};
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			String currentDirectory = getConsoleState(
				console).currentDirectory;

			String directory = (args.size() == 0
				? currentDirectory
				: MiscUtilities.constructPath(currentDirectory,
				(String)args.elementAt(0)));

			if(values.get("new-window") != null)
				VFSBrowser.browseDirectoryInNewWindow(console.getView(),directory);
			else
				VFSBrowser.browseDirectory(console.getView(),directory);
		}
	} //}}}

	//{{{ cd class
	static class cd extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			SystemShell.ConsoleState state = getConsoleState(console);

			if(args.size() == 0)
			{
				state.setCurrentDirectory(console,
					System.getProperty("user.home"));
			}
			else
			{
				String arg = (String)args.elementAt(0);
				if(arg.equals("-"))
					state.gotoLastDirectory(console);
				else
				{
					state.setCurrentDirectory(console,
						MiscUtilities.constructPath(
						state.currentDirectory, arg));
					if(OperatingSystem.isWindows()) {
						int colonPos = arg.indexOf(":");
						if (colonPos > 0) {
							char driveLetter = arg.charAt(0);
							args = state.changeDrive(driveLetter);
						}
					}					
				}
			}
		}
	} //}}}

	//{{{ clear class
	static class clear extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 0;
		}

		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			console.clear();
		}
	} //}}}

	//{{{ dirstack class
	static class dirstack extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			Stack<String> directoryStack = getConsoleState(console)
				.directoryStack;

			for(int i = 0; i < directoryStack.size(); i++)
			{
				output.print(null,(String)directoryStack.elementAt(i));
			}
		}
	} //}}}

	//{{{ echo class
	static class echo extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 1;
		}

		public int getMaxArguments()
		{
			return -1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			for(int i = 0; i < args.size(); i++)
			{
				output.print(null,(String)args.elementAt(i));
			}
		}
	} //}}}

	//{{{ edit class
	static class edit extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			String currentDirectory = getConsoleState(
				console).currentDirectory;

			for(int i = 0; i < args.size(); i++)
			{
				jEdit.openFile(console.getView(),currentDirectory,
					(String)args.elementAt(i),false,null);
			}
		}
	} //}}}

	//{{{ env class
	static class env extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 0;
		}

		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			Map<String, String> variables = ConsolePlugin.getSystemShell().getVariables();
			StringList returnValue = new StringList();
			for (Map.Entry<String,String> ent: variables.entrySet()) 
			{
				returnValue.add(ent.getKey() + "=" + ent.getValue());
			}
			
			Collections.sort(returnValue, new StringCompare<String>(true));

			for(int i = 0; i < returnValue.size(); i++)
			{
				output.print(null,returnValue.get(i));
			}
		}
	} //}}}

	//{{{ help class
	static class help extends SystemShellBuiltIn
	{
		static String HELP_PATH = "jeditresource:/Console.jar!/index.html";

		public int getMinArguments()
		{
			return 0;
		}

		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			if(args.size() == 1)
			{
				String cmd = (String)args.get(0);
				if(cmd.startsWith("%"))
					cmd = cmd.substring(1);
				String help = jEdit.getProperty("console.shell."
					+ cmd + ".usage");
				// if command name specified, print its usage
				if(help != null)
					error.print(null,help);
				else {
					ActionContext ac = jEdit.getActionContext();
					EditAction ea = ac.getAction("help");
					ea.invoke(jEdit.getActiveView());
				}
			}
			else
				new HelpViewer(HELP_PATH);
		}
	} //}}}

	//{{{ kill class
	static class kill extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 1;
		}

		/**
		 * @deprecated
		 */
		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			SystemShell.ConsoleState state = getConsoleState(console);

			ConsoleProcess process = state.getProcess();
			if(process == null)
			{
				error.print(console.getErrorColor(),
					jEdit.getProperty("console.shell.noproc"));
				return;
			}

			// process.stop();
		}
	} //}}}

	//{{{ popd class
	static class popd extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			SystemShell.ConsoleState state = getConsoleState(console);
			Stack<String> directoryStack = state.directoryStack;

			if(directoryStack.isEmpty())
			{
				error.print(console.getErrorColor(),
					jEdit.getProperty("console.shell.popd.error"));
				return;
			}

			String newDir = (String)directoryStack.pop();
			state.setCurrentDirectory(console,newDir);
		}
	} //}}}

	//{{{ pushd class
	static class pushd extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			SystemShell.ConsoleState state = getConsoleState(console);
			Stack<String> directoryStack = state.directoryStack;

			directoryStack.push(state.currentDirectory);
			if (args.size() > 0) {
				state.setCurrentDirectory(console, args.get(0).toString());
			}

			String[] pp = { state.currentDirectory };
			error.print(null,jEdit.getProperty("console.shell.pushd.ok",pp));
		}
	} //}}}

	//{{{ pwd class
	static class pwd extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 0;
		}

		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			ConsoleState cs = getConsoleState(console);
			
			output.writeAttrs(null, cs.currentDirectory + "\n");
			// shell will print prompt with current working dir
		}
	} //}}}

	//{{{ run class
	static class run extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			String currentDirectory = getConsoleState(
				console).currentDirectory;

			for(int i = 0; i < args.size(); i++)
			{
				Macros.runScript(console.getView(),
					MiscUtilities.constructPath(
					currentDirectory,
					(String)args.get(i)),
					false);
			}
		}
	} //}}}

	//{{{ set class
	static class set extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 2;
		}

		public int getMaxArguments()
		{
			return 2;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			Map<String, String> variables = ConsolePlugin.getSystemShell().getVariables();
			variables.put(args.elementAt(0),args.elementAt(1));
		}
	} //}}}

	//{{{ unalias
	static class unalias extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 1;
		}

		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			Hashtable<String, String> aliases = ConsolePlugin.getSystemShell().getAliases();
			aliases.remove(args.elementAt(0));
		}
	} //}}}

	//{{{ unset class
	static class unset extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 1;
		}

		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{
			Map<String, String> variables = ConsolePlugin.getSystemShell().getVariables();
			variables.remove(args.elementAt(0));
		}
	} //}}}

	//{{{ version class
	static class version extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 0;
		}

		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output,
			Output error, Vector<String> args, Hashtable<String, Object> values)
		{		
			output.print(null,"jEdit " + jEdit.getBuild() 
				+ " Console " + jEdit.getProperty("plugin.console.ConsolePlugin.version"));
		}
	} //}}}

	//}}}
}
