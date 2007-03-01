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
