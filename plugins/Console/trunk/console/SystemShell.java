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

			if(ProcessRunner.getProcessRunner()
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
	 * @param view The current view
	 * @param currentDirectory The current directory
	 * @param command The comamnd
	 */
	public CompletionInfo getCompletions(View view, String currentDirectory,
		String command)
	{
		// lazily initialize aliases and variables
		init();

		final String fileDelimiters = "=\'\" \\"+File.pathSeparator;

		String lastArgEscaped, lastArg;
		if (File.separatorChar == '\\')
		{
			// Escaping impossible
			lastArgEscaped = (String)parse(command).lastElement();
			lastArg = lastArgEscaped;
		}
		else
		{
			// Escaping possible

			// I use findLastArgument and then unescape instead of
			// (String)parse(command).lastElement() because there's no way
			// to get parse(String) to also return the original length
			// of the unescaped argument, which we need to calculate the
			// completion offset.

			lastArgEscaped = findLastArgument(command, fileDelimiters);
			lastArg = unescape(lastArgEscaped, fileDelimiters);
		}

		String [] commandCompletion = getCommandCompletions(lastArg);
		String [] fileCompletion = getFileCompletions(view,
			currentDirectory,lastArg);

		CompletionInfo completionInfo = new CompletionInfo();

		completionInfo.offset = command.length() - lastArg.length();

		// Count the amount of completions
		int completionsCount = 0;
		completionsCount += commandCompletion == null ? 0 : commandCompletion.length;
		completionsCount += fileCompletion == null ? 0 : fileCompletion.length;
		if(completionsCount == 0)
			return null;

		completionInfo.completions = new String[completionsCount];


		int offset = 0;

		// Add command completions
		if((commandCompletion != null) && (commandCompletion.length > 0))
		{
			int amount = commandCompletion.length;
			System.arraycopy(commandCompletion, 0, completionInfo.completions, offset, amount);
			offset += amount;
		}

		// Add file completions
		if((fileCompletion != null) && (fileCompletion.length > 0))
		{
			int amount = fileCompletion.length;
			System.arraycopy(fileCompletion, 0, completionInfo.completions, offset, amount);
			offset += amount;
		}

		// On systems where the file separator is the same as the escape
		// character (Windows), it's impossible to do escaping properly,
		// so we just assume that escaping is not needed (which is true
		// for windows).
		if(File.separatorChar != '\\')
		{
			for(int i = 0; i < completionsCount; i++)
			{
				completionInfo.completions[i] = escape(completionInfo.completions[i], fileDelimiters);
			}
		}


		// We add a double quote at the beginning of any completion with
		// special characters because the current argument parsing
		// (done in parse()) uses StreamTokenizer, which only handles
		// escaping if it's done within a string. The purpose here is
		// dual - to get parse() to unescape the characters and to get
		// it to recognize the string as a single argument.
		// On systems where we don't support escaping, we do this
		// only for the 2nd purpose.
		boolean isDoubleQuoted = (completionInfo.offset > 0) && (command.charAt(completionInfo.offset - 1) == '\"');
		if(!isDoubleQuoted)
		{
			final String specialCharacters = (File.separatorChar == '\\') ? " " : fileDelimiters;
			for(int i = 0; i < completionsCount; i++){
				String result = completionInfo.completions[i];
				if (containsCharacters(result, specialCharacters))
					result = "\"" + result;
				completionInfo.completions[i] = result;
			}
		}

		return completionInfo;
	} //}}}

	//{{{ expandVariables() method
	public String expandVariables(View view, String arg)
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

					String expansion = getVariableValue(view,varName);

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

				String expansion = getVariableValue(view,varName);

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

	//{{{ getVariableValue() method
	public String getVariableValue(View view, String varName)
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
		init();
		return aliases;
	} //}}}

	//{{{ getVariables() method
	static Hashtable getVariables()
	{
		init();
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
		aliases.put("-","%cd -");

		// load aliases from properties
		String alias;
		int i = 0;
		while((alias = jEdit.getProperty("console.shell.alias." + i)) != null)
		{
			aliases.put(alias,jEdit.getProperty("console.shell.alias."
				+ i + ".expansion"));
			i++;
		}

		ProcessRunner osSupport = ProcessRunner.getProcessRunner();

		osSupport.setUpDefaultAliases(aliases);

		variables = osSupport.getEnvironmentVariables();

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
		if (File.separatorChar == '\\')
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
					if (File.separatorChar == '\\')
						args.addElement(st.sval.replace(dosSlash,'\\'));
					else
						args.addElement(st.sval);
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
				expandGlobs(view,newArgs,(String)expansionArgs
					.elementAt(i));
			}
		}
		else
			expandGlobs(view,newArgs,commandName);

		// add remaining arguments
		for(int i = 1; i < args.size(); i++)
			expandGlobs(view,newArgs,(String)args.elementAt(i));

		return newArgs;
	} //}}}

	//{{{ expandGlobs() method
	private void expandGlobs(View view, Vector args, String arg)
	{
		// XXX: to do
		args.addElement(expandVariables(view,arg));
	} //}}}

	//{{{ getFileCompletions() method
	private String [] getFileCompletions(View view, String currentDirName,
		String typedFilename)
	{
		int lastSeparatorIndex = typedFilename.lastIndexOf(File.separator);

		// The directory part of what the user typed, including the file separator.
		String typedDirName = lastSeparatorIndex == -1 ? "" : typedFilename.substring(0, lastSeparatorIndex+1);

		String expandedTypedFilename = expandVariables(view, typedFilename);

		// The file typed by the user.
		File typedFile = new File(expandedTypedFilename).isAbsolute() ?
				new File(expandedTypedFilename) :
				new File(currentDirName, expandedTypedFilename);

		boolean directory = expandedTypedFilename.endsWith(File.separator)
			|| expandedTypedFilename.length() == 0;

		// The parent directory of the file typed by the user (or itself if it's already a directory).
		File dir = directory ? typedFile : typedFile.getParentFile();

		// The filename part of the file typed by the user, or "" if it's a directory.
		String fileName = directory ? "" : typedFile.getName();

		// The list of files we're going to try to match
		String [] filenames = dir.list();

		if ((filenames == null) || (filenames.length == 0))
			return null;

		boolean isOSCaseSensitive = ProcessRunner.getProcessRunner().isCaseSensitive();
		String [] matchingFilenames = new String[filenames.length];
		int matchingFilenamesCount = 0;
		String matchedString = isOSCaseSensitive ? fileName : fileName.toLowerCase();
		for(int i = 0; i < filenames.length; i++)
		{
			String matchedAgainst = isOSCaseSensitive ? filenames[i] : filenames[i].toLowerCase();

			if(matchedAgainst.startsWith(matchedString))
			{
				String match;

				File matchFile = new File(dir, filenames[i]);

				match = typedDirName + filenames[i];

				// Add a separator at the end if it's a directory
				if(matchFile.isDirectory() && !match.endsWith(File.separator))
					match = match + File.separator;

				matchingFilenames[matchingFilenamesCount++] = match;
			}
		}

		String [] result = new String[matchingFilenamesCount];
		System.arraycopy(matchingFilenames, 0, result, 0, matchingFilenamesCount);

		return result.length == 0 ? null : result;
	} //}}}

	//{{{ getCommandCompletions() method
	private String[] getCommandCompletions(String command)
	{
		 return null;
	} //}}}

	//{{{ findLastArgument() method
	/**
	 * Returns the last argument in the given command by using the given
	 * delimiters. The delimiters can be escaped.
	 */
	private static String findLastArgument(String command, String delimiters)
	{
		int i = command.length() - 1;
		while(i >= 0)
		{
			char c = command.charAt(i);
			if(delimiters.indexOf(c) != -1)
			{
				if((i == 0) || (command.charAt(i - 1) != '\\'))
					break;
				else
					i--;
			}
			i--;
		}

		return command.substring(i+1);
	} //}}}

	//{{{ unescape() method
	/**
	 * Unescapes the given delimiters in the given string.
	 */
	private static String unescape(String s, String delimiters)
	{
		StringBuffer buf = new StringBuffer(s.length());
		int i = s.length() - 1;
		while(i >= 0)
		{
			char c = s.charAt(i);
			buf.append(c);
			if(delimiters.indexOf(c) != -1)
			{
				if(s.charAt(i - 1) == '\\')
					i--;
			}
			i--;
		}

		return buf.reverse().toString();
	} //}}}

	//{{{ escape() method
	/**
	 * Escapes the given delimiters in the given string.
	 */
	private static String escape(String s, String delimiters)
	{
		StringBuffer buf = new StringBuffer();
		int length = s.length();
		for(int i = 0; i < length; i++)
		{
			char c = s.charAt(i);
			if (delimiters.indexOf(c) != -1)
				buf.append('\\');
			buf.append(c);
		}

		return buf.toString();
	} //}}}

	//{{{ containsWhitespace() method
	/**
	 * Returns <code>true</code> if the first string contains any of the
	 * characters in the second string. Returns <code>false</code> otherwise.
	 */
	private static boolean containsCharacters(String s, String characters)
	{
		int stringLength = s.length();
		for (int i = 0; i < stringLength; i++)
			if (characters.indexOf(s.charAt(i)) != -1)
				return true;

		return false;
	} //}}}

	//}}}

	//{{{ ConsoleState class
	static class ConsoleState
	{
		String currentDirectory = System.getProperty("user.dir");
		String lastDirectory = System.getProperty("user.dir");
		Stack directoryStack = new Stack();
		ConsoleProcess process;

		void gotoLastDirectory(Console console)
		{
			String[] pp = { lastDirectory };
			if(new File(lastDirectory).exists())
			{
				String newLastDir = currentDirectory;
				currentDirectory = lastDirectory;
				lastDirectory = newLastDir;
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

		void setCurrentDirectory(Console console, String newDir)
		{
			String[] pp = { newDir };
			if(new File(newDir).exists())
			{
				lastDirectory = currentDirectory;
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
