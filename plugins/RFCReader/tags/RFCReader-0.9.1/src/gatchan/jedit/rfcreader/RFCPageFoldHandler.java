/*
* RFCPageFoldHandler.java
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

import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import javax.swing.text.Segment;

/**
* @author Matthieu Casanova
* @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
*/
public class RFCPageFoldHandler extends FoldHandler
{
	//{{{ RFCPageFoldHandler constructor
	public RFCPageFoldHandler()
	{
		super("rfc-page");
	} //}}}
	
	//{{{ getFoldLevel() method
	public int getFoldLevel(JEditBuffer buffer, int lineIndex, Segment seg)
	{
		if(lineIndex == 0)
			return 0;
		
		int foldLevel = buffer.getFoldLevel(lineIndex - 1);
		buffer.getLineText(lineIndex - 1,seg);
		if (segmentIsPage(seg))
		{
			return foldLevel + 1;
		}
		else if (seg.count == 0)
		{
			buffer.getLineText(lineIndex,seg);
			if (segmentIsPage(seg))
			{
				return Math.max(foldLevel - 1,0);
			}
		}
		return foldLevel;
	} //}}}
	
	//{{{ segmentIsPage() method
	private boolean segmentIsPage(Segment seg)
	{
		int offset = seg.offset;
		int count = seg.count;
		if (count < 8)
			return false;
		
		char[] chars = seg.array;
		if (chars[offset + count - 1] != ']')
			return false;
		
		int i;
		for (i = count - 2;i>0;i--)
		{
			char c = chars[offset + i];
			if (Character.isWhitespace(c))
			{
				i--;
				break;
			}
			
			if (!Character.isDigit(c))
				return false;
		}
		if (i < 5)
			return false;
		
		if (chars[offset + i] == 'e' &&
		    chars[offset + i-1] == 'g' &&
		    chars[offset + i-2] == 'a' &&
		    chars[offset + i-3] == 'P' &&
		    chars[offset + i-4] == '[')
		{
			return true;
		}
		return false;
	} //}}}
}
