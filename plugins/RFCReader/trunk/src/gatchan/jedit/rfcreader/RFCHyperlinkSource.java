/*
 * RFCHyperlinkSource.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.rfcreader;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.jedit.hyperlinks.HyperlinkSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;

/**
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class RFCHyperlinkSource implements HyperlinkSource
{
	private static final String NO_WORD_SEP = "";

	private Hyperlink currentLink;

	//{{{ getHyperlink() method
	public Hyperlink getHyperlink(Buffer buffer, int caretPosition)
	{
		if (currentLink != null)
		{
			if (currentLink.getStartOffset() <= caretPosition && currentLink.getEndOffset() >= caretPosition)
			{
				return currentLink;
			}
		}
		int line = buffer.getLineOfOffset(caretPosition);
		int lineStart = buffer.getLineStartOffset(line);
		int lineLength = buffer.getLineLength(line);
		if (lineLength == 0)
			return null;
		int offset = caretPosition - lineStart;
		String lineText = buffer.getLineText(line);
		if (offset == lineLength)
			offset--;
		
		int wordStart = TextUtilities.findWordStart(lineText, offset,
							    NO_WORD_SEP, true, false, false);
		int wordEnd = TextUtilities.findWordEnd(lineText, offset + 1,
							NO_WORD_SEP, true, false, false);
		
		
		String currentWord = lineText.substring(wordStart, wordEnd).toLowerCase();
		int rfcNum;
		
		// todo : rewrite this crap 
		if (currentWord.equals("rfc"))
		{
			int rfcStart = -1;
			int rfcEnd = -1;
			for (int i = wordEnd;i<lineText.length();i++)
			{
				char ch = lineText.charAt(i);
				if (Character.isWhitespace(ch))
					continue;
				
				if (Character.isDigit(ch))
				{
					if (rfcStart == -1)
					{
						rfcStart = i;
					}
					rfcEnd = i+1;
					continue;
				}
				break;
			}
			if (rfcStart != -1)
			{
				rfcNum = Integer.parseInt(lineText.substring(rfcStart, rfcEnd));
				wordEnd = rfcEnd;
			}
			else
				return null;
		}
		else if (currentWord.matches("\\d+"))
		{
			int start = -1;
			int j = 0;
			char[] rfcChars = new char[]{'c','f','r'};
			for (int i = wordStart -1 ;i>= 0;i--)
			{
				char ch = lineText.charAt(i);
				if (Character.isWhitespace(ch))
					continue;
				
				if (Character.isLetter(ch))
				{
					if (rfcChars[j] == Character.toLowerCase(ch))
					{
						start = i;
						j++;
						if (j == 3)
							break;
						continue;
					}
					break;
				}
				break;
			}
			if (start == -1 || j != 3)
				return null;
			wordStart = start;
			rfcNum = Integer.parseInt(currentWord);
		}
		else if (currentWord.matches("rfc\\d+"))
		{
			rfcNum = Integer.parseInt(currentWord.substring(3, currentWord.length()));
		}
		else
		{
			return null;
		}
		
		currentLink = new RFCHyperlink(lineStart + wordStart, lineStart + wordEnd, line,"rfc"+ rfcNum, rfcNum);
		return currentLink;
	} //}}}

}
