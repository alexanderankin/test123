/*
 * ColoredRectWithStripsPainter - Dirty mark painter that uses a colored rect with
 * optional strips on top and below (to mark removed lines for example).
 *
 * Copyright (C) 2009 Shlomy Reinstein
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

package lcm.painters;

import java.awt.Color;
import java.awt.Graphics2D;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.Gutter;

public class ColoredRectWithStripsPainter extends ColoredRectDirtyMarkPainter
{
	static final int STRIP_HEIGHT = 2;
	private int stripHeight = STRIP_HEIGHT;
	private Color stripColor = Color.RED;
	private boolean topStrip = false, middleRect = false, bottomStrip = false;
	public void setStripColor(Color c)
	{
		stripColor = c;
	}
	public void setParts(boolean top, boolean middle, boolean bottom)
	{
		topStrip = top;
		middleRect = middle;
		bottomStrip = bottom;
	}
	public void setStripHeight(int height)
	{
		stripHeight = height;
	}
	public void paint(Graphics2D gfx, Gutter gutter, int y, int height,
		Buffer buffer, int physicalLine)
	{
		if (middleRect)
			super.paint(gfx, gutter, y, height, buffer, physicalLine);
		Color c = gfx.getColor();
		gfx.setColor(stripColor);
		if (topStrip)
			gfx.fillRect(gutter.getWidth() - width, y, width, stripHeight);
		if (bottomStrip)
			gfx.fillRect(gutter.getWidth() - width, y + height - stripHeight, width,
				stripHeight);
		gfx.setColor(c);
	}

}
