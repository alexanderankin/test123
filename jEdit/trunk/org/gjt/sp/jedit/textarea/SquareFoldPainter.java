/*
 * SquareFoldPainter.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2008 Shlomy Reinstein
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

package org.gjt.sp.jedit.textarea;

import java.awt.Graphics2D;

import org.gjt.sp.jedit.buffer.JEditBuffer;

public class SquareFoldPainter implements FoldPainter {

	@Override
	public void paintFoldEnd(Gutter gutter, Graphics2D gfx, int screenLine,
			int physicalLine, int y, int lineHeight, JEditBuffer buffer)
	{
		gfx.setColor(gutter.getFoldColor());
		int _y = y + lineHeight / 2;
		gfx.drawLine(5,y,5,_y + 3);
		gfx.drawLine(5,_y + 3,9,_y + 3);
	}

	@Override
	public void paintFoldStart(Gutter gutter, Graphics2D gfx, int screenLine,
			int physicalLine, boolean nextLineVisible, int y, int lineHeight,
			JEditBuffer buffer)
	{
		int _y = y + lineHeight / 2;
		int _x = 5;
		gfx.setColor(gutter.getFoldColor());
		gfx.drawRect(_x-4,_y-4,8,8);
		gfx.drawLine(_x-2,_y,_x+2,_y);
		if (nextLineVisible)
			gfx.drawLine(_x,_y+5,_x,y+lineHeight-1);
		else
			gfx.drawLine(_x,_y-2,_x,_y+2);
	}

	@Override
	public void paintFoldMiddle(Gutter gutter, Graphics2D gfx, int screenLine,
			int physicalLine, int y, int lineHeight, JEditBuffer buffer)
	{
		gfx.setColor(gutter.getFoldColor());
		gfx.drawLine(5,y,5,y+lineHeight-1);
	}

}
