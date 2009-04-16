/*
 * JMouseComboBox.java - bugfix class for JComboBox
 * Copyright (C) 2001 Dirk Moebius
 *
 * jEdit buffer options:
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package common.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.gjt.sp.util.Log;


/**
 * This is a combo-box that allows listeners to be informed of mouse
 * entered and mouse exited events.
 *
 * Note that other mouse events besides MOUSE_ENTERED and MOUSE_EXITED still
 * do <i>not</i> get delivered. This is because sending a MOUSE_PRESSED,
 * MOUSE_CLICKED or MOUSE_RELEASED event would cause the combo box popup to
 * be hidden immediately after it has been shown, resulting in that it
 * would not be shown at all.
*
 * This class was created as a fix/workaround for Swing bug #4144505.
 *
 * @author Dirk Moebius (<a href="mailto:dmoebius@gmx.net">dmoebius@gmx.net</a>)
 * @version JMouseComboBox 1.0
 */
public class JMouseComboBox extends JComboBox implements MouseListener {

	/**
	 * Creates a JMouseComboBox with a default data model.
	 *
	 * The default data model is an empty list of objects.
	 * Use <code>addItem</code> to add items.
	 */
	public JMouseComboBox()
	{
		super();
		initialize();
	}


	/**
	 * Creates a JMouseComboBox that contains the elements in the specified
	 * array.
	 */
	public JMouseComboBox(Object[] items)
	{
		super(items);
		initialize();
	}


	/**
	 * Creates a JMouseComboBox that takes its items from an existing
	 * <code>ComboBoxModel</code>.
	 *
	 * @param  aModel  the ComboBoxModel that provides the displayed
	 *                 list of items
	 */
	public JMouseComboBox(ComboBoxModel aModel)
	{
		super(aModel);
		initialize();
	}


	/**
	 * Creates a JMouseComboBox that contains the elements in the specified
	 * Vector.
	 */
	public JMouseComboBox(java.util.Vector items)
	{
		super(items);
		initialize();
	}


	/**
	 * Initialize the class.
	 */
	private void initialize()
	{
		setName("JMouseComboBox");
		for (int i = 0; i < getComponentCount(); i++)
			getComponent(i).addMouseListener(this);
	}


	private void createMouseEvent(MouseEvent e)
	{
		if (e.getSource() != this) {
			int id = e.getID();
			switch(id) {
				case MouseEvent.MOUSE_PRESSED:
				case MouseEvent.MOUSE_RELEASED:
				case MouseEvent.MOUSE_CLICKED:
					// cannot deliver these events, because it would cause the
					// popup to be hidden.
					break;
				case MouseEvent.MOUSE_ENTERED:
				case MouseEvent.MOUSE_EXITED:
					// create new event from the old one, with this as source:
					MouseEvent newEvt = new MouseEvent(
						this, e.getID(), e.getWhen(), e.getModifiers(),
						e.getX(), e.getY(), e.getClickCount(),
						e.isPopupTrigger()
					);
					// send the event to the event queue:
					processMouseEvent(newEvt);
					break;
			}
		}
	}


	public void mouseClicked(MouseEvent e)
	{
		createMouseEvent(e);
	}


	public void mouseEntered(MouseEvent e)
	{
		createMouseEvent(e);
	}


	public void mouseExited(MouseEvent e)
	{
		createMouseEvent(e);
	}


	public void mousePressed(MouseEvent e)
	{
		createMouseEvent(e);
	}


	public void mouseReleased(MouseEvent e)
	{
		createMouseEvent(e);
	}

}

