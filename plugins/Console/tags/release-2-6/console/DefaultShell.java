/*
 * DefaultShell.java - Executes OS commands
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

import java.lang.reflect.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

class DefaultShell extends Shell
{
	public DefaultShell()
	{
		super("Console");

		try
		{
			Class[] classes = { String[].class, String[].class, File.class };
			java13exec = Runtime.class.getMethod("exec",classes);
		}
		catch(Exception e)
		{
			// do nothing
		}

		dir = System.getProperty("user.dir");

		String osName = System.getProperty("os.name");
		appendEXE = (osName.indexOf("Windows") != -1 ||
			osName.indexOf("OS/2") != -1);
		if(appendEXE)
			initTableWinBuiltIns();
	}

	public void printInfoMessage(Console console)
	{
		console.printInfo(jEdit.getProperty("console.shell.info"));
	}

	public void execute(View view, String command, Console console)
	{
		stop();
		ConsolePlugin.clearErrors();

		// We replace \ with a non-printable char because
		// StreamTokenizer handles \ specially, which causes
		// problems on Windows as \ is the file separator
		// there.

		// After parsing is done, the non printable char is
		// changed to \ once again.

		// StreamTokenizer needs a way to disable backslash
		// handling...
		String rawCommand = command;
		if(appendEXE)
			command = rawCommand.replace('\\',winSlash);

		StreamTokenizer st = new StreamTokenizer(new StringReader(command));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		Vector args = new Vector();
		StringBuffer buf = new StringBuffer();

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
					args.addElement(expandVariables(view,
						st.sval,buf));
					break;
				}
			}
		}
		catch(IOException io)
		{
			String[] pp = { io.getMessage() };
			console.printError(jEdit.getProperty("console.shell.ioerror",pp));
		}

		if(args.size() == 0)
		{
			exitStatus = false;
			return;
		}

		String executable = (String)args.elementAt(0);
		if(executable.equals("pwd"))
		{
			if(args.size() != 1)
			{
				console.printError(jEdit.getProperty("console.shell.pwd-usage"));
				exitStatus = false;
			}
			else
			{
				console.printPlain(dir);
				exitStatus = true;
			}
			return;
		}
		else if(executable.equals("cd"))
		{
			if(java13exec == null)
			{
				console.printError(jEdit.getProperty("console.shell.cd-unsup"));
				exitStatus = false;
			}
			else if(args.size() != 2)
			{
				console.printError(jEdit.getProperty("console.shell.cd-usage"));
				exitStatus = false;
			}
			else
			{
				String newDir = (String)args.elementAt(1);
				if(newDir.equals(".."))
					newDir = MiscUtilities.getParentOfPath(dir);
				else
					newDir = MiscUtilities.constructPath(dir,newDir);
				String[] pp = { newDir };
				if(new File(newDir).exists())
				{
					dir = newDir;
					console.printPlain(jEdit.getProperty("console.shell.cd",pp));
					exitStatus = true;
				}
				else
				{
					console.printError(jEdit.getProperty("console.shell.cd-error",pp));
					exitStatus = false;
				}
			}

			return;
		}
		else if(appendEXE && tableWinBuiltIns.contains(executable))
		{
			// which command interpreter?
			if(System.getProperty("os.name").indexOf("Windows 9") != -1)
				args.insertElementAt("command.com",0);
			else
				args.insertElementAt("cmd.exe",0);

			executable = (String)args.elementAt(0);

			args.insertElementAt("/C",1);
		}

		// Hack to look for executable in current directory
		String cdPath = dir + System.getProperty("file.separator");
		if( tryExtensions( cdPath, executable, args, console ) == null )
		{
			// Now  try the executable found along the PATH
			if( tryExtensions( null, executable, args, console ) == null )
			{
				exitStatus = false;
				return;
			}
		}

		this.command = rawCommand;
		this.console = console;

		stdout = new StdoutThread();
		stderr = new StderrThread();
	}

	public synchronized void stop()
	{
		if(command != null)
		{
			stdout.stop();
			stderr.stop();
			process.destroy();

			String[] args = { command };
			console.printError(jEdit.getProperty("console.shell.killed",args));

			exitStatus = false;
			commandDone();
		}
	}

	public synchronized boolean waitFor()
	{
		if(command != null)
		{
			try
			{
				wait();
			}
			catch(InterruptedException ie)
			{
				return false;
			}
		}
		return exitStatus;
	}

	// private members
	private static final char winSlash = 127;

	private String command;
	private Console console;
	private Process process;
	private Thread stdout;
	private Thread stderr;
	private String dir;
	private Method java13exec;
	private boolean appendEXE;

	private int threadDoneCount;
	private boolean exitStatus;

	// used to store built-in commands
	private Hashtable tableWinBuiltIns;

	/**
	 Specical processing for some well known Windows file extensions.
	 */
	private Process tryExtensions(  String currentDir,
					String  executable,
					Vector  args,
					Console console ) {
		String[] extensionsToTry;
		if(appendEXE && executable.indexOf('.') == -1)
			extensionsToTry = new String[] { ".cmd", ".bat", ".com", ".exe" };
		else
			extensionsToTry = new String[] { "" };

		String[] _args = new String[args.size()];
		args.copyInto(_args);

		for(int i = 0; i < extensionsToTry.length; i++)
		{
			if( currentDir == null )
			{
				_args[0] = executable + extensionsToTry[i];
			}
			else
			{
				String exeFullPath = currentDir + executable + extensionsToTry[i];
				File exeFile = new File( exeFullPath );
				if( !exeFile.exists() )
					return null;
				_args[0] = exeFullPath;
			}

			try
			{
				process = _exec(_args);
				process.getOutputStream().close();
				return process;
			}
			catch(IOException io)
			{
				if(i == extensionsToTry.length - 1)
				{
					String[] tt = { io.getMessage() };
					console.printError(jEdit.getProperty("console.shell.ioerror",tt));
					exitStatus = false;
					return null;
				}
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,t);
				exitStatus = false;
				return null;
			}
		}

		return null;
	}

	private String expandVariables(View view, String arg, StringBuffer buf)
	{
		buf.setLength(0);

		for(int i = 0; i < arg.length(); i++)
		{
			char c = arg.charAt(i);
			switch(c)
			{
			case winSlash:
				buf.append('\\');
				break;
			case '$':
				if(i == arg.length() - 1)
					buf.append(c);
				else
				{
					Buffer buffer = view.getBuffer();
					switch(arg.charAt(++i))
					{
					case 'd':
						String path = MiscUtilities.getParentOfPath(
							buffer.getPath());
						if(path.endsWith("/")
							|| path.endsWith(File.separator))
							path = path.substring(0,

								path.length() - 1);
						buf.append(path);
						break;
					case 'u':
						path = buffer.getPath();
						if(!MiscUtilities.isURL(path))
							path = "file:" + path;
						buf.append(path);
						break;
					case 'f':
						buf.append(buffer.getPath());
						break;
					case 'j':
						buf.append(jEdit.getJEditHome());
						break;
					case 'n':
						String name = buffer.getName();
						int index = name.lastIndexOf('.');
						if(index == -1)
							buf.append(name);
						else
							buf.append(name.substring(0,index));
						break;
					case '$':
						buf.append('$');
						break;
					}
				}
				break;
			case '~':
				// insert the home directory if the tilde
				// is the last character on the line, or if
				// the character after it is whitespace or
				// a path separator.
				if(i == arg.length() - 1)
				{
					buf.append(System.getProperty("user.home"));
					break;
				}
				c = arg.charAt(i + 1);
				if(c == '/' || c == ' ' || c == File.separatorChar)
				{
					buf.append(System.getProperty("user.home"));
					break;
				}
				buf.append('~');
				break;
			default:
				buf.append(c);
				break;
			}
		}

		return buf.toString();
	}

	private void initTableWinBuiltIns()
	{
		String [] elems  = { "md", "rd", "del", "dir", "copy",
					"move", "erase", "mkdir", "rmdir", "start",
					"path", "ver", "vol", "ren", "type"};
		this.tableWinBuiltIns = new Hashtable();
		for( int i = 0; i < elems.length; ++i)
		{
			this.tableWinBuiltIns.put(elems[i], elems[i]);
		}
	}

	private void parseLine(String line)
	{
		if(console == null)
			return;

		int type = ConsolePlugin.parseLine(line,dir);
		switch(type)
		{
		case ErrorSource.ERROR:
			console.printError(line);
			break;
		case ErrorSource.WARNING:
			console.printWarning(line);
			break;
		default:
			console.printPlain(line);
			break;
		}
	}

	private synchronized void threadDone()
	{
		threadDoneCount++;
		if(threadDoneCount == 2)
			commandDone();
	}

	private synchronized void commandDone()
	{
		if(process != null)
		{
			int exitCode;
			try
			{
				exitCode = process.waitFor();
			}
			catch(InterruptedException e)
			{
				Log.log(Log.ERROR,this,"Yo Flav, what is this?");
				return;
			}

			Object[] args = { command, new Integer(exitCode) };

			String msg = jEdit.getProperty("console.shell.exited",args);
			if(exitCode == 0)
				console.printInfo(msg);
			else
				console.printError(msg);

			exitStatus = (exitCode == 0);
		}

		threadDoneCount = 0;

		command = null;
		stdout = null;
		stderr = null;
		process = null;
		console = null;

		notify();
	}

	private Process _exec(String[] args) throws Throwable
	{
		if(java13exec != null)
		{
			try
			{
				Object[] params = { args, null, new File(dir) };
				return (Process)java13exec.invoke(Runtime.getRuntime(),params);
			}
			catch(InvocationTargetException e)
			{
				throw e.getTargetException();
			}
		}
		else
			return Runtime.getRuntime().exec(args);
	}

	class StdoutThread extends Thread
	{
		StdoutThread()
		{
			setName(StdoutThread.class + "[" + command + "]");
			start();
		}

		public void run()
		{
			try
			{
				BufferedReader in = new BufferedReader(
					new InputStreamReader(process
					.getInputStream()));

				String line;
				while((line = in.readLine()) != null)
				{
					parseLine(line);
				}
				in.close();
			}
			catch(IOException io)
			{
				String[] args = { io.getMessage() };
				console.printError(jEdit.getProperty("console.shell.ioerror",args));
			}
			finally
			{
				threadDone();
			}
		}
	}

	class StderrThread extends Thread
	{
		StderrThread()
		{
			setName(StderrThread.class + "[" + command + "]");
			start();
		}

		public void run()
		{
			try
			{
				// If process exits really fast, it could
				// be null by now. So check first...
				if(process == null)
					return;

				BufferedReader in = new BufferedReader(
					new InputStreamReader(process
					.getErrorStream()));

				String line;
				while((line = in.readLine()) != null)
				{
					parseLine(line);
				}
				in.close();
			}
			catch(IOException io)
			{
				String[] args = { io.getMessage() };
				console.printError(jEdit.getProperty("console.shell.ioerror",args));
			}
			finally
			{
				threadDone();
			}
		}
	}
}
