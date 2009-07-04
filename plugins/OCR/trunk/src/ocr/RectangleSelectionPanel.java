package ocr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class RectangleSelectionPanel extends JPanel
{
	interface SelectionListener
	{
		void rectSelected(Point [] p);
	}
	private BufferedImage image;
	private Dimension dimension;
	private Point [] points = new Point[2];
	private Point drag = null;
	private SelectionListener listener;

	public RectangleSelectionPanel(BufferedImage bi, Dimension d,
		SelectionListener l)
	{
		image = bi;
		dimension = d;
		listener = l;
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				points[1] = e.getPoint();
				listener.rectSelected(points);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				points[0] = e.getPoint();
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				drag(e.getPoint());
			}
		});
	}
	public void drag(Point newDrag)
	{
		if (newDrag != null)
		{
			Graphics g = getGraphics();
			g.setXORMode(getBackground());
			g.setColor(Color.BLUE);
			Point start = points[0];
			if (drag != null)
				g.drawRect(start.x, start.y, drag.x - start.x, drag.y - start.y);
			drag = newDrag;
			g.drawRect(start.x, start.y, drag.x - start.x, drag.y - start.y);
		}
	}
	public void paintComponent(Graphics g)
	{
		g.drawImage(image, 0, 0, dimension.width, dimension.height, null);
	}

}
