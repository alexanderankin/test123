/*
 * RFCChaptersFoldHandler.java - The rfc chapters fold handler
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

import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import javax.swing.text.Segment;
import java.util.Arrays;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class RFCChaptersFoldHandler extends FoldHandler
{
	private static final char[] match = new char[4];

	//{{{ RFCChaptersFoldHandler constructor
	public RFCChaptersFoldHandler()
	{
		super("rfc-chapters");
		Arrays.fill(match, '.');
	} //}}}

	//{{{ getFoldLevel() method
	@Override
	public int getFoldLevel(JEditBuffer buffer, int lineIndex, Segment seg)
	{
		if(lineIndex == 0)
			return 0;
		
		int foldLevel = buffer.getFoldLevel(lineIndex - 1);
		buffer.getLineText(lineIndex - 1,seg);
		if (segmentIsChapter(seg))
		{
			return foldLevel + 1;
		}
		else if (seg.count == 0)
		{
			buffer.getLineText(lineIndex,seg);
			if (segmentIsChapter(seg))
			{
				return Math.max(foldLevel - 1,0);
			}
		}
		return foldLevel;
	} //}}}

	//{{{ segmentIsChapter() method
	private static boolean segmentIsChapter(Segment seg)
	{
		if (seg.count < 3)
			return false;

		boolean digit = Character.isDigit(seg.array[seg.offset]);
		if (!digit)
			return false;

		if (seg.length() < 71)
			return true;

		int pos = 0;
		for (int i = 0;i < seg.length();i++)
		{
			char c = seg.charAt(i);
			if (match[pos] == c)
			{
				pos++;
				if (pos == match.length)
					return false;
			}
			else
			{
				pos = 0;
			}
		}
		return true;
	} //}}}
}