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
import java.io.Reader;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.textarea.StructureMatcher;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.Log;
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
		long startTime = System.currentTimeMillis();
		
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
				/* FIXME: should a readlock() be held during the whole parsing since
				 * it's not a copy of the buffer's content but the live content that we get ?
				 * */
				JEditBuffer b =  textArea.getBuffer();
				int max = b.getLength();
				int sA = asset.getStart().getOffset();
				int lA = asset.getEnd().getOffset()-sA;
				if(sA < 0 || sA > max){
					return null;
				}
				if(lA < 0 || sA+lA > max){
					return null;
				}
				CharSequence toParse = b.getSegment(sA,lA);
				int line = textArea.getLineOfOffset(sA);
				Reader r = new CharSequenceReader(toParse);
				XmlParser parser = new XmlParser(r,line+1,
					sA-textArea.getLineStartOffset(line)+1);
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
			Log.log(Log.DEBUG, SideKickTagHighlight.class, "highlighting matching tag has taken "+(System.currentTimeMillis()-startTime)+"ms");
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
