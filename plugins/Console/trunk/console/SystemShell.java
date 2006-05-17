/*
 * SystemShell.java - Executes OS commands
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov
 * 
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
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;

// }}}

/**
 * A SystemShell belongs to each Console. It creates a ProcessBuilder. When it
 * is time to execute something, it creates a ConsoleProcess, passing the
 * ProcessBuilder down. The process itself is started indirectly by
 * ProcessRunner.exec().
 */

public class SystemShell extends Shell
{

	// {{{ SystemShell constructor
	public SystemShell()
	{
		super("System");
		lineSep = toBytes(System.getProperty("line.separator"));
		processBuilder = new ProcessBuilder();
		consoleStateMap = new Hashtable<Console, ConsoleState>();
		showExitStatus = jEdit.getBooleanProperty("console.processrunner.showExitStatus", true);

	} // }}}

	// {{{ openConsole() method
	/**
	 * Called when a Console dockable first selects this shell.
	 * 
	 * @since Console 4.0.2
	 */
	public void openConsole(Console console)
	{
		consoleStateMap.put(console, new ConsoleState());
	} // }}}

	// {{{ closeConsole() method
	/**
	 * Called when a Console dockable is closed.
	 * 
	 * @since Console 4.0.2
	 */
	public void closeConsole(Console console)
	{
		ConsoleProcess process = getConsoleState(console).process;
		if (process != null)
			process.stop();

		consoleStateMap.remove(console);
	} // }}}

	// {{{ printInfoMessage() method
	public void printInfoMessage(Output output)
	{
		output.print(null, jEdit.getProperty("console.shell.info"));
	} // }}}

	// {{{ printPrompt()
	/**
	 * Prints a prompt to the specified console.
	 * 
	 * @param output
	 *                The output
	 */
	public void printPrompt(Console console, Output output)
	{
		ConsoleState cstate = getConsoleState(console);
		String currentDirectory;
		if (cstate == null)
			currentDirectory = System.getProperty("user.dir");
		else
		{
			currentDirectory = cstate.currentDirectory;
		}
		ConsoleProcess proc = cstate.process;
		if (proc != null && showExitStatus)  {
			int exitCode = proc.getExitStatus();
			Object[] args = proc.getArgs();
			Object[] pp = { args[0], new Integer(exitCode) };	
			String msg = jEdit.getProperty("console.shell.exited", pp);
			if (exitCode == 0)
				output.print(console.getInfoColor(), msg);
			else
				output.print(console.getErrorColor(), msg);
		}
		output.writeAttrs(ConsolePane.colorAttributes(console.getPlainColor()), jEdit
			.getProperty("console.shell.prompt", new String[] { currentDirectory }));
		output.writeAttrs(null, " ");
	}

	// }}}
	// {{{ execute()
	public void execute(final Console console, String input, final Output output, Output error,
		String command)
	{

		if (error == null)
			error = output;
		ConsoleState state = getConsoleState(console);

		if (state.process != null)
		{
			PipedOutputStream out = state.process.getPipeOutput();
			if (out != null)
			{
				try
				{
					out.write(toBytes(command));
					out.write(lineSep);
					out.flush();
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
			return;
		}

		// comments, for possible future scripting support
		if (command.startsWith("#"))
		{
			output.commandDone();
			return;
		}

		// lazily initialize aliases and variables
		init();

		Vector<String> args = parse(command);
		// will be null if the command is an empty string
		if (args == null)
		{
			output.commandDone();
			return;
		}

		args = preprocess(console.getView(), console, args);

		String commandName = (String) args.elementAt(0);
		if (commandName.charAt(0) == '%')
		{
			// a console built-in
			args.removeElementAt(0);
			executeBuiltIn(console, output, error, commandName, args);
			output.commandDone();
			return;
		}

		// if current working directory doesn't exist, print an error.
		String cwd = state.currentDirectory;
		if (!new File(cwd).exists())
		{
			output.print(console.getErrorColor(), jEdit.getProperty(
				"console.shell.error.working-dir", new String[] { cwd }));
			output.commandDone();
			// error.commandDone();
			return;
		}

		String fullPath = MiscUtilities.constructPath(cwd, commandName);

		// Java resolves this relative to user.dir, not
		// the directory we pass to exec()...
		if (commandName.startsWith("./") || commandName.startsWith("." + File.separator))
		{
			args.setElementAt(fullPath, 0);
		}

		if (new File(fullPath).isDirectory() && args.size() == 1)
		{
			args.setElementAt(fullPath, 0);
			executeBuiltIn(console, output, error, "%cd", args);
			output.commandDone();
			// error.commandDone();
		}
		else
		{
			boolean foreground;

			if (args.elementAt(args.size() - 1).equals("&"))
			{
				// run in background
				args.removeElementAt(args.size() - 1);
				foreground = false;
				output.commandDone();
				// error.commandDone();
			}
			else
			{
				// run in foreground
				foreground = true;
			}

			String[] _args = new String[args.size()];
			args.copyInto(_args);
			state.currentDirectory = cwd;
			final ConsoleProcess proc = new ConsoleProcess(console, output, _args,
				processBuilder, state, foreground);

			/* If startup failed its no longer running */
			if (foreground && proc.isRunning())
			{
				console.getErrorSource().clear();
				state.process = proc;
			}

			/* Check if we were doing a "run command with selection as input" */
			if (input != null)
			{
				try
				{
					OutputStream out = proc.getPipeOutput();
					out.write(toBytes(input));
					out.close();
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
	}

	// }}}

	// {{{ stop() method
	public void stop(Console console)
	{
		ConsoleState consoleState = getConsoleState(console);
		if (consoleState == null)
			return;
		ConsoleProcess process = consoleState.process;
		if (process != null)
			process.stop();
		else
		{
			console.getOutput().print(console.getErrorColor(),
				jEdit.getProperty("console.shell.noproc"));
		}
	} // }}}

	// {{{ waitFor() method
	/**
	 * Waits for currently running Console processes to finish execution.
	 * @return true if all was successful (i.e. the error status code was 0)
	 */
	public boolean waitFor(Console console)
	{
		ConsoleState consoleState = getConsoleState(console);
		if (consoleState == null)
			return true;
		ConsoleProcess process = consoleState.process;
		if (process != null)
		{
			try
			{
				return (process.waitFor() == 0);
			}
			catch (InterruptedException e)
			{
			}

			return process.getExitStatus() == 0;
		}
		else
			return true;
	} // }}}

	// {{{ endOfFile() method
	/**
	 * Sends an end of file.
	 * 
	 * @param console
	 *                The console
	 */
	public void endOfFile(Console console)
	{
		ConsoleState state = getConsoleState(console);

		if (state.process != null)
		{
			console.getOutput().writeAttrs(
				ConsolePane.colorAttributes(console.getInfoColor()), "^D\n");
			PipedOutputStream out = state.process.getPipeOutput();
			try
			{
				out.close();
			}
			catch (IOException e)
			{
			}
		}
	} // }}}

	// {{{ detach() method
	/**
	 * Detaches the currently running process.
	 * 
	 * @param console
	 *                The console
	 */
	public void detach(Console console)
	{
		SystemShell.ConsoleState state = getConsoleState(console);

		ConsoleProcess process = state.process;
		if (process == null)
		{
			console.getOutput().print(console.getErrorColor(),
				jEdit.getProperty("console.shell.noproc"));
			return;
		}

		process.detach();
	} // }}}

	// {{{ getCompletions() method
	/**
	 * Returns possible completions for the specified command.
	 * 
	 * @param console
	 *                The console instance
	 * @param command
	 *                The command
	 * @since Console 3.6
	 */
	public CompletionInfo getCompletions(Console console, String command)
	{
		// lazily initialize aliases and variables
		init();

		View view = console.getView();
		String currentDirectory = (console == null ? System.getProperty("user.dir")
			: getConsoleState(console).currentDirectory);

		final String fileDelimiters = "=\'\" \\" + File.pathSeparator;

		String lastArgEscaped, lastArg;
		if (File.separatorChar == '\\')
		{
			// Escaping impossible
			lastArgEscaped = (String) parse(command).lastElement();
			lastArg = lastArgEscaped;
		}
		else
		{
			// Escaping possible

			// I use findLastArgument and then unescape instead of
			// (String)parse(command).lastElement() because there's
			// no way
			// to get parse(String) to also return the original
			// length
			// of the unescaped argument, which we need to calculate
			// the
			// completion offset.

			lastArgEscaped = findLastArgument(command, fileDelimiters);
			lastArg = unescape(lastArgEscaped, fileDelimiters);
		}

		CompletionInfo completionInfo = new CompletionInfo();

		completionInfo.offset = command.length() - lastArg.length();

		if (completionInfo.offset == 0)
		{
			completionInfo.completions = (String[]) getCommandCompletions(view,
				currentDirectory, lastArg).toArray(new String[0]);
		}
		else
		{
			completionInfo.completions = (String[]) getFileCompletions(view,
				currentDirectory, lastArg, false).toArray(new String[0]);
		}

		// On systems where the file separator is the same as the escape
		// character (Windows), it's impossible to do escaping properly,
		// so we just assume that escaping is not needed (which is true
		// for windows).
		if (File.separatorChar != '\\')
		{
			for (int i = 0; i < completionInfo.completions.length; i++)
			{
				completionInfo.completions[i] = escape(
					completionInfo.completions[i], fileDelimiters);
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
		boolean isDoubleQuoted = (completionInfo.offset > 0)
			&& (command.charAt(completionInfo.offset - 1) == '\"');
		if (!isDoubleQuoted)
		{
			final String specialCharacters = (File.separatorChar == '\\') ? " "
				: fileDelimiters;
			for (int i = 0; i < completionInfo.completions.length; i++)
			{
				String result = completionInfo.completions[i];
				if (containsCharacters(result, specialCharacters))
					result = "\"" + result;
				completionInfo.completions[i] = result;
			}
		}

		return completionInfo;
	}

	// }}}

	// {{{ expandVariables()

	static final String varPatternString = "([$%])([a-zA-Z0-9_]+)(\\1?)";

	static final String varPatternString2 = "([$%])\\{([^}]+)\\}";

	static final Pattern varPattern = Pattern.compile(varPatternString);

	static final Pattern varPattern2 = Pattern.compile(varPatternString2);

	/**
	 * returns a string after it's been processed by jedit's internal
	 * command processor
	 * 
	 * @param view
	 *                A view corresponding to this console's state.
	 * @param arg
	 *                A single argument on the command line
	 * @return A string after it's been processed, with variables replaced
	 *         with their values.
	 */
	public String expandVariables(View view, String arg)
	{

		StringBuffer buf = new StringBuffer();
		String varName = null;

		arg = arg.replace("^~", System.getProperty("user.home"));
		Matcher m = varPattern.matcher(arg);
		if (!m.find())
		{
			m = varPattern2.matcher(arg);
			if (!m.find())
				return arg;
		}
		varName = m.group(2);
		String expansion = getVariableValue(view, varName);

		if (expansion != null)
		{
			expansion = expansion.replace("\\", "\\\\");
			return m.replaceFirst(expansion);
		}
		return arg;
	}

	// }}}

	// {{{ getVariableValue() method
	public String getVariableValue(View view, String varName)
	{
		init();
		Map<String, String> variables = processBuilder.environment();
		if (view == null)
			return variables.get(varName);

		String expansion;

		// Expand some special variables
		Buffer buffer = view.getBuffer();

		// What is this for?
		if (varName.equals("$") || varName.equals("%"))
			expansion = varName;
		else if (varName.equals("d"))
		{
			expansion = MiscUtilities.getParentOfPath(buffer.getPath());
			if (expansion.endsWith("/") || expansion.endsWith(File.separator))
			{
				expansion = expansion.substring(0, expansion.length() - 1);
			}
		}
		else if (varName.equals("u"))
		{
			expansion = buffer.getPath();
			if (!MiscUtilities.isURL(expansion))
			{
				expansion = "file:/" + expansion.replace(File.separatorChar, '/');
			}
		}
		else if (varName.equals("f"))
			expansion = buffer.getPath();
		else if (varName.equals("n"))
			expansion = buffer.getName();
		else if (varName.equals("c"))
			expansion = ConsolePlugin.getClassName(buffer);
		else if (varName.equals("PKG"))
		{
			expansion = ConsolePlugin.getPackageName(buffer);
			if (expansion == null)
				expansion = "";
		}
		else if (varName.equals("ROOT"))
			expansion = ConsolePlugin.getPackageRoot(buffer);
		else if (varName.equals("BROWSER_DIR"))
		{
			VFSBrowser browser = (VFSBrowser) view.getDockableWindowManager()
				.getDockable("vfs.browser");
			if (browser == null)
				expansion = null;
			else
				expansion = browser.getDirectory();
		}
		else
			expansion = (String) variables.get(varName);

		return expansion;
	} // }}}

	// {{{ getAliases() method
	public Hashtable getAliases()
	{
		init();
		return aliases;
	} // }}}

	// {{{ getConsoleState() method
	ConsoleState getConsoleState(Console console)
	{
		ConsoleState retval = (ConsoleState) consoleStateMap.get(console);
		if (retval == null)
			openConsole(console);
		return (ConsoleState) consoleStateMap.get(console);
	} // }}}

	// {{{ getVariables() method
	Map getVariables()
	{
		return processBuilder.environment();
	} // }}}

	// {{{ propertiesChanged() method
	static void propertiesChanged()
	{
		// aliases = null;
		// variables = null;

		// next time execute() is called, init() will reload everything
	} // }}}

	// }}}

	// {{{ toBytes() method
	private static byte[] toBytes(String str)
	{
		try
		{
			return str.getBytes(jEdit.getProperty("console.encoding"));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	} // }}}

	// {{{ init() method
	private void init()
	{
		if (initialized)
			return;

		initialized = true;

		initCommands();
		initAliases();
		initVariables();
	} // }}}

	// {{{ initCommands() method
	private void initCommands()
	{
		commands = new Hashtable<String, SystemShellBuiltIn>();
		commands.put("%alias", new SystemShellBuiltIn.alias());
		commands.put("%aliases", new SystemShellBuiltIn.aliases());
		commands.put("%browse", new SystemShellBuiltIn.browse());
		commands.put("%cd", new SystemShellBuiltIn.cd());
		commands.put("%clear", new SystemShellBuiltIn.clear());
		commands.put("%dirstack", new SystemShellBuiltIn.dirstack());
		commands.put("%echo", new SystemShellBuiltIn.echo());
		commands.put("%edit", new SystemShellBuiltIn.edit());
		commands.put("%env", new SystemShellBuiltIn.env());
		commands.put("%help", new SystemShellBuiltIn.help());
		commands.put("%kill", new SystemShellBuiltIn.kill());
		commands.put("%popd", new SystemShellBuiltIn.popd());
		commands.put("%pushd", new SystemShellBuiltIn.pushd());
		commands.put("%pwd", new SystemShellBuiltIn.pwd());
		commands.put("%run", new SystemShellBuiltIn.run());
		commands.put("%set", new SystemShellBuiltIn.set());
		commands.put("%unalias", new SystemShellBuiltIn.unalias());
		commands.put("%unset", new SystemShellBuiltIn.unset());
		commands.put("%version", new SystemShellBuiltIn.version());
	} // }}}

	// {{{ initAliases() method
	private void initAliases()
	{
		aliases = new Hashtable();
		ProcessRunner pr = ProcessRunner.getProcessRunner();
		pr.setUpDefaultAliases(aliases);

		// some built-ins can be invoked without the % prefix
		aliases.put("cd", "%cd");
		aliases.put("pushd", "%pushd");
		aliases.put("popd", "%popd");
		aliases.put("pwd","%pwd");
		aliases.put("aliases", "%aliases");
		aliases.put("alias", "%alias");
		aliases.put("-", "%cd -");

		// load aliases from properties
		String alias;
		int i = 0;
		while ((alias = jEdit.getProperty("console.shell.alias." + i)) != null)
		{
			aliases.put(alias, jEdit.getProperty("console.shell.alias." + i
				+ ".expansion"));
			i++;
		}
	} // }}}

	// {{{ initVariables() method
	private void initVariables()
	{
		Map<String, String> variables = processBuilder.environment();
		if (jEdit.getJEditHome() != null)
			variables.put("JEDIT_HOME", jEdit.getJEditHome());

		if (jEdit.getSettingsDirectory() != null)
			variables.put("JEDIT_SETTINGS", jEdit.getSettingsDirectory());

		// for the sake of Unix programs that try to be smart
		variables.put("TERM", "dumb");

		// load variables from properties
		/*
		 * String varname; i = 0; while((varname =
		 * jEdit.getProperty("console.shell.variable." + i)) != null) {
		 * variables.put(varname,jEdit.getProperty("console.shell.variable." +
		 * i + ".value")); i++; }
		 */
	} // }}}

	// {{{ parse() method
	/**
	 * Convert a command into a vector of arguments.
	 */
	private Vector<String> parse(String command)
	{
		Vector<String> args = new Vector<String>();

		// We replace \ with a non-printable char because
		// StreamTokenizer handles \ specially, which causes
		// problems on Windows as \ is the file separator
		// there.

		// After parsing is done, the non printable char is
		// changed to \ once again.

		// StreamTokenizer needs a way to disable backslash
		// handling...
		if (File.separatorChar == '\\')
			command = command.replace('\\', dosSlash);

		StreamTokenizer st = new StreamTokenizer(new StringReader(command));
		st.resetSyntax();
		st.wordChars('!', 255);
		st.whitespaceChars(0, ' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		try
		{
			loop: for (;;)
			{
				switch (st.nextToken())
				{
				case StreamTokenizer.TT_EOF:
					break loop;
				case StreamTokenizer.TT_WORD:
				case '"':
				case '\'':
					if (File.separatorChar == '\\')
						args.addElement(st.sval.replace(dosSlash, '\\'));
					else
						args.addElement(st.sval);
					break;
				}
			}
		}
		catch (IOException io)
		{
			// won't happen
		}

		if (args.size() == 0)
			return null;
		else
			return args;
	} // }}}

	// {{{ preprocess() method
	/**
	 * Expand aliases, variables and globs.
	 * 
	 * @return a new vector of arguments after vars have been substituted.
	 */
	private Vector<String> preprocess(View view, Console console, Vector<String> args)
	{
		Vector<String> newArgs = new Vector<String>();

		// expand aliases
		String commandName = args.elementAt(0);

		String expansion = aliases.get(commandName);
		if (expansion != null)
		{
			Vector<String> expansionArgs = parse(expansion);
			for (int i = 0; i < expansionArgs.size(); i++)
			{
				expandGlobs(view, newArgs, (String) expansionArgs.elementAt(i));
			}
		}
		else
			expandGlobs(view, newArgs, commandName);

		// add remaining arguments
		for (int i = 1; i < args.size(); i++)
			expandGlobs(view, newArgs, (String) args.elementAt(i));

		return newArgs;
	} // }}}

	// {{{ expandGlobs() method
	/**
	 * @param arg -
	 *                a single input argument
	 * @param args -
	 *                an output variable where we place resulting expanded
	 *                args
	 * 
	 */
	private void expandGlobs(View view, Vector<String> args, String arg)
	{
		// XXX: to do
		args.addElement(expandVariables(view, arg));
	} // }}}

	// {{{ executeBuiltIn() method

	public void executeBuiltIn(Console console, Output output, Output error, String command,
		Vector args)
	{
		SystemShellBuiltIn builtIn = (SystemShellBuiltIn) commands.get(command);
		if (builtIn == null)
		{
			String[] pp = { command };
			error.print(console.getErrorColor(), jEdit.getProperty(
				"console.shell.unknown-builtin", pp));
		}
		else
		{
			builtIn.execute(console, output, error, args);
		}
	} // }}}

	// {{{ getFileCompletions() method
	private List getFileCompletions(View view, String currentDirName, String typedFilename,
		boolean directoriesOnly)
	{
		String expandedTypedFilename = expandVariables(view, typedFilename);

		int lastSeparatorIndex = expandedTypedFilename.lastIndexOf(File.separator);

		// The directory part of what the user typed, including the file
		// separator.
		String typedDirName = lastSeparatorIndex == -1 ? "" : expandedTypedFilename
			.substring(0, lastSeparatorIndex + 1);

		// The file typed by the user.
		File typedFile = new File(expandedTypedFilename).isAbsolute() ? new File(
			expandedTypedFilename) : new File(currentDirName, expandedTypedFilename);

		boolean directory = expandedTypedFilename.endsWith(File.separator)
			|| expandedTypedFilename.length() == 0;

		// The parent directory of the file typed by the user (or itself
		// if it's already a directory).
		File dir = directory ? typedFile : typedFile.getParentFile();

		// The filename part of the file typed by the user, or "" if
		// it's a directory.
		String fileName = directory ? "" : typedFile.getName();

		// The list of files we're going to try to match
		String[] filenames = dir.list();

		if ((filenames == null) || (filenames.length == 0))
			return null;

		boolean isOSCaseSensitive = ProcessRunner.getProcessRunner().isCaseSensitive();
		ArrayList matchingFilenames = new ArrayList(filenames.length);
		int matchingFilenamesCount = 0;
		String matchedString = isOSCaseSensitive ? fileName : fileName.toLowerCase();
		for (int i = 0; i < filenames.length; i++)
		{
			String matchedAgainst = isOSCaseSensitive ? filenames[i] : filenames[i]
				.toLowerCase();

			if (matchedAgainst.startsWith(matchedString))
			{
				String match;

				File matchFile = new File(dir, filenames[i]);
				if (directoriesOnly && !matchFile.isDirectory())
					continue;

				match = typedDirName + filenames[i];

				// Add a separator at the end if it's a
				// directory
				if (matchFile.isDirectory() && !match.endsWith(File.separator))
					match = match + File.separator;

				matchingFilenames.add(match);
			}
		}

		return matchingFilenames;
	} // }}}

	// {{{ getCommandCompletions() method
	private List getCommandCompletions(View view, String currentDirName, String command)
	{
		ArrayList list = new ArrayList();

		Iterator iter = commands.keySet().iterator();
		while (iter.hasNext())
		{
			String cmd = (String) iter.next();
			if (cmd.startsWith(command))
				list.add(cmd);
		}

		iter = aliases.keySet().iterator();
		while (iter.hasNext())
		{
			String cmd = (String) iter.next();
			if (cmd.startsWith(command))
				list.add(cmd);
		}

		list.addAll(getFileCompletions(view, currentDirName, command, false));

		return list;
	} // }}}

	// {{{ findLastArgument() method
	/**
	 * Returns the last argument in the given command by using the given
	 * delimiters. The delimiters can be escaped.
	 */
	private static String findLastArgument(String command, String delimiters)
	{
		int i = command.length() - 1;
		while (i >= 0)
		{
			char c = command.charAt(i);
			if (delimiters.indexOf(c) != -1)
			{
				if ((i == 0) || (command.charAt(i - 1) != '\\'))
					break;
				else
					i--;
			}
			i--;
		}

		return command.substring(i + 1);
	} // }}}

	// {{{ unescape() method
	/**
	 * Unescapes the given delimiters in the given string.
	 */
	private static String unescape(String s, String delimiters)
	{
		StringBuffer buf = new StringBuffer(s.length());
		int i = s.length() - 1;
		while (i >= 0)
		{
			char c = s.charAt(i);
			buf.append(c);
			if (delimiters.indexOf(c) != -1)
			{
				if (s.charAt(i - 1) == '\\')
					i--;
			}
			i--;
		}

		return buf.reverse().toString();
	} // }}}

	// {{{ escape() method
	/**
	 * Escapes the given delimiters in the given string.
	 */
	private static String escape(String s, String delimiters)
	{
		StringBuffer buf = new StringBuffer();
		int length = s.length();
		for (int i = 0; i < length; i++)
		{
			char c = s.charAt(i);
			if (delimiters.indexOf(c) != -1)
				buf.append('\\');
			buf.append(c);
		}

		return buf.toString();
	} // }}}

	// {{{ containsWhitespace() method
	/**
	 * Returns <code>true</code> if the first string contains any of the
	 * characters in the second string. Returns <code>false</code>
	 * otherwise.
	 */
	private static boolean containsCharacters(String s, String characters)
	{
		int stringLength = s.length();
		for (int i = 0; i < stringLength; i++)
			if (characters.indexOf(s.charAt(i)) != -1)
				return true;

		return false;
	}

	// }}}

	// {{{ ConsoleState class
	static class ConsoleState
	{
		String currentDirectory = System.getProperty("user.dir");

		String lastDirectory = System.getProperty("user.dir");

		Stack<String> directoryStack = new Stack<String>();

		ConsoleProcess process;

		void gotoLastDirectory(Console console)
		{
			setCurrentDirectory(console, lastDirectory);
		}

		void setCurrentDirectory(Console console, String newDir)
		{
			String[] pp = { newDir };
			File file = new File(newDir);
			if (!file.isAbsolute()) file = new File(currentDirectory, newDir);
			if (!file.exists())
			{
				console.getOutput().print(console.getErrorColor(),
					jEdit.getProperty("console.shell.cd.error", pp));
			}
			else if (!file.isDirectory())
			{
				console.getOutput().print(console.getErrorColor(),
					jEdit.getProperty("console.shell.cd.file", pp));
			}
			else
			{
				lastDirectory = currentDirectory;
				try {
					currentDirectory = file.getCanonicalPath();
				}
				catch (IOException ioe) { throw new RuntimeException(ioe);}
			}
		}
	}

	// }}}

	// {{{ private members
	private ProcessBuilder processBuilder;

	private Hashtable<Console, ConsoleState> consoleStateMap;

	private final char dosSlash = 127;

	private Hashtable<String, String> aliases;

	private Hashtable<String, SystemShellBuiltIn> commands;
	private boolean showExitStatus;

	private boolean initialized;

	private byte[] lineSep;
	// }}}

}
