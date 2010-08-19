/*
 * RFCHyperlinkSource.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007-2010 Matthieu Casanova
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

import java.util.regex.Pattern;

/**
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class RFCHyperlinkSource implements HyperlinkSource
{
	private static final String NO_WORD_SEP = "";

	private Hyperlink currentLink;
	private final char[] rfcChars;
	private static final Pattern PATTERN = Pattern.compile("rfc\\d+");

	public RFCHyperlinkSource()
	{
		rfcChars = new char[]{'c','f','r'};
	}

	//{{{ getHyperlink() method
	public Hyperlink getHyperlink(Buffer buffer, int caretPosition)
	{
		if (currentLink != null)
		{
			if (currentLink.getStartOffset() <= caretPosition &&
				currentLink.getEndOffset() >= caretPosition)
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
		if ("rfc".equals(currentWord))
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
		else if (isDigitWord(currentWord))
		{
			int start = -1;
			int j = 0;
			for (int i = wordStart -1 ;i>= 0;i--)
			{
				char ch = lineText.charAt(i);
				if (Character.isWhitespace(ch) || ch == '-' || ch == '_')
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
		else if (PATTERN.matcher(currentWord).matches())
		{
			rfcNum = Integer.parseInt(currentWord.substring(3, currentWord.length()));
		}
		else if (isIndexLine(lineText))
		{
			int pos = lineText.indexOf("....");
			if (pos < wordStart)
				return null;
			int i;
			for (i = 0;i<lineText.length();i++)
			{
				if (!Character.isWhitespace(lineText.charAt(i)))
					break;
			}
			pos -= i;
			String txt = lineText.substring(i);
			int spacePos = txt.indexOf(' ');
			String tooltip = txt.substring(spacePos + 1, pos - 1);
			String num = txt.substring(0, spacePos);
			if (num.endsWith("."))
			{
				num = num.substring(0, num.length() - 2);
			}
			String pattern = num + "\\.?\\s+" + tooltip;
			currentLink = new ChapterHyperlink(lineStart + i, i+lineStart + pos - 1, line,
				tooltip, pattern, buffer.getPath());
			return currentLink;
		}
		else
		{
			return null;
		}
		
		currentLink = new RFCHyperlink(lineStart + wordStart, lineStart + wordEnd, line,"rfc"+ rfcNum, rfcNum);
		return currentLink;
	} //}}}

	private static boolean isIndexLine(String seg)
	{
		seg = seg.trim();
		if (seg.length() < 3)
			return false;

		boolean digit = Character.isDigit(seg.charAt(0));
		if (!digit)
			return false;

		if (seg.length() < 50)
			return true;
		if (seg.contains("...."))
			return true;
		return false;
	}

	private static boolean isDigitWord(CharSequence seq)
	{
		for (int i = 0;i<seq.length();i++)
		{
			if (!Character.isDigit(seq.charAt(i)))
				return false;
		}
		return true;
	}

}
