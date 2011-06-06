/*
 * SideKickTagHighlight.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (c) 2011 Eric Le Lay
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
import java.io.StringReader;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import sidekick.SideKickActions;
import sidekick.SideKickParsedData;
import sidekick.IAsset;
import sidekick.util.ElementUtil;
import xml.parser.javacc.*;
import xml.parser.XmlTag;
//}}}

public class SideKickTagHighlight implements StructureMatcher
{
	//{{{ getMatch() method
	public StructureMatcher.Match getMatch(TextArea textArea)
	{
		if(XmlPlugin.isDelegated(textArea)) {
			return null;
		}

		int caret = textArea.getCaretPosition();
		SideKickParsedData parsedData = SideKickParsedData.getParsedData(((JEditTextArea)textArea).getView());

		if(parsedData == null || !(parsedData instanceof XmlParsedData)) {
			return null;
		}
		else
		{
			TagParser.Tag tag = null;
			IAsset asset = parsedData.getAssetAtOffset(caret);
			if(asset != null)
			{
				/* use the XML javacc parser to parse the start tag.
				 * If the caret is inside the start tag,
				 * then look for the '<' from the end of the region,
				 * to get easily the end tag (there is nothing to check for in the
				 * end tag : no quoted attributes and so on).
				 * On the contrary, when looking for '>' from the start of the region,
				 * one has to take care of '>' in attribute values, so it's better to simply
				 * use the javacc parser.
				 * */
				/* FIXME: copy of a potentially big portion of the buffer.
				 * It's consumed by a streaming client (javacc parser) or read char by char from the end.
				 * */
				String toParse = textArea.getBuffer().getText(
					asset.getStart().getOffset(),
					asset.getEnd().getOffset()-asset.getStart().getOffset());
				System.err.println(toParse);
				System.err.println("-------------------");
				int line = textArea.getLineOfOffset(asset.getStart().getOffset());
				StringReader r = new StringReader(toParse);
				XmlParser parser = new XmlParser(r,line+1,
					asset.getStart().getOffset()-textArea.getLineStartOffset(line)+1);
				try{
					XmlDocument.XmlElement startTag = parser.Tag();
					System.err.println("startL="+startTag.getStartLocation()+",endL="+startTag.getEndLocation());
					int start = ElementUtil.createStartPosition((Buffer)textArea.getBuffer(),startTag).getOffset();
					int end= ElementUtil.createEndPosition((Buffer)textArea.getBuffer(),startTag).getOffset();
					tag = new TagParser.Tag(
						start,
						end);
					System.err.println("start="+start+",end"+end);
					/* if the caret is inside start tag */
					if(caret <= end)
					{
						for(int i=toParse.length()-2;i>0;i--){
							if(toParse.charAt(i) == '<'){
								tag.start = start + i;
								tag.end = start + toParse.length()-1;
								break;
							}else if(toParse.charAt(i) == '>'){
								return null;
							}
						}
					}
					tag.startLine = textArea.getLineOfOffset(tag.start);
					tag.endLine = textArea.getLineOfOffset(tag.end);
					tag.matcher = this;
					
				}catch(ParseException pe){
					//FIXME: why should a PE be thrown ?
					throw new RuntimeException(pe);
				}
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
