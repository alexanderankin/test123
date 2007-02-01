/*
 * TextFieldWithClear.java - Text field with a 'clear' button
 *
 * Copyright (C) 2007 Carmine Lucarelli
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

package shortcutSaver;

//{{{ Imports
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import org.gjt.sp.jedit.*;
//}}}

/**
 * Text field with an 'x' key for clearing its contents
 */
public class TextFieldWithClear extends JTextField
{
	public TextFieldWithClear()
	{
		this("");
	}

	public TextFieldWithClear(String text)
	{
		MouseHandler mouseHandler = new MouseHandler();
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		setText(text);
		Border textFieldBorder = UIManager.getBorder("TextField.border");

		if(textFieldBorder != null)
		{
			setBorder(new CompoundBorder(textFieldBorder,
				new ClearBorder()));
		}
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	//{{{ fireActionPerformed() method
	/**
	 * Make it public.
	 */
	public void fireActionPerformed()
	{
		super.fireActionPerformed();
	} //}}}

	//{{{ Protected members

	//{{{ processMouseEvent() method
	protected void processMouseEvent(MouseEvent evt)
	{
		if(!isEnabled())
			return;

		switch(evt.getID())
		{
		case MouseEvent.MOUSE_PRESSED:
			Border border = getBorder();
			Insets insets = border.getBorderInsets(TextFieldWithClear.this);

			if(evt.getX() >= getWidth() - insets.right
				|| GUIUtilities.isPopupTrigger(evt))
			{
				setText("");
			}
			else
				super.processMouseEvent(evt);

			break;
		case MouseEvent.MOUSE_EXITED:
			setCursor(Cursor.getDefaultCursor());
			super.processMouseEvent(evt);
			break;
		default:
			super.processMouseEvent(evt);
			break;
		}
	} //}}}

	//}}}

	public String getToolTipText(MouseEvent evt)
	{
		Border border = getBorder();
		Insets insets = border.getBorderInsets(TextFieldWithClear.this);

		if(evt.getX() >= getWidth() - insets.right)
			return "Click to clear";
		else
			return null;
	}
	
	//{{{ Inner classes

	//{{{ MouseHandler class
	class MouseHandler extends MouseInputAdapter
	{
		//{{{ mouseMoved() method
		public void mouseMoved(MouseEvent evt)
		{
			Border border = getBorder();
			Insets insets = border.getBorderInsets(TextFieldWithClear.this);

			if(evt.getX() >= getWidth() - insets.right)
				setCursor(Cursor.getDefaultCursor());
			else
				setCursor(Cursor.getPredefinedCursor(
					Cursor.TEXT_CURSOR));
		} //}}}
	} //}}}

	//{{{ ClearBorder class
	static class ClearBorder extends AbstractBorder
	{
		static final int WIDTH = 16;

		public void paintBorder(Component c, Graphics g,
			int x, int y, int w, int h)
		{
			g.translate(x+w-WIDTH,y-1);

			//if(c.isEnabled())
			//{
			//	// vertical separation line
			//	g.setColor(UIManager.getColor("controlDkShadow"));
			//	g.drawLine(0,0,0,h);
			//}

			// down arrow
			int w2 = WIDTH/2;
			int h2 = h/2;
			g.setColor(UIManager.getColor("TextField.foreground"));
			g.drawLine(w2-5,h2-3,w2+4,h2+3);
			g.drawLine(w2-5,h2+3,w2+4,h2-3);
			//g.drawLine(w2-5,h2-2,w2+4,h2-2);
			//g.drawLine(w2-4,h2-1,w2+3,h2-1);
			//g.drawLine(w2-3,h2  ,w2+2,h2  );
			//g.drawLine(w2-2,h2+1,w2+1,h2+1);
			//g.drawLine(w2-1,h2+2,w2  ,h2+2);

			g.translate(-(x+w-WIDTH),-(y-1));
		}

		public Insets getBorderInsets(Component c)
		{
			return new Insets(0,0,0,WIDTH);
		}
	} //}}}

	//}}}
}
