/*
 * DirtyMarkPainter - Interface for painters of dirty state marks in gutter.
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

import java.awt.Graphics2D;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.Gutter;

public interface DirtyMarkPainter
{
	/*
	 * Paints the dirty mark.
	 * - gfx: The graphics object used for painting the gutter.
	 * - gutter: The gutter.
	 * - y: The y coordinate where to start drawing in the gutter.
	 * - height: The height of a text line.
	 */
	void paint(Graphics2D gfx, Gutter gutter, int y, int height, Buffer buffer,
		int physicalLine);
}
