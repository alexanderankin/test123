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

//{{{ Imports
import org.gjt.sp.jedit.*;
import java.io.*;
import java.net.URL;
//}}}

public class CommandoCommand extends EditAction
{
	//{{{ CommandoCommand constructor
	public CommandoCommand(String name, URL url)
	{
		super("commando." + name.replace(' ','_'));

		jEdit.setTemporaryProperty(getName() + ".label",name);
		this.url = url;
		this.propertyPrefix = getName() + '.';
	} //}}}

	//{{{ CommandoCommand constructor
	public CommandoCommand(String name, String path)
	{
		super("commando." + name.replace(' ','_'));

		jEdit.setTemporaryProperty(getName() + ".label",name);
		this.path = path;
		this.propertyPrefix = getName() + '.';
	} //}}}

	//{{{ getPropertyPrefix() method
	public String getPropertyPrefix()
	{
		return propertyPrefix;
	} //}}}

	//{{{ invoke() method
	public void invoke(View view)
	{
		new CommandoDialog(view,getName());
	} //}}}

	//{{{ getCode() method
	public String getCode()
	{
		return "new console.commando.CommandoDialog(view,\"" + getName() + "\");";
	} //}}}

	//{{{ openStream() method
	public Reader openStream() throws IOException
	{
		if(url != null)
		{
			return new BufferedReader(new InputStreamReader(
				url.openStream()));
		}
		else
		{
			return new BufferedReader(new FileReader(path));
		}
	} //}}}

	//{{{ Private members
	private URL url;
	private String path;
	private String propertyPrefix;
	//}}}
}
