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

package perl.breakpoints;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;

import debugger.jedit.DebuggerPainter;

public class BreakpointPainter extends DebuggerPainter {

	private Breakpoint bp;
	
	public BreakpointPainter(EditPane e, Buffer b, Breakpoint bp) {
		super(e, b, bp.getLine());
		this.bp = bp;
	}
	
	@Override
	protected void paintLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y, Point p) {
		gfx.setColor(Color.blue);
		if (bp.isEnabled()) 
			gfx.fillOval(p.x, p.y, 10, 10);
		else
			gfx.drawOval(p.x, p.y, 10, 10);
	}
}
