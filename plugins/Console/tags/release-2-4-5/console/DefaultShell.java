/*
 * DefaultShell.java - Executes OS commands
 * Copyright (C) 1999, 2000 Slava Pestov
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
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

class DefaultShell extends Shell
{
	public DefaultShell()
	{
		super("Console");

		try
		{
			Class[] classes = { String.class, String[].class, File.class };
			java13exec = Runtime.class.getMethod("exec",classes);
		}
		catch(Exception e)
		{
			// do nothing
		}

		dir = System.getProperty("user.dir");
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

		if(command.equals("pwd"))
		{
			console.printPlain(dir);
			return;
		}

		// Expand variables
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < command.length(); i++)
		{
			char c = command.charAt(i);
			switch(c)
			{
			case '$':
				if(i == command.length() - 1)
					buf.append(c);
				else
				{
					Buffer buffer = view.getBuffer();
					switch(command.charAt(++i))
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
				if(i == command.length() - 1)
				{
					buf.append(System.getProperty("user.home"));
					break;
				}
				c = command.charAt(i + 1);
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

		command = buf.toString();

		if(command.startsWith("cd "))
		{
			if(java13exec == null)
				console.printError(jEdit.getProperty("console.shell.cd-unsup"));
			else
			{
				String newDir = command.substring(3).trim();
				if(newDir.equals(".."))
					newDir = MiscUtilities.getParentOfPath(dir);
				else
					newDir = MiscUtilities.constructPath(dir,newDir);
				String[] args = { newDir };
				if(new File(newDir).exists())
				{
					dir = newDir;
					console.printPlain(jEdit.getProperty("console.shell.cd",args));
				}
				else
					console.printError(jEdit.getProperty("console.shell.cd-error",args));
			}
			return;
		}

		String osName = System.getProperty("os.name");
		boolean appendEXE = (osName.indexOf("Windows") != -1 ||
			osName.indexOf("OS/2") != -1);

		// this will be set to true if adding a Windows extension
		// to the executable file name works
		boolean alreadyDone = false;
		// On Windows and OS/2, try running <command>.bat,
		// then <command>.exe
		if(appendEXE)
		{
			// first, let's deal with some built-in Windows commands
			// and pass them to an instance of the Windows command
			// interpreter
			if(IsWinBuiltIn(command))
			{
				// which command interpreter?
				String cmdInterp;
				if(osName.indexOf("Windows 9") != -1)
					cmdInterp = new String("command.com /C ");
				else cmdInterp = new String("cmd.exe /C ");
				command = cmdInterp + command;
			}

			int spaceIndex = command.indexOf(' ');
			if(spaceIndex == -1)
				spaceIndex = command.length();
			int dotIndex = command.indexOf('.');
			if(dotIndex == -1 || dotIndex > spaceIndex)
			{
				String[] extensionsToTry = { ".cmd", ".bat", ".com", ".exe" };
				for(int i = 0; i < extensionsToTry.length; i++)
				{
					try
					{
						String newCommand = command.substring(
							0,spaceIndex) + extensionsToTry[i]
							+ command.substring(spaceIndex);
						process = _exec(newCommand);
						process.getOutputStream().close();
						alreadyDone = true;
						break;
					}
					catch(IOException io)
					{
					}
					catch(Throwable t)
					{
						Log.log(Log.ERROR,this,t);
						return;
					}
				}
			}
		}

		if(!alreadyDone)
		{
			try
			{
				process = _exec(command);
				process.getOutputStream().close();
			}
			catch(IOException io)
			{
				String[] args = { io.getMessage() };
				console.printInfo(jEdit.getProperty("console.shell.ioerror",args));
				return;
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,t);
			}
		}

		this.command = command;
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
	private String command;
	private Console console;
	private Process process;
	private Thread stdout;
	private Thread stderr;
	private String dir;
	private Method java13exec;

	private int threadDoneCount;
	private boolean exitStatus;

	// used to store built-in commands
	private Hashtable tableWinBuiltIns;

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

	private boolean IsWinBuiltIn(String command)
	{
		String com = command.trim();
		final int i1 = com.indexOf(' ');
		final int i2 = com.indexOf('.');
		final int i3 = com.indexOf('\\');
		final int i4 = com.indexOf('\"');
		final int cLen = com.length();
		int pos = cLen;
		if( i1 != -1 && i1 < pos) pos = i1;
		if( i2 != -1 && i2 < pos) pos = i2;
		if( i3 != -1 && i3 < pos) pos = i3;
		if( i4 != -1 && i4 < pos) pos = i4;
		String builtIn = com.substring( 0, pos);
		if( tableWinBuiltIns.get( builtIn) != null)
		{
			int bLen = builtIn.length();
			if( cLen == bLen)
				return true;
			final char c = com.charAt(bLen);
			if( c == ' ' || c == '\\' || c == '\"')
				return true;
			if( c == '.')
			{
				if( cLen == bLen)
					return true;
				final char cc = com.charAt(++bLen);
				if( cc == '.' || cc == '\\')
					return true;
			}
		}
		return false;
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

	private Process _exec(String command) throws Throwable
	{
		if(java13exec != null)
		{
			try
			{
				Object[] args = { command, null, new File(dir) };
				return (Process)java13exec.invoke(Runtime.getRuntime(),args);
			}
			catch(InvocationTargetException e)
			{
				throw e.getTargetException();
			}
		}
		else
			return Runtime.getRuntime().exec(command);
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
