/*
 * SystemShell.java - Executes OS commands
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

class SystemShell extends Shell
{
	//{{{ SystemShell constructor
	public SystemShell()
	{
		super("System");
	} //}}}

	//{{{ printInfoMessage() method
	public void printInfoMessage(Output output)
	{
		output.print(null,jEdit.getProperty("console.shell.info"));
	} //}}}

	//{{{ execute() method
	public void execute(Console console, Output output, String command)
	{
		// comments, for possible future scripting support
		if(command.startsWith("#"))
		{
			output.commandDone();
			return;
		}

		// lazily initialize aliases and variables
		init();

		Vector args = parse(command);
		// will be null if the command is an empty string
		if(args == null)
		{
			output.commandDone();
			return;
		}

		args = preprocess(console.getView(),console,args);

		String commandName = (String)args.elementAt(0);
		if(commandName.charAt(0) == '%')
		{
			// a console built-in
			args.removeElementAt(0);
			SystemShellBuiltIn.executeBuiltIn(console,output,
				commandName.substring(1),args);
			output.commandDone();
		}
		else if(new File(MiscUtilities.constructPath(
			getConsoleState(console).currentDirectory,
			commandName)).isDirectory() && args.size() == 1)
		{
			SystemShellBuiltIn.executeBuiltIn(console,output,
				"cd",args);
			output.commandDone();
		}
		else
		{
			boolean foreground;

			if(args.elementAt(args.size() - 1).equals("&"))
			{
				// run in background
				args.removeElementAt(args.size() - 1);
				foreground = false;
			}
			else
			{
				// run in foreground
				foreground = true;
			}

			String[] _args = new String[args.size()];
			args.copyInto(_args);

			String[] env;

			if(OperatingSystem.getOperatingSystem()
				.supportsEnvironmentVariables())
			{
				env = new String[variables.size()];
				int counter = 0;
				Enumeration keys = variables.keys();
				while(keys.hasMoreElements())
				{
					Object key = keys.nextElement();
					env[counter++]= (key + "=" + variables.get(key));
				}
			}
			else
				env = null;

			new ConsoleProcess(console,output,_args,env,foreground);
		}
	} //}}}

	//{{{ stop() method
	public void stop(Console console)
	{
		ConsoleState consoleState = getConsoleState(console);
		ConsoleProcess process = consoleState.process;
		if(process != null)
			process.stop();
		else
		{
			console.print(console.getErrorColor(),
				jEdit.getProperty("console.shell.noproc"));
		}
	} //}}}

	//{{{ waitFor() method
	public boolean waitFor(Console console)
	{
		ConsoleState consoleState = getConsoleState(console);
		ConsoleProcess process = consoleState.process;
		if(process != null)
		{
			try
			{
				synchronized(process)
				{
					process.wait();
				}
			}
			catch(InterruptedException e)
			{
			}

			return process.getExitStatus();
		}
		else
			return true;
	} //}}}

	//{{{ getCompletions() method
	/**
	 * Returns possible completions for the specified command.
	 * @param command The command
	 */
	public CompletionInfo getCompletions(String command)
	{
		return null;
	} //}}}

	//{{{ Package-private members

	//{{{ consoleOpened() method
	static void consoleOpened(Console console)
	{
		consoleStateMap.put(console,new ConsoleState());
	} //}}}

	//{{{ consoleClosed() method
	static void consoleClosed(Console console)
	{
		ConsoleProcess process = getConsoleState(console).process;
		if(process != null)
			process.stop();

		consoleStateMap.remove(console);
	} //}}}

	//{{{ getConsoleState() method
	static ConsoleState getConsoleState(Console console)
	{
		return (ConsoleState)consoleStateMap.get(console);
	} //}}}

	//{{{ getAliases() method
	static Hashtable getAliases()
	{
		return aliases;
	} //}}}

	//{{{ getVariables() method
	static Hashtable getVariables()
	{
		return variables;
	} //}}}

	//{{{ propertiesChanged() method
	static void propertiesChanged()
	{
		aliases = null;
		variables = null;

		// next time execute() is called, init() will reload everything
	} //}}}

	//}}}

	//{{{ Private members

	//{{{ Instance variables
	private static Hashtable consoleStateMap = new Hashtable();
	private static final char dosSlash = 127;
	private static Hashtable aliases;
	private static Hashtable variables;
	//}}}

	//{{{ init() method
	private static void init()
	{
		if(aliases != null && variables != null)
			return;

		aliases = new Hashtable();

		// some built-ins can be invoked without the % prefix
		aliases.put("cd","%cd");
		aliases.put("pwd","%pwd");

		// load aliases from properties
		String alias;
		int i = 0;
		while((alias = jEdit.getProperty("console.shell.alias." + i)) != null)
		{
			aliases.put(alias,jEdit.getProperty("console.shell.alias."
				+ i + ".expansion"));
			i++;
		}

		OperatingSystem os = OperatingSystem.getOperatingSystem();

		os.setUpDefaultAliases(aliases);

		variables = os.getEnvironmentVariables();

		if(jEdit.getJEditHome() != null)
			variables.put("JEDIT_HOME",jEdit.getJEditHome());

		if(jEdit.getSettingsDirectory() != null)
			variables.put("JEDIT_SETTINGS",jEdit.getSettingsDirectory());

		// for the sake of Unix programs that try to be smart
		variables.put("TERM","dumb");

		// load variables from properties
		String varname;
		i = 0;
		while((varname = jEdit.getProperty("console.shell.variable." + i)) != null)
		{
			variables.put(varname,jEdit.getProperty("console.shell.variable."
				+ i + ".value"));
			i++;
		}
	} //}}}

	//{{{ parse() method
	/**
	 * Convert a command into a vector of arguments.
	 */
	private Vector parse(String command)
	{
		Vector args = new Vector();

		// We replace \ with a non-printable char because
		// StreamTokenizer handles \ specially, which causes
		// problems on Windows as \ is the file separator
		// there.

		// After parsing is done, the non printable char is
		// changed to \ once again.

		// StreamTokenizer needs a way to disable backslash
		// handling...
		command = command.replace('\\',dosSlash);

		StreamTokenizer st = new StreamTokenizer(new StringReader(command));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		try
		{
loop:			for(;;)
			{
				switch(st.nextToken())
				{
				case StreamTokenizer.TT_EOF:
					break loop;
				case StreamTokenizer.TT_WORD:
				case '"':
				case '\'':
					args.addElement(st.sval.replace(dosSlash,'\\'));
					break;
				}
			}
		}
		catch(IOException io)
		{
			// won't happen
		}

		if(args.size() == 0)
			return null;
		else
			return args;
	} //}}}

	//{{{ preprocess() method
	/**
	 * Expand aliases, variables and globs.
	 */
	private Vector preprocess(View view, Console console, Vector args)
	{
		Vector newArgs = new Vector();

		// expand aliases
		String commandName = (String)args.elementAt(0);
		String expansion = (String)aliases.get(commandName);
		if(expansion != null)
		{
			Vector expansionArgs = parse(expansion);
			for(int i = 0; i < expansionArgs.size(); i++)
			{
				expandGlobs(view,console,newArgs,(String)expansionArgs
					.elementAt(i));
			}
		}
		else
			expandGlobs(view,console,newArgs,commandName);

		// add remaining arguments
		for(int i = 1; i < args.size(); i++)
			expandGlobs(view,console,newArgs,(String)args.elementAt(i));

		return newArgs;
	} //}}}

	//{{{ expandGlobs() method
	private void expandGlobs(View view, Console console, Vector args, String arg)
	{
		// XXX: to do
		args.addElement(expandVariables(view,console,arg));
	} //}}}

	//{{{ expandVariables() method
	private String expandVariables(View view, Console console, String arg)
	{
		StringBuffer buf = new StringBuffer();

		String varName;

		for(int i = 0; i < arg.length(); i++)
		{
			char c = arg.charAt(i);
			switch(c)
			{
			case dosSlash:
				buf.append('\\');
				break;
			//{{{ DOS-style variable (%name%)
			case '%':
				int index = arg.indexOf('%',i + 1);
				if(index != -1)
				{
					if(index == i + 1)
					{
						// %%
						break;
					}

					varName = arg.substring(i + 1,index);

					i = index;

					String expansion = getExpansion(view,console,varName);

					if(expansion != null)
						buf.append(expansion);
				}
				else
					buf.append('%');

				break;
			//}}}
			//{{{ Unix-style variables ($name, ${name})
			case '$':
				if(i == arg.length() - 1)
				{
					buf.append(c);
					break;
				}

				if(arg.charAt(i + 1) == '{')
				{
					index = arg.indexOf('}',i + 1);
					if(index == -1)
						index = arg.length();
					varName = arg.substring(i + 2,index);

					i = index;
				}
				else
				{
					for(index = i + 1; index < arg.length(); index++)
					{
						char ch = arg.charAt(index);
						if(!Character.isLetterOrDigit(ch)
							&& ch != '_' && ch != '$')
						{
							break;
						}
					}

					varName = arg.substring(i + 1,index);

					i = index - 1;

					if(varName.startsWith("$"))
					{
						buf.append(varName);
						break;
					}
					else if(varName.length() == 0)
						break;
				}

				String expansion = getExpansion(view,console,varName);

				if(expansion != null)
					buf.append(expansion);

				break;
			//}}}
			//{{{ Home directory (~)
			case '~':
				String home = System.getProperty("user.home");

				if(arg.length() == 1)
				{
					buf.append(home);
					break;
				}
				if(i != 0)
				{
					c = arg.charAt(i - 1);
					if(c == '/' || c == File.separatorChar)
					{
						buf.append(home);
						break;
					}
				}
				if(i != arg.length() - 1)
				{
					c = arg.charAt(i + 1);
					if(c == '/' || c == File.separatorChar)
					{
						buf.append(home);
						break;
					}
				}
				buf.append('~');
				break;
			//}}}
			default:
				buf.append(c);
				break;
			}
		}

		return buf.toString();
	} //}}}

	//{{{ getExpansion() method
	private String getExpansion(View view, Console console, String varName)
	{
		String expansion;

		// Expand some special variables
		Buffer buffer = view.getBuffer();

		if(varName.equals("$") || varName.equals("%"))
			expansion = varName;
		else if(varName.equals("d"))
		{
			expansion = MiscUtilities.getParentOfPath(
				buffer.getPath());
			if(expansion.endsWith("/")
				|| expansion.endsWith(File.separator))
			{
				expansion = expansion.substring(0,
					expansion.length() - 1);
			}
		}
		else if(varName.equals("u"))
		{
			expansion = buffer.getPath();
			if(!MiscUtilities.isURL(expansion))
			{
				expansion = "file:/" + expansion
					.replace(File.separatorChar,'/');
			}
		}
		else if(varName.equals("f"))
			expansion = buffer.getPath();
		else if(varName.equals("n"))
			expansion = buffer.getName();
		else if(varName.equals("c"))
			expansion = ConsolePlugin.getClassName(buffer);
		else if(varName.equals("PKG"))
		{
			expansion = ConsolePlugin.getPackageName(buffer);
			if(expansion == null)
				expansion = "";
		}
		else if(varName.equals("ROOT"))
			expansion = ConsolePlugin.getPackageRoot(buffer);
		else if(varName.equals("PWD"))
			expansion = getConsoleState(console).currentDirectory;
		else if(varName.equals("BROWSER_DIR"))
		{
			VFSBrowser browser = (VFSBrowser)view
				.getDockableWindowManager()
				.getDockable("vfs.browser");
			if(browser == null)
				expansion = null;
			else
				expansion = browser.getDirectory();
		}
		else
			expansion = (String)variables.get(varName);

		return expansion;
	} //}}}

	//}}}

	//{{{ ConsoleState class
	static class ConsoleState
	{
		String currentDirectory = System.getProperty("user.dir");
		Stack directoryStack = new Stack();
		ConsoleProcess process;

		void setCurrentDirectory(Console console, String newDir)
		{
			String[] pp = { newDir };
			if(new File(newDir).exists())
			{
				currentDirectory = newDir;
				console.print(console.getInfoColor(),
					jEdit.getProperty(
					"console.shell.cd.ok",pp));
			}
			else
			{
				console.print(console.getErrorColor(),
					jEdit.getProperty(
					"console.shell.cd.error",pp));
			}
		}
	} //}}}
}
