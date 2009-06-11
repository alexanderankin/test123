/*
 * HrefLinkComponent.java
 * Copyright (c) 2002 Calvin Yu
 *
 * :mode=java:tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
package lookandfeel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

/**
 * A button component that behaves like a HTML HREF link.
 */
public class HrefLinkComponent extends JComponent
{

	private String text;
	private String href;

	private Dimension size;
	private boolean infoViewerAvailable;
	private Insets paintInsets;
	private AttributedString attrString;
	private boolean mouseOver;

	/**
	 * Create a new <code>HrefLinkComponent</code>.
	 */
	public HrefLinkComponent(String theText, String aHref)
	{
		setBorder(null);
		setOpaque(true);
		setForeground(UIManager.getColor("Label.foreground"));
		setFont(UIManager.getFont("Label.font"));
		setToolTipText(aHref);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);

		text = theText;
		href = aHref;

		mouseOver = false;
		infoViewerAvailable = jEdit.getPlugin("infoviewer.InfoViewerPlugin") != null;
		paintInsets = new Insets(0, 0, 0, 0);
	}

	/**
	 * Open the label's url.
	 */
	public void openURL()
	{
		if (infoViewerAvailable) {
			String cmd = "infoviewer.InfoViewerPlugin.openURL(view,\"" + href + "\");";
			BeanShell.eval(getView(), BeanShell.getNameSpace(), cmd);
		}
	}

	/**
	 * Returns the preferred size.
	 */
	public Dimension getPreferredSize()
	{
		if (size == null) {
			size = new Dimension();
			FontMetrics fm = getFontMetrics(getFont());
			Insets insets = getInsets();
			size.width = insets.left + insets.right + fm.stringWidth(text);
			LineMetrics lm = fm.getLineMetrics(text, getGraphics());
			size.height = insets.top + insets.bottom + (int) lm.getHeight();
		}
		return size != null ? (Dimension) size.clone() : null;
	}

	/**
	 * Returns the minimum size.
	 */
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	/**
	 * Paint this component.
	 */
	protected void paintComponent(Graphics g)
	{
		g.clearRect(0, 0, getWidth(), getHeight());
		if (text == null) {
			return;
		}
		g = getComponentGraphics(g);
		FontMetrics fm = g.getFontMetrics();
		paintInsets = getInsets(paintInsets);
		if (mouseOver) {
			if (attrString == null) {
				attrString = new AttributedString(text);
				attrString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
				attrString.addAttribute(TextAttribute.FONT, g.getFont());
			}
			g.setColor(Color.blue);
			g.drawString(attrString.getIterator(), paintInsets.left, paintInsets.top + fm.getAscent());
		} else {
			g.drawString(text, paintInsets.left, paintInsets.top + fm.getAscent());
		}
	}

	/**
	 * Process a mouse event.
	 */
	protected void processMouseEvent(MouseEvent evt)
	{
		if (infoViewerAvailable) {
			switch (evt.getID()) {
				case MouseEvent.MOUSE_CLICKED:
					openURL();
					break;
				case MouseEvent.MOUSE_ENTERED:
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					mouseOver = true;
					repaint();
					break;
				case MouseEvent.MOUSE_EXITED:
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					mouseOver = false;
					repaint();
					break;
			}
		}
		super.processMouseEvent(evt);
	}

	/**
	 * Returns a view.
	 */
	private View getView()
	{
		View view = GUIUtilities.getView(this);
		return view;
	}

}

