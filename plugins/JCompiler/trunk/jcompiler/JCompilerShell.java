/*
 * JCompilerShell.java - ConsolePlugin shell for JCompiler
 * Copyright (C) 2000 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package jcompiler;


import java.io.*;
import java.util.Vector;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import errorlist.DefaultErrorSource;
import console.Console;
import console.Output;
import console.Shell;


/**
 * a JCompiler shell for the Console plugin.
 */
public class JCompilerShell extends Shell
{

	public JCompilerShell()
	{
		super("JCompiler");
	}


	// ----- Begin Shell implementation -----

	/**
	 * print an information message.
	 *
	 * @param  output  where to put the information
	 */
	public void printInfoMessage(Output output)
	{
		output.print(null, jEdit.getProperty("jcompiler.msg.info"));
	}


	/**
	 * execute a command.
	 *
	 * @param  console  the Console where the command was entered.
	 * @param  output  where the output should go.
	 * @param  command  the entered command.
	 */
	public void execute(Console console, Output output, String command)
	{
		stop(console); // stop last command

		String cmd = command.trim();
		DefaultErrorSource errorSource = console.getErrorSource();

		if ("compile".equals(cmd))
		{
			errorSource.clear();
			compileTask = new JCompilerTask(false, false, console, output, errorSource);
		}
		else if ("compilepkg".equals(cmd))
		{
			errorSource.clear();
			compileTask = new JCompilerTask(true, false, console, output, errorSource);
		}
		else if ("rebuildpkg".equals(cmd))
		{
			errorSource.clear();
			compileTask = new JCompilerTask(true, true, console, output, errorSource);
		}
		else if ("javac".equals(cmd))
		{
			// command "javac" invoked without arguments,
			// prints javac usage
			compileTask = new JCompilerTask(new String[] {}, console, output, errorSource);
		}
		else if (cmd.startsWith("javac "))
		{
			// command "javac" with arguments
			String[] args;
			try
			{
				args = parseCmdLineArguments(cmd.substring(6));
			}
			catch (IOException ex)
			{
				console.print(console.getErrorColor(),
					jEdit.getProperty("jcompiler.msg.errorCommandLine",
						new Object[] { ex }));
				return;
			}

			errorSource.clear();
			compileTask = new JCompilerTask(args, console, output, errorSource);
		}
		else if ("help".equals(cmd))
		{
			printInfoMessage(output);
			output.commandDone();
		}
		else
		{
			console.print(console.getInfoColor(),
				jEdit.getProperty("jcompiler.msg.errorUnknownCommand",
					new Object[] { cmd }));
			console.commandDone();
		}
	}


	public void stop(Console console)
	{
		if (compileTask != null)
		{
			if (compileTask.isAlive())
			{
				console.print(console.getErrorColor(), jEdit.getProperty("jcompiler.msg.stopping"));
				compileTask.stop();
				console.commandDone();
			}
			compileTask = null;
		}
	}


	public boolean waitFor(Console console)
	{
		if (compileTask != null)
		{
			try
			{
				synchronized(compileTask)
				{
					compileTask.wait();
				}
			}
			catch (InterruptedException ie)
			{
				return false;
			}
			compileTask = null;
		}
		return true;
	}

	// ----- End Shell implementation -----


	private String[] parseCmdLineArguments(String cmd) throws IOException
	{
		// Expand any variables in the command line arguments:
		cmd = JCompiler.expandVariables(cmd);

		// The following is stolen from Slava Pestov's SystemShell.java (Console plugin):

		// We replace \ with a non-printable char because StreamTokenizer
		// handles \ specially, which causes problems on Windows as \ is the
		// file separator there. After parsing is done, the non printable
		// char is changed to \ once again.
		cmd = cmd.replace('\\', NON_PRINTABLE);

		StreamTokenizer st = new StreamTokenizer(new StringReader(cmd));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		Vector args = new Vector();

loop:
		for(;;)
		{
			switch(st.nextToken())
			{
				case StreamTokenizer.TT_EOF:
					break loop;
				case StreamTokenizer.TT_WORD:
				case '"':
				case '\'':
					args.addElement(st.sval.replace(NON_PRINTABLE, '\\'));
					break;
			}
		}

		Log.log(Log.DEBUG, this, "arguments=" + args);

		String[] array = new String[args.size()];
		args.copyInto(array);
		return array;
	}


	private JCompilerTask compileTask;
	private static final char NON_PRINTABLE = 127;

}
