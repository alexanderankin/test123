package console;

// {{{ Imports
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;

// }}}

/**
 * A SystemShell is shared across all instances of Console. 
 * It has own environment (variables), and executes system statements in
 * a shell which resembles in terms of user interface, something that is
 * a cross between the Windows "cmd.exe" and the Linux bash shell, so it
 * should be easy to use for both.
 * 
 * It manages a mapping of Console to ConsoleState objects, where the ConsoleState
 * manages the actual ConsoleProcess and the state of that shell.
 *  
 * When SystemShell executes something, the process itself is started indirectly by
 * ProcessRunner.exec().
 * @author 1999, 2005 Slava Pestov
 * @author 2006, 2009 Alan Ezust
 */
// {{{ class SystemShell
public class SystemShell extends Shell
{
	// {{{ private members
	private String userHome;

	/** common shell variables shared across all instances of the System Shell. */
	Map<String, String> variables;

	/** The state of each console System Shell instance. */
	private Hashtable<Console, ConsoleState> consoleStateMap;

	static private final char dosSlash = 127;

	/** Map of aliases */
	private Hashtable<String, String> aliases;

	/** Built in commands */
	private Hashtable<String, SystemShellBuiltIn> commands;

	private boolean initialized;

	private byte[] lineSep;

	// }}}

	// {{{ SystemShell constructor
	public SystemShell()
	{
		super("System");
		lineSep = toBytes(System.getProperty("line.separator"));
		consoleStateMap = new Hashtable<Console, ConsoleState>();
		userHome = System.getProperty("user.home");
		if (File.separator.equals("\\"))
		{
			userHome = userHome.replace("\\", "\\\\");
		}

	} // }}}

	// {{{ public methods
	// {{{ openConsole() method
	/**
	 * Called when a Console dockable first selects this shell.
	 *
	 * @since Console 4.0.2
	 */
	public void openConsole(Console console)
	{
		ConsoleState cs = new ConsoleState();
		consoleStateMap.put(console, cs);
		if (jEdit.getBooleanProperty("console.rememberCWD" )) {
			String propName = "console.cwd." + console.getId();
			String val = jEdit.getProperty(propName, "null");
			if (!val.equals("null")) cs.currentDirectory = val;
		}
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
		if (jEdit.getBooleanProperty("console.shell.info.toggle"))
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
		output.writeAttrs(ConsolePane.colorAttributes(console.getPlainColor()), jEdit
			.getProperty("console.shell.prompt", 
                new String[] { MiscUtilities.abbreviate(currentDirectory)}) + " ");
	}

	// }}}

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

	
	// {{{ execute()
	public void execute(final Console console, String input, final Output output, Output error,
		String command)
	{

		if (error == null)
			error = output;
		ConsoleState state = getConsoleState(console);

		// If a process is running under this shell and the pipe to its
		// stdin is open, treat this command line as a input line for
		// the process to make interactive processes usable as in
		// general shells.
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
					Log.log (Log.ERROR, this, "execute()", e);
				}
				return;
			}
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

		String commandName = args.elementAt(0);
		// check for drive letter changedirs (windows only)
		if (OperatingSystem.isWindows()
			&& commandName.endsWith(":")) {
			char driveLetter = commandName.charAt(0);
			args = state.changeDrive(driveLetter);
			commandName = args.elementAt(0);
		}
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
				variables, state, foreground);

			/* If startup failed its no longer running */
			if (foreground && proc.isRunning())
			{
				console.getErrorSource().clear();
				state.process = proc;
			}

			/*
			 * Check if we were doing a "run command with selection
			 * as input"
			 */
			if (input != null)
			{
				OutputStream out = proc.getPipeOutput();
				if (out != null)
				{
					try
					{
						out.write(toBytes(input));
						out.close();
					}
					catch (IOException e)
					{
						Log.log (Log.ERROR, this, "execute.pipeout", e);
					}
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
	 *
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
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
				}
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

		String lastArg;
		CompletionInfo completionInfo = new CompletionInfo();
		completionInfo.offset = 0;
		if (File.separatorChar == '\\')
		{
			// Escaping impossible
			String lastArgEscaped = (String) parse(command).lastElement();
			// We want to allow completion on the forward slash too
			lastArg = lastArgEscaped.replace('/', File.separatorChar);
			if (lastArg.startsWith("\\")) {
				ConsoleState state = getConsoleState(console);
				char drive = state.currentDirectory.charAt(0);
				lastArg = drive + ":\\" + lastArg.substring(1);
				completionInfo.offset = 2;
			}
		}
		else
		{
			// Escaping possible
			/* I use findLastArgument and then unescape instead of
			   (String)parse(command).lastElement() because there's
			   no way to get parse(String) to also return the original
			   length of the unescaped argument, which we need to calculate
			   the completion offset. */
			String lastArgEscaped = findLastArgument(command, fileDelimiters);
			lastArg = unescape(lastArgEscaped, fileDelimiters);
		}

		completionInfo.offset += command.length() - lastArg.length();

		Matcher m = homeDir.matcher(lastArg);
		lastArg = m.replaceFirst(System.getProperty("user.home"));

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

	static final Pattern homeDir = Pattern.compile("^~");

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
	String expandVariables(View view, String arg)
	{
		// StringBuffer buf = new StringBuffer();
		String varName = null;
		// Expand homedir
		Matcher m = homeDir.matcher(arg);
		if (m.find())
		{
			arg = m.replaceFirst(userHome);
		}
		
		m = varPattern.matcher(arg);
		if (!m.find())
		{
			m = varPattern2.matcher(arg);
			if (!m.find())
				return arg;
		}
		varName = m.group(2);
		String expansion = getVariableValue(view, varName);
		if (expansion == null)
		{
			varName = varName.toUpperCase();
			expansion = getVariableValue(view, varName);
		}
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
	public Hashtable<String, String> getAliases()
	{
		init();
		return aliases;
	} // }}}
	// }}}

	// {{{ methods
	// {{{ getConsoleState() method

	ConsoleState getConsoleState(Console console)
	{
		ConsoleState retval = (ConsoleState) consoleStateMap.get(console);
		if (retval == null)
			openConsole(console);
		return (ConsoleState) consoleStateMap.get(console);
	} // }}}

	// {{{ getVariables() method
	Map<String, String> getVariables()
	{
        init();
		return variables;
	} // }}}

	// {{{ propertiesChanged() method
	static void propertiesChanged()
	{
		// aliases = null;
		// variables = null;

		// next time execute() is called, init() will reload everything
	} // }}}

	// {{{ toBytes() method
	private static byte[] toBytes(String str)
	{
		try
		{
			return str.getBytes(jEdit.getProperty("console.encoding"));
		}
		catch (UnsupportedEncodingException e)
		{
			Log.log (Log.ERROR, SystemShell.class, "toBytes()", e);
			return null;
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
		commands.put("%printwd", new SystemShellBuiltIn.pwd());
		commands.put("%run", new SystemShellBuiltIn.run());
		commands.put("%set", new SystemShellBuiltIn.set());
		commands.put("%unalias", new SystemShellBuiltIn.unalias());
		commands.put("%unset", new SystemShellBuiltIn.unset());
		commands.put("%version", new SystemShellBuiltIn.version());
	} // }}}

	// {{{ initAliases() method
	private void initAliases()
	{
		aliases = new Hashtable<String,String>();
		ProcessRunner pr = ProcessRunner.getProcessRunner();
		pr.setUpDefaultAliases(aliases);

		// some built-ins can be invoked without the % prefix
		
		aliases.put("cd", "%cd");
		aliases.put("pushd", "%pushd");
		aliases.put("popd", "%popd");
		aliases.put("pwd", "%printwd");
		aliases.put("aliases", "%aliases");
		aliases.put("alias", "%alias");
		
		aliases.put("-", "%cd -");

		/* run ant without adornments to make error parsing easier */
		aliases.put("ant", "ant -emacs");
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
		variables = new HashMap<String, String>();
		variables.putAll(System.getenv());

		if (File.separator.equals("\\"))
		{
			Map<String, String> upcased = new HashMap<String, String>();
			for (String key : variables.keySet())
			{
				upcased.put(key.toUpperCase(), variables.get(key));
			}
			variables = upcased;
		}

		if (jEdit.getJEditHome() != null)
			variables.put("JEDIT_HOME", jEdit.getJEditHome());

		if (jEdit.getSettingsDirectory() != null)
			variables.put("JEDIT_SETTINGS", jEdit.getSettingsDirectory());

		// for the sake of Unix programs that try to be smart
		variables.put("TERM", "dumb");

		// load variables from properties
		
		 String varname; 
		 int i = 0; 
		 while((varname = jEdit.getProperty("console.shell.variable." + i)) != null) {
		     variables.put(varname, jEdit.getProperty("console.shell.variable." + i + ".value")); i++; 
		 }
		 
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


	// {{{ getFileCompletions() method
	private List<String> getFileCompletions(View view, String currentDirName, String typedFilename,
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
		StringList matchingFilenames = new StringList();
//		int matchingFilenamesCount = 0;
		String matchedString = isOSCaseSensitive ? fileName : fileName.toLowerCase();
		for (int i = 0; i < filenames.length; i++)
		{
			String matchedAgainst = isOSCaseSensitive ? filenames[i] : filenames[i]
				.toLowerCase();

			if (matchedAgainst.startsWith(matchedString))
			{
				StringBuffer match = new StringBuffer();

				File matchFile = new File(dir, filenames[i]);
				if (directoriesOnly && !matchFile.isDirectory())
					continue;

				match.append(typedDirName + filenames[i]);
				
				// Add a separator at the end if it's a
				// directory
				if (matchFile.isDirectory() && match.charAt(match.length()-1) != File.separatorChar)
					match.append(File.separator);

				matchingFilenames.add(match.toString());
			}
		}
		return matchingFilenames;
	} // }}}

	// {{{ getCommandCompletions() method
	private List<String> getCommandCompletions(View view, String currentDirName, String command)
	{
		StringList list = new StringList();

		Iterator<String> iter = commands.keySet().iterator();
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

	// }}}

	// {{{ ConsoleState inner class
	/**
	 * A SystemShell is a singleton instance - one per plugin. There are a
	 * number of ConsoleStates, one for each Console instance.
	 *
	 * The ConsoleState contains information that the SystemShell needs to
	 * know "where it is". This includes: ConsoleProcess process, String
	 * currentDirectory, String lastDirectory, Stack<String>
	 * directoryStack.
	 */
	static class ConsoleState
	{
		// {{{ ConsoleState members
		private ConsoleProcess process;

		private ConsoleProcess lastProcess;

		/* used only for windows, to keep track of current directories
		 * for each drive letter
		 */
		private HashMap<Character, String> driveDirectories = null;
		
		String currentDirectory = System.getProperty("user.dir");

		String lastDirectory = System.getProperty("user.dir");

		Stack<String> directoryStack = new Stack<String>();
		// }}}
				
		// {{{ setProcess method
		void setProcess(ConsoleProcess cp)
		{
			if (process != null)
				lastProcess = process;
			process = cp;
		} // }}}

		// {{{ getProcess
		ConsoleProcess getProcess()
		{
			return process;
		} // }}}

		// {{{ getLastProcess method
		ConsoleProcess getLastProcess()
		{
			if (process != null)
				return process;
			return lastProcess;
		} // }}}

		// {{{ gotoLastDirectory method
		void gotoLastDirectory(Console console)
		{
			setCurrentDirectory(console, lastDirectory);
		} // }}}

		// {{{ changeDrive()
		Vector<String> changeDrive(char driveLetter) {
			driveLetter = Character.toUpperCase(driveLetter);
			Vector<String> retval = new Vector<String>();
			retval.add("%cd");
			char curDrive = Character.toUpperCase(currentDirectory.charAt(0));
			if (driveDirectories == null) driveDirectories = new HashMap<Character, String>();
			driveDirectories.put(Character.valueOf(curDrive), currentDirectory);
			String path = driveLetter + ":" + File.separator;
			if (driveDirectories.containsKey(Character.valueOf(driveLetter))) {
				path = driveDirectories.get(Character.valueOf(driveLetter));
			}
			retval.add(path);
			return retval;
			
		} // }}}
		
		// {{{ setCurrentDirectory()
		void setCurrentDirectory(Console console, String newDir)
		{
			String[] pp = { newDir };
			File file = new File(newDir);
			if (!file.isAbsolute())
				file = new File(currentDirectory, newDir);
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
				try
				{
					currentDirectory = file.getCanonicalPath();
					if (jEdit.getBooleanProperty("console.rememberCWD")) 
					{
						String propName = "console.cwd." + console.getId();
						jEdit.setProperty(propName, currentDirectory);
					}
				}
				catch (IOException ioe)
				{
					Log.log (Log.ERROR, this, "setCurrentDirectory()", ioe);					
				}
			}
		} // }}}
	} // }}}
} // }}}
