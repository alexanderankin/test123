/*
 * CommandoCommand.java - Commando command wrapper
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

import java.io.*;
import java.net.URL;

class CommandoCommand
{
	String name;
	URL url;
	String path;

	CommandoCommand(String name, URL url)
	{
		this.name = name;
		this.url = url;
	}

	CommandoCommand(String name, String path)
	{
		this.name = name;
		this.path = path;
	}

	Reader openStream() throws IOException
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
	}

	public String toString()
	{
		return name;
	}
}
