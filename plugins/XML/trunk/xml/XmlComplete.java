/*
 * XmlComplete.java - Complete tag popup
 * Copyright (C) 2000, 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;

class XmlComplete extends JWindow
{
	XmlComplete(View view, String text, Vector completions, Point location)
	{
		super(view);

		this.view = view;
		this.text = text;
		this.completions = completions;

		list = new JList();
		list.setCellRenderer(new Renderer());

		list.setVisibleRowCount(Math.min(8,completions.size()));

		list.addMouseListener(new MouseHandler());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setUpListModel();

		// stupid scrollbar policy is an attempt to work around
		// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
		JScrollPane scroller = new JScrollPane(list,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		getContentPane().add(scroller, BorderLayout.CENTER);

		GUIUtilities.requestFocus(this,list);

		pack();
		setLocation(location);
		show();

		KeyHandler keyHandler = new KeyHandler();
		addKeyListener(keyHandler);
		getRootPane().addKeyListener(keyHandler);
		list.addKeyListener(keyHandler);
		view.setKeyEventInterceptor(keyHandler);
	}

	public void dispose()
	{
		view.setKeyEventInterceptor(null);
		super.dispose();
	}

	// private members
	private View view;
	private String text;
	private Vector completions;
	private JList list;

	private void setUpListModel()
	{
		DefaultListModel model = new DefaultListModel();
		for(int i = 0; i < completions.size(); i++)
		{
			Object obj = completions.elementAt(i);
			if(obj instanceof ElementDecl)
			{
				ElementDecl element = (ElementDecl)obj;
				if(element.name.startsWith(text)
					|| (element.html
					&& element.name.toLowerCase()
					.startsWith(text.toLowerCase())))
				{
					model.addElement(element);
				}
			}
			else if(obj instanceof EntityDecl)
			{
				EntityDecl entity = (EntityDecl)obj;
				if(entity.name.startsWith(text))
					model.addElement(entity);
			}
		}

		if(model.getSize() == 0)
		{
			dispose();
			return;
		}

		list.setModel(model);
		list.setSelectedIndex(0);
	}

	private void insertSelected(boolean showEditTagDialog)
	{
		Object obj = list.getSelectedValue();
		if(obj instanceof ElementDecl)
		{
			ElementDecl element = (ElementDecl)obj;
			JEditTextArea textArea = view.getTextArea();

			int offset = (!element.html && element.empty) ? 3 : 1;

			textArea.setSelectedText(element.name.substring(
				text.length())
				+ ((!element.html && element.empty)
				? " />" : ">"));

			// put caret inside tag so 'edit tag' can work
			textArea.setCaretPosition(textArea.getCaretPosition()
				- offset);

			if(showEditTagDialog)
				XmlPlugin.showEditTagDialog(view,view.getEditPane());
		}
		else if(obj instanceof EntityDecl)
		{
			EntityDecl entity = (EntityDecl)obj;
			JEditTextArea textArea = view.getTextArea();

			textArea.setSelectedText(entity.name.substring(
				text.length()) + ";");
		}

		dispose();
	}

	class KeyHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent evt)
		{
			switch(evt.getKeyCode())
			{
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_TAB:
				insertSelected(false);
				evt.consume();
				break;
			case KeyEvent.VK_ENTER:
				insertSelected(true);
				evt.consume();
				break;
			case KeyEvent.VK_ESCAPE:
				dispose();
				evt.consume();
				break;
			case KeyEvent.VK_UP:
				if(getFocusOwner() == list)
					return;

				int selected = list.getSelectedIndex();
				if(selected == 0)
					return;

				selected = selected - 1;
	
				list.setSelectedIndex(selected);
				list.ensureIndexIsVisible(selected);

				evt.consume();
				break;
			case KeyEvent.VK_DOWN:
				if(getFocusOwner() == list)
					return;

				selected = list.getSelectedIndex();
				if(selected == list.getModel().getSize() - 1)
					return;

				selected = selected + 1;

				list.setSelectedIndex(selected);
				list.ensureIndexIsVisible(selected);

				evt.consume();
				break;
			default:
				dispose();
				view.processKeyEvent(evt);
				break;
			}
		}

		public void keyTyped(KeyEvent evt)
		{
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;
			else
			{
				char ch = evt.getKeyChar();
				view.getTextArea().userInput(ch);
				text = text + ch;
				setUpListModel();
				evt.consume();
			}
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			insertSelected(true);
		}
	}

	class Renderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(
			JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list,value,
				index,isSelected,cellHasFocus);

			if(value instanceof ElementDecl)
				setText(((ElementDecl)value).name);
			else if(value instanceof EntityDecl)
				setText(((EntityDecl)value).name);

			return this;
		}
	}
}
