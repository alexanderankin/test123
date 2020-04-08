/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2012 Matthieu Casanova
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
package gatchan.jedit.hyperlinks.include_c;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.jedit.hyperlinks.HyperlinkSource;
import gatchan.jedit.hyperlinks.jEditOpenFileHyperlink;
import org.gjt.sp.jedit.Buffer;

import java.util.regex.Pattern;

/**
 * This hyperlink source will search for #include "something".
 *
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class IncludeHyperlinkSource implements HyperlinkSource
{
	private static final Pattern PATTERN = Pattern.compile("#\\s*include.*");
	private Hyperlink currentLink;

	@Override
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
		{
			currentLink = null;
			return null;
		}
		int offset = caretPosition - lineStart;
		String lineText = buffer.getLineText(line);
		if (offset == lineLength) offset--;

		if (!PATTERN.matcher(lineText).matches())
			return currentLink = null;

		int offsetStart = lineText.indexOf('"', "#include ".length());
		int offsetEnd = lineText.indexOf('"', offsetStart + 1);
		if (offsetStart == -1 || offsetEnd == -1 || offset <= offsetStart || offset >= offsetEnd)
		{
			return currentLink = null;
		}
		String included = lineText.substring(offsetStart + 1, offsetEnd);
		currentLink = new jEditOpenFileHyperlink(lineStart + offsetStart + 1, lineStart + offsetEnd, line, included);
		return currentLink;
	}
}
