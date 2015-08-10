/*
 * IDDecl.java
 * :tabSize=8:indentSize=8:noTabs=false:
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

package xml.parser;

//{{{ Imports
import javax.swing.text.Position;
import org.gjt.sp.jedit.MiscUtilities;
//}}}

public class IDDecl
{
	public String id;
	public String element;
	public Position declaringLocation;

	//{{{ IDDecl constructor
	public IDDecl(String id, String element, Position declaringLocation)
	{
		this.id = id;
		this.element = element;
		this.declaringLocation = declaringLocation;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return id + " [element: <" + element + ">]";
	} //}}}

	//{{{ Compare class
	public static class Compare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			IDDecl id1 = (IDDecl)obj1;
			IDDecl id2 = (IDDecl)obj2;

			return MiscUtilities.compareStrings(
				id1.id,
				id2.id,true);
		}
	} //}}}
}
