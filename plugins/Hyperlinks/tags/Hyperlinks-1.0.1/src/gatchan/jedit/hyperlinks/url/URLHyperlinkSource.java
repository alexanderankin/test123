/*
 * URLHyperlinkSource.java - The URL hyperlink source
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.hyperlinks.url;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.jedit.hyperlinks.HyperlinkSource;
import gatchan.jedit.hyperlinks.jEditOpenFileHyperlink;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This hyperlink source will search for url in the buffer.
 *
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class URLHyperlinkSource implements HyperlinkSource
{
	private static final String NO_WORD_SEP = "/.-_&;:%#?=";

	private Hyperlink currentLink;

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


		String url = lineText.substring(wordStart, wordEnd);
		try
		{
			new URL(url);
			currentLink = new jEditOpenFileHyperlink(lineStart + wordStart, lineStart + wordEnd,line, url);
		}
		catch (MalformedURLException e)
		{
			currentLink = null;
		}
		return currentLink;
	}
}
