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

package xml.completion;

//{{{ Imports
import java.util.Comparator;
import org.gjt.sp.jedit.MiscUtilities;
//}}}

public class IDDecl
{
	public String uri;
	public String id;
	public String element;
	public int line;
	public int column;

	//{{{ IDDecl constructor
	public IDDecl(String uri, String id, String element, int line, int column)
	{
		this.uri = uri;
		this.id = id;
		this.element = element;
		this.line = line;
		this.column = column;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return id + " [element: <" + element + ">]";
	} //}}}

	//{{{ Compare class
	public static class Compare implements Comparator<IDDecl>
	{
		public int compare(IDDecl id1, IDDecl id2)
		{
			return MiscUtilities.compareStrings(
				id1.id,
				id2.id,true);
		}
	} //}}}
}
