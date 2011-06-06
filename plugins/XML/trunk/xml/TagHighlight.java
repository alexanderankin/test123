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
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.Buffer;
import sidekick.SideKickActions;
import xml.parser.javacc.TagParser;
//}}}

public class TagHighlight implements StructureMatcher
{
	//{{{ getMatch() method
	public StructureMatcher.Match getMatch(TextArea textArea)
	{
		if(XmlPlugin.isDelegated(textArea)) {
			return null;
		}

		int caret = textArea.getCaretPosition();
		String text = textArea.getText();
		Buffer buffer = (Buffer)textArea.getBuffer();
		TagParser.Tag current = TagParser.getTagAtOffset(buffer,text,caret);

		if(current == null) {
			return null;
		}
		else
		{
			TagParser.Tag tag = TagParser.getMatchingTag(buffer,text,current);
			if(tag != null)
			{
				tag.startLine = textArea.getLineOfOffset(tag.start);
				tag.endLine = textArea.getLineOfOffset(tag.end);
				tag.matcher = this;
			}
			return tag;
		}
	} //}}}

	//{{{ selectMatch() method
	/**
	 * Selects from the caret to the matching structure element (if there is
	 * one, otherwise the behavior of this method is undefined).
	 * @since jEdit 4.2pre3
	 */
	public void selectMatch(TextArea textArea)
	{
		SideKickActions.selectAsset(GUIUtilities.getView(textArea));
	} //}}}
}
