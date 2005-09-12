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
import org.gjt.sp.util.Log;

// }}}

public class CommandoCommand extends EditAction {

	static final String pattern = "([^\\./]+)\\.xml$";
	static final Pattern p = Pattern.compile(pattern);
	String label;
	
	/**
	 * @return the short label - what you can put on the button
	 */
	public String getShortLabel() {
		return label;
	}
	
	void setCommandName(String path) {
		Matcher m = p.matcher(path);
		m.find();
		String name = m.group(1);
		name = name.replace('_', ' ');
		label = name;
		setName("commando." + label);
		Log.log(Log.WARNING, this, "New command: " + label + " path: " + path);
		this.path = path;
		this.propertyPrefix = getName() + '.';
		jEdit.setTemporaryProperty(getName() + ".label", label);
	}
	
	public CommandoCommand(URL url) {
		super("unknown");
		setCommandName(url.getPath());
		this.url = url;
	} // }}}

	// {{{ CommandoCommand constructor
	public CommandoCommand(String path) {
		super("unknown");
		setCommandName(path);
	} // }}}

	// {{{ getPropertyPrefix() method
	public String getPropertyPrefix() {
		return propertyPrefix;
	} // }}}

	// {{{ invoke() method
	public void invoke(View view) {
		new CommandoDialog(view, getName());
	} // }}}

	// {{{ getCode() method
	public String getCode() {
		return "new console.commando.CommandoDialog(view,\"" + getName()
				+ "\");";
	} // }}}

	// {{{ openStream() method
	public Reader openStream() throws IOException {
		if (url != null) {
			return new BufferedReader(new InputStreamReader(url.openStream()));
		} else {
			return new BufferedReader(new FileReader(path));
		}
	} // }}}

	// {{{ Private members
	private URL url;

	private String path;

	private String propertyPrefix;
	// }}}
}
