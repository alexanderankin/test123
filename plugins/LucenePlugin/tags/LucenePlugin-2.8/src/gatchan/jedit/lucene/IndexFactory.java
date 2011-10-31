/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
 * Copyright (C) 2009, 2011 Shlomy Reinstein
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.lucene;

import java.util.HashMap;
import java.io.File;
import java.lang.reflect.Constructor;

import org.gjt.sp.util.Log;

public class IndexFactory
{
	private static final HashMap<String, Class<? extends Index>> indexes =
		new HashMap<String, Class<? extends Index>>();

	static
	{
		register("File-based index", IndexImpl.class);
		register("Line-based index", LineIndexImpl.class);
	}

	// Returns the type name of the given index
	public static String getType(Index index)
	{
		Class<? extends Index> c = index.getClass();
		for (String type : indexes.keySet())
		{
			if (indexes.get(type) == c)
				return type;
		}
		return null;
	}

	public static void register(String name, Class<? extends Index> cls)
	{
		indexes.put(name, cls);
	}

	public static String[] getIndexNames()
	{
		String[] names = new String[indexes.size()];
		indexes.keySet().toArray(names);
		return names;
	}

	public static Index createIndex(String type, String name, File path)
	{
		Class<? extends Index> c = indexes.get(type);
		if (c == null)
			return null;
		Index index = null;
		try
		{
			Constructor<? extends Index> constructor = c.getConstructor(String.class, File.class);
			index = constructor.newInstance(name, path);
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, IndexFactory.class, "Unable to instantiate a new index", e);
		}
		return index;
	}

	private IndexFactory()
	{
	}
}
