/*
 * ConsoleShell.java - Console shell
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

import java.io.*;
import org.gjt.sp.jedit.*;

public class ConsoleShell implements Shell
{
	public void printInfoMessage(Output output)
	{
		output.printInfo(jEdit.getProperty("console.shell.info"));
	}

	public void execute(View view, String command, Output output)
	{
		stop();
		ConsoleShellPluginPart.clearErrors();

		String osName = System.getProperty("os.name");
		boolean appendEXE = (osName.indexOf("Windows") != -1 ||
			osName.indexOf("OS/2") != -1);

		if(appendEXE)
		{
			// append .exe to command name on Windows and OS/2
			int spaceIndex = command.indexOf(' ');
			if(spaceIndex == -1)
				spaceIndex = command.length();
			int dotIndex = command.indexOf('.');
			if(dotIndex == -1 || dotIndex > spaceIndex)
			{
				command = command.substring(0,spaceIndex)
					+ ".exe" + command.substring(spaceIndex);
			}
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
						buf.append(MiscUtilities.getFileParent(
							buffer.getPath()));
						break;
					case 'u':
						String path = buffer.getPath();
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
			}
		}

		command = buf.toString();

		try
		{
			process = Runtime.getRuntime().exec(command);
			process.getOutputStream().close();
		}
		catch(IOException io)
		{
			String[] args = { io.getMessage() };
			output.printInfo(jEdit.getProperty("console.shell.ioerror",args));
			return;
		}

		this.command = command;
		this.output = output;

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
			output.printError(jEdit.getProperty("console.shell.killed",args));

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
	private Output output;
	private Process process;
	private Thread stdout;
	private Thread stderr;

	private boolean exitStatus;

	private void parseLine(String line)
	{
		int type = ConsoleShellPluginPart.parseLine(line);
		switch(type)
		{
		case ErrorSource.ERROR:
			output.printError(line);
			break;
		case ErrorSource.WARNING:
			output.printWarning(line);
			break;
		default:
			output.printPlain(line);
			break;
		}
	}

	private synchronized void commandDone()
	{
		command = null;
		stdout = null;
		stderr = null;
		process = null;

		notify();
	}

	class StdoutThread extends Thread
	{
		StdoutThread()
		{
			setName(getName() + "[" + command + "]");
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

				int exitCode = process.waitFor();
				Object[] args = { command, new Integer(exitCode) };

				output.printInfo(jEdit.getProperty("console.shell.exited",args));
				exitStatus = (exitCode == 0);
				commandDone();
			}
			catch(IOException io)
			{
				String[] args = { io.getMessage() };
				output.printError(jEdit.getProperty("console.shell.ioerror",args));
			}
			catch(InterruptedException ie)
			{
			}
		}
	}

	class StderrThread extends Thread
	{
		StderrThread()
		{
			setName(getClass().getName() + "[" + command + "]");
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
				output.printError(jEdit.getProperty("console.shell.ioerror",args));
			}
		}
	}
}
