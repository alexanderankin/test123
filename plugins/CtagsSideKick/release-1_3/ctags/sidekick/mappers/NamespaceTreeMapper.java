/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ctags.sidekick.mappers;
import java.util.Hashtable;
import java.util.Vector;

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;

public class NamespaceTreeMapper extends AbstractTreeMapper {

	private static final String NAME = "Namespace";
	private static final String DESCRIPTION =
		"Adds the tag namespace (multi-level) to the tree path.";
	
	static final String [] Keywords = {
		"namespace", "class", "union", "struct", "enum"
	};
	
	String separator, separatorRegExp;

	public NamespaceTreeMapper(String name, String description) {
		super(name, description);
	}

	public NamespaceTreeMapper() {
		super(NAME, DESCRIPTION);
	}
	
	public void setLang(String lang)
	{
		if (lang.equals("c++") || lang.equals("c"))
		{
			separatorRegExp = separator = "::";
		}
		else
		{
			separator = ".";
			separatorRegExp = "\\.";
		}			
	}
	public Vector<Object> getPath(Tag tag)
	{
		Vector<Object> path = new Vector<Object>();
		Hashtable info = tag.getInfo();
		for (int i = 0; i < Keywords.length; i++)
		{
			String ns = (String)info.get(Keywords[i]);
			if (ns != null)
			{
				String [] parts = ns.split(separatorRegExp);
				for (int j = 0; j < parts.length; j++)
					path.add(parts[j]);
				break;
			}
		}
		return path;		
	}
	public IObjectProcessor getClone() {
		return new NamespaceTreeMapper();
	}
}
