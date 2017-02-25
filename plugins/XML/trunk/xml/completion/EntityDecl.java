/*
 * EntityDecl.java
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.completion;

import org.gjt.sp.util.StandardUtilities;

import java.util.Comparator;

public class EntityDecl
{
	public static final int INTERNAL = 0;
	public static final int EXTERNAL = 1;

	public int type;

	public String name;
	public String value;
	public String publicId;
	public String systemId;
	public String source;
	public int line;
	public int col;

	//{{{ EntityDecl constructor
	public EntityDecl(int type, String name, String value, String source, int line, int col)
	{
		this.type = type;
		this.name = name;
		this.value = value;
		this.source = source;
		this.line = line;
		this.col = col;
	} //}}}

	//{{{ EntityDecl constructor
	public EntityDecl(int type, String name, String publicId, String systemId, String source, int line, int col)
	{
		this.type = type;
		this.name = name;
		this.publicId = publicId;
		this.systemId = systemId;
		this.source = source;
		this.line = line;
		this.col = col;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		if(type == INTERNAL)
			return getClass().getName() + "[" + name + "," + value + "]";
		else if(type == EXTERNAL)
			return getClass().getName() + "[" + name
				+ "," + publicId + "," + systemId + "]";
		else
			return null;
	} //}}}

	//{{{ Compare class
	public static class Compare implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			EntityDecl entity1 = (EntityDecl)obj1;
			EntityDecl entity2 = (EntityDecl)obj2;

			if(entity1.type != entity2.type)
				return entity2.type - entity1.type;
			else
			{
				return StandardUtilities.compareStrings(
					entity1.name,
					entity2.name,true);
			}
		}
	} //}}}
}
