package lcm.painters;

import java.awt.Color;
import java.awt.Graphics2D;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.Gutter;

public class ColoredRectWithStripsPainter extends ColoredRectDirtyMarkPainter
{
	static final int STRIP_HEIGHT = 1;
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
