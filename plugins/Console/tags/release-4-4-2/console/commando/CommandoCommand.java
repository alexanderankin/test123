/*
 * CommandoCommand.java - Commando command wrapper
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2000, 2001, 2002 Slava Pestov
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

package console.commando;

// {{{ Imports
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StringList;

import console.Console;

// }}}


/**
 * An EditAction which is intended to be used in the Console Commando.
 * Associated with an .xml file which may be inside a jar, or may be in the
 * user dir.
 * 
 */
public class CommandoCommand extends EditAction
{

	// {{{ isUser()
	/**
	 * 
	 * @return true if userdefined
	 */
	public boolean isUser()
	{
		return (url == null);
	}

	/**
	 * 
	 * @return true for user commands that override a command 
	 *     with the same name in the jar. 
	 */
	public boolean isOverriding() 
	{
		if (!isUser()) return false;
		String defaultCommands = jEdit.getProperty("commando.default");
		StringList sl = StringList.split(defaultCommands, " ");
		String cmdName = name.replace("commando.", "");
		return sl.contains(cmdName);
	}
	// }}}

	// {{{ create()
	public static CommandoCommand create(URL url)
	{
		String l = shortLabel(url.getPath());
		CommandoCommand retval = new CommandoCommand(l, url.getPath());
		retval.url = url;
		return retval;
	}

	public static CommandoCommand create(String path)
	{
		String l = shortLabel(path);
		File f = new File(path);
		if (f.canRead())
		{
			return new CommandoCommand(l, path);
		}
		else
			throw new RuntimeException("path: " + path + " abs: " + f.getAbsolutePath());
	}

	// }}}

	// {{{ shortLabel
	/**
	 * @return the short label - for button text
	 */
	public String getShortLabel()
	{
		return label;
	}

	/**
	 * @param path
	 *                an absolute path to a resource
	 * @return the short label on for a button text
	 */

	static String shortLabel(String path)
	{
		Matcher m = p.matcher(path);
		m.find();
		String name = m.group(1);
		name = name.replace('_', ' ');
		return name;
	}

	// }}}

	// {{{ Constructor (private )

	private CommandoCommand(String shortLabel, String path)
	{
		super("commando." + shortLabel);
		label = shortLabel;
		// Log.log(Log.WARNING, this, "New command: " + label + " path:
		// " + path);
		this.path = path;
		this.propertyPrefix = getName() + '.';
		jEdit.setTemporaryProperty(getName() + ".label", label);
	}

	// }}}

	// {{{ getPropertyPrefix() method
	public String getPropertyPrefix()
	{
		return propertyPrefix;
	} // }}}

	// {{{ invoke() method
	public void invoke(View view)
	{
		new CommandoDialog(view, getName());
	} // }}}

	// {{{ getCode() method
	public String getCode()
	{
		return "new console.commando.CommandoDialog(view,\"" + getName() + "\");";
	} // }}}

	// {{{ openStream() method
	protected Reader openStream() throws IOException
	{
		if (url != null)
		{
			return new BufferedReader(new InputStreamReader(url.openStream()));
		}
		else
		{
			return new BufferedReader(new FileReader(path));
		}
	} // }}}

	// {{{ Private members
	private URL url = null;

	private String label;

	private String path;

	private String propertyPrefix;

	// }}}

	// {{{ static private members
	private static final String pattern = "([^\\\\\\./]+)\\.xml$";

	private static final Pattern p = Pattern.compile(pattern);

	// }}}

}
