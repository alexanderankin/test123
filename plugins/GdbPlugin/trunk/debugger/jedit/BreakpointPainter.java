package debugger.jedit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;

public class BreakpointPainter extends DebuggerPainter {

	BreakpointPainter(EditPane e, Buffer b, int l) {
		super(e, b, l);
	}
	
	@Override
	protected void paintLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y, Point p) {
		gfx.setColor(Color.blue);
		gfx.fillOval(p.x, p.y, 10, 10);
	}
}
