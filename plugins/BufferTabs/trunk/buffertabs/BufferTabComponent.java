/**
 * 
 */
package buffertabs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
class BufferTabComponent extends JPanel
{
	static private CloseIcon icon = new CloseIcon();
	static private Dimension iconDimension =
		new Dimension(icon.getIconWidth(), icon.getIconHeight());
	private BufferTabs pane;
	
	public BufferTabComponent(BufferTabs bufferTabs) {
		super(new FlowLayout(FlowLayout.CENTER, 5, 0));
		pane = bufferTabs;
		setOpaque(false);
		JLabel l = new JLabel() {
			@Override
			public Icon getIcon() {
				int index = pane.indexOfTabComponent(BufferTabComponent.this);
				if (index < 0)
					return null;
				return pane.getIconAt(index);
			}
			@Override
			public String getText() {
				int index = pane.indexOfTabComponent(BufferTabComponent.this);
				if (index < 0)
					return null;
				return pane.getTitleAt(index);
			}
		};
		add(l);
		JLabel close = new JLabel(icon);
		close.setPreferredSize(iconDimension);
		add(close);
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = pane.indexOfTabComponent(BufferTabComponent.this);
				if (index < 0)
					return;
				jEdit.closeBuffer(pane.getEditPane().getView(),
					pane.bufferSet.getBuffer(index));
			}
		});
	}
	
	static private class CloseIcon implements Icon {
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, getIconWidth() - 1, getIconHeight() - 1);
			g.drawLine(getIconWidth() - 1, 0, 0, getIconHeight() - 1);
		}
		public int getIconWidth() {
			return 6;
		}
		public int getIconHeight() {
			return 6;
		}
	}

}