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
import org.gjt.sp.util.Log;
import sidekick.SideKickActions;
import xml.parser.javacc.TagParser;
//}}}

public class TagHighlight implements StructureMatcher
{
	//{{{ getMatch() method
	public StructureMatcher.Match getMatch(TextArea textArea)
	{
		long start = System.currentTimeMillis();

		if(XmlPlugin.isDelegated(textArea)) {
			return null;
		}
		
		// FIXME: should a readLock() be held ?
		int caret = textArea.getCaretPosition();
		Buffer buffer = (Buffer)textArea.getBuffer();
		CharSequence text = buffer.getSegment(0, buffer.getLength());
		// too bad that we first retrieve the current tag, parsing the whole buffer
		// and then reparse again to get the matching tag...
		// Also, the first parse could get only the buffer up to caret or
		// caret +1000 characters to get a full tag and then the relevant part of the buffer's content (before or after).
		// this would speed up matching near the begining of a big buffer
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
			Log.log(Log.DEBUG, TagHighlight.class, "matching tag parsing the full buffer has taken "+(System.currentTimeMillis()-start)+"ms");
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
