/*
 * TagHighlight.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (c) 2002, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;
import xml.parser.*;
//}}}

public class TagHighlight implements StructureMatcher
{
	//{{{ getMatch() method
	public StructureMatcher.Match getMatch(JEditTextArea textArea)
	{
		if(XmlPlugin.isDelegated(textArea))
			return null;

		int caret = textArea.getCaretPosition();
		String text = textArea.getText();
		TagParser.Tag current = TagParser.getTagAtOffset(text,caret);

		if(current == null)
			return null;
		else
			return TagParser.getMatchingTag(text,current);
	} //}}}
}
