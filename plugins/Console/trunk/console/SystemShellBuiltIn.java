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
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.HelpViewer;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.*;
//}}}

public abstract class SystemShellBuiltIn
{
	//{{{ executeBuiltIn() method
	public static void executeBuiltIn(Console console, Output output,
		String command, Vector args)
	{
		SystemShellBuiltIn builtIn = (SystemShellBuiltIn)commands.get(command);
		if(builtIn == null)
		{
			String[] pp = { command };
			console.print(console.getErrorColor(),jEdit.getProperty(
				"console.shell.unknown-builtin",pp));
		}
		else
		{
			builtIn.execute(console,output,command,args);
		}
	} //}}}

	//{{{ getOptions() method
	public Option[] getOptions()
	{
		return new Option[0];
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
	public void execute(Console console, Output output, String command, Vector args)
	{
		Hashtable values = new Hashtable();
		Option[] options = getOptions();

		for(int i = 0; i < args.size(); i++)
		{
			String arg = (String)args.elementAt(i);

			//{{{ end of options
			if(arg.equals("--"))
				break;
			else if(arg.equals("--help"))
			{
				console.print(null,jEdit.getProperty("console.shell."
					+ command + ".usage"));
				return;
			} //}}}
			//{{{ long option
			else if(arg.startsWith("--"))
			{
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
					console.print(console.getErrorColor(),
						jEdit.getProperty("console.shell.bad-opt-long",pp));
					return;
				}

				if(option.takesArgument)
				{
					if(no)
					{
						String[] pp = { longName };
						console.print(console.getErrorColor(),
							jEdit.getProperty("console.shell.no-arg-long",pp));
						return;
					}

					if(i == args.size() - 1)
					{
						String[] pp = { longName };
						console.print(console.getErrorColor(),
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
						console.print(console.getErrorColor(),
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
		if(args.size() < getMinArguments()
			|| (max != -1 && args.size() > getMaxArguments()))
		{
			console.print(console.getErrorColor(),
				jEdit.getProperty("console.shell.bad-args"));
			return;
		}

		execute(console,output,args,values);
	} //}}}

	//{{{ execute() method
	protected abstract void execute(Console console, Output output,
		Vector args, Hashtable values); //}}}

	//{{{ Private members
	private static Hashtable commands;

	//{{{ Class initializer
	static
	{
		commands = new Hashtable();
		commands.put("alias", new alias());
		commands.put("aliases", new aliases());
		commands.put("browse", new browse());
		/* commands.put("cat", new cat()); */
		commands.put("cd", new cd());
		commands.put("clear", new clear());
		commands.put("dirstack", new dirstack());
		commands.put("detach", new detach());
		commands.put("echo", new echo());
		commands.put("edit", new edit());
		commands.put("env", new env());
		commands.put("help", new help());
		commands.put("kill", new kill());
		/* commands.put("ls", new ls()); */
		commands.put("popd", new popd());
		commands.put("pushd", new pushd());
		commands.put("pwd", new pwd());
		commands.put("run", new run());
		commands.put("set", new set());
		/* commands.put("touch", new touch()); */
		commands.put("unalias", new unalias());
		commands.put("unset", new unset());
		commands.put("version", new version());
	} //}}}

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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			Hashtable aliases = SystemShell.getAliases();
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			Hashtable aliases = SystemShell.getAliases();
			Vector returnValue = new Vector();
			Enumeration keys = aliases.keys();
			while(keys.hasMoreElements())
			{
				Object key = keys.nextElement();
				returnValue.addElement(key + "=" + aliases.get(key));
			}

			MiscUtilities.quicksort(returnValue,
				new MiscUtilities.StringICaseCompare());
			for(int i = 0; i < returnValue.size(); i++)
			{
				output.print(null,(String)returnValue.elementAt(i));
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			String currentDirectory = SystemShell.getConsoleState(
				console).currentDirectory;

			final String directory = (args.size() == 0
				? currentDirectory
				: MiscUtilities.constructPath(currentDirectory,
				(String)args.elementAt(0)));

			final DockableWindowManager dwm = console.getView()
				.getDockableWindowManager();
			dwm.addDockableWindow("vfs.browser");

			VFSManager.runInAWTThread(new Runnable()
			{
				public void run()
				{
					((VFSBrowser)dwm.getDockable("vfs.browser"))
						.setDirectory(directory);
				}
			});
		}
	} //}}}

	//{{{ cd class
	static class cd extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			SystemShell.ConsoleState state = SystemShell.getConsoleState(console);

			String newDir;
			if(args.size() == 0)
				newDir = System.getProperty("user.home");
			else
			{
				newDir = MiscUtilities.constructPath(
					state.currentDirectory,
					(String)args.elementAt(0));
			}

			state.setCurrentDirectory(console,newDir);
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			console.getOutputPane().setText("");
		}
	} //}}}

	//{{{ detach class
	static class detach extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			SystemShell.ConsoleState state = SystemShell.getConsoleState(console);

			ConsoleProcess process = state.process;
			if(process == null)
			{
				console.print(console.getErrorColor(),
					jEdit.getProperty("console.shell.noproc"));
				return;
			}

			process.detach();
		}
	} //}}}

	//{{{ dirstack class
	static class dirstack extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			Stack directoryStack = SystemShell.getConsoleState(console)
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			String currentDirectory = SystemShell.getConsoleState(
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			Hashtable variables = SystemShell.getVariables();
			Vector returnValue = new Vector();
			Enumeration keys = variables.keys();
			while(keys.hasMoreElements())
			{
				Object key = keys.nextElement();
				returnValue.addElement(key + "=" + variables.get(key));
			}

			MiscUtilities.quicksort(returnValue,
				new MiscUtilities.StringICaseCompare());
			for(int i = 0; i < returnValue.size(); i++)
			{
				output.print(null,(String)returnValue.elementAt(i));
			}
		}
	} //}}}

	//{{{ help class
	static class help extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 0;
		}

		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			new HelpViewer(getClass().getResource("/console/Console.html").toString());
		}
	} //}}}

	//{{{ kill class
	static class kill extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			SystemShell.ConsoleState state = SystemShell.getConsoleState(console);

			ConsoleProcess process = state.process;
			if(process == null)
			{
				console.print(console.getErrorColor(),
					jEdit.getProperty("console.shell.noproc"));
				return;
			}

			process.stop();
		}
	} //}}}

	//{{{ popd class
	static class popd extends SystemShellBuiltIn
	{
		public int getMaxArguments()
		{
			return 0;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			SystemShell.ConsoleState state = SystemShell.getConsoleState(console);
			Stack directoryStack = state.directoryStack;

			if(directoryStack.isEmpty())
			{
				console.print(console.getErrorColor(),
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
			return 0;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			SystemShell.ConsoleState state = SystemShell.getConsoleState(console);
			Stack directoryStack = state.directoryStack;

			directoryStack.push(state.currentDirectory);

			String[] pp = { state.currentDirectory };
			console.print(null,jEdit.getProperty("console.shell.pushd.ok",pp));
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			output.print(null,SystemShell.getConsoleState(console)
				.currentDirectory);
		}
	} //}}}

	//{{{ run class
	static class run extends SystemShellBuiltIn
	{
		public int getMinArguments()
		{
			return 1;
		}

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			String currentDirectory = SystemShell.getConsoleState(
				console).currentDirectory;

			for(int i = 0; i < args.size(); i++)
			{
				BeanShell.runScript(console.getView(),
					MiscUtilities.constructPath(
					currentDirectory,
					(String)args.elementAt(i)),
					true,false);
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			Hashtable variables = SystemShell.getVariables();
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			Hashtable aliases = SystemShell.getAliases();
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			Hashtable variables = SystemShell.getVariables();
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

		public void execute(Console console, Output output, Vector args,
			Hashtable values)
		{
			output.print(null,jEdit.getProperty(
				"plugin.console.ConsolePlugin.version"));
		}
	} //}}}

	//}}}
}
