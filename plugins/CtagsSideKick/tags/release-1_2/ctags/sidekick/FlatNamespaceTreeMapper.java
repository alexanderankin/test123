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

package ctags.sidekick;
import java.util.Hashtable;
import java.util.Vector;


public class FlatNamespaceTreeMapper extends NamespaceTreeMapper
{
	public Vector<Object> getPath(Tag tag)
	{
		Vector<Object> path = new Vector<Object>();
		Hashtable info = tag.getInfo();
		for (int i = 0; i < Keywords.length; i++)
		{
			String ns = (String)info.get(Keywords[i]);
			if (ns != null)
			{
				// If the tag is also a namespace, concatenate it
				// to its own namespace.
				boolean tagIsNamespace = false;
				String kind = (String) info.get("kind");
				if (kind != null && kind.length() > 0)
				{
					for (int j = 0; j < Keywords.length; j++)
					{
						if (kind.equals(Keywords[j]))
						{
							tagIsNamespace = true;
							break;
						}
					}
				}
				if (tagIsNamespace)
					tag.setShort(ns + separator + tag.getShortString());
				else
					path.add(ns);
				break;
			}
		}
		path.add(tag);
		return path;		
	}

}
