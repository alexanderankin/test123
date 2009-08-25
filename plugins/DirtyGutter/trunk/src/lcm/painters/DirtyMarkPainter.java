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
