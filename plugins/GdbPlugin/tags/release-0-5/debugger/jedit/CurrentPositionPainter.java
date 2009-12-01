/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package debugger.jedit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;

public class CurrentPositionPainter extends DebuggerPainter {

	CurrentPositionPainter(EditPane e, Buffer b, int l) {
		super(e, b, l);
	}
	
	@Override
	protected void paintLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y, Point p) {
		gfx.setColor(Color.MAGENTA);
		gfx.drawLine(p.x, p.y + 5, p.x + 10, p.y + 5);
		gfx.drawLine(p.x, p.y + 6, p.x + 10, p.y + 6);
		gfx.drawLine(p.x + 7, p.y + 2, p.x + 10, p.y + 5);
		gfx.drawLine(p.x + 6, p.y + 2, p.x + 9, p.y + 5);
		gfx.drawLine(p.x + 7, p.y + 9, p.x + 10, p.y + 6);
		gfx.drawLine(p.x + 6, p.y + 9, p.x + 9, p.y + 6);
	}

}
