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

		aliases = new Hashtable();

		String osName = System.getProperty("os.name");
		dos = (osName.indexOf("Windows") != -1 ||
			osName.indexOf("OS/2") != -1);

		// some built-ins can be invoked without the % prefix
		aliases.put("cd","%cd");
		aliases.put("pwd","%pwd");
		aliases.put("clear","%clear");

		// on Windows, we need a special calling convention to run system
		// shell built-ins
		if(dos)
		{
			String prefix;
			if(System.getProperty("os.name").indexOf("Windows 9") != -1)
				prefix = "command.com /C ";
			else
				prefix = "cmd.exe /C ";

			String[] builtins  = { "md", "rd", "del", "dir", "copy",
				"move", "erase", "mkdir", "rmdir", "start",
				"path", "ver", "vol", "ren", "type"};
			for(int i = 0; i < builtins.length; i++)
			{
				aliases.put(builtins[i],prefix + builtins[i]);
			}
		}
	}

	public void printInfoMessage(Console console)
	{
		console.printInfo(jEdit.getProperty("console.shell.info"));
	}

	public void execute(View view, String command, Console console)
	{
		Vector args = parse(command);
		// will be null if the command is an empty string
		if(args == null)
			return;

		args = preprocess(view,args);

		String commandName = (String)args.elementAt(0);
		if(commandName.charAt(0) == '%')
		{
			// a console built-in
			DefaultShellBuiltIns.executeBuiltIn(view,
				commandName.substring(1),args,console);
		}
		else
		{
			// pass it to the process manager
			if(args.elementAt(args.size() - 1).equals("&"))
			{
				// run in background
				args.removeElementAt(args.size() - 1);
			}
			else
			{
				// run in foreground
			}
		}
	}

	public synchronized boolean waitFor()
	{
		// TODO
		return true;
	}

	// private members
	private static final char dosSlash = 127;
	private boolean dos;
	private Hashtable aliases;

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
	}

	/**
	 * Expand aliases, variables and globs.
	 */
	private Vector preprocess(View view, Vector args)
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
	}

	private void expandGlobs(View view, Vector args, String arg)
	{
		// XXX: to do
		args.addElement(expandVariables(view,arg));
	}

	private String expandVariables(View view, String arg)
	{
		StringBuffer buf = new StringBuffer();

		for(int i = 0; i < arg.length(); i++)
		{
			char c = arg.charAt(i);
			switch(c)
			{
			case dosSlash:
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
			default:
				buf.append(c);
				break;
			}
		}

		return buf.toString();
	}
}
