/*
 * SideKickCompletionPopup.java - Completer popup
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package sidekick;

//{{{ Imports
import javax.swing.event.ListDataListener;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Point;
import java.util.*;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;
//}}}

public class SideKickCompletionPopup extends JWindow
{
	//{{{ SideKickCompletionPopup constructor
	public SideKickCompletionPopup(View view, SideKickParser parser, Point location)
	{
		super(view);

		this.view = view;
		this.parser = parser;
		this.textArea = view.getTextArea();

		list = new JList();

		//setListModel(completions);

		list.addMouseListener(new MouseHandler());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
	} //}}}

	//{{{ dispose() method
	public void dispose()
	{
		view.setKeyEventInterceptor(null);
		super.dispose();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				view.getTextArea().requestFocus();
			}
		});
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private int mode;
	private View view;
	private JEditTextArea textArea;
	private JList list;
	private SideKickParser parser;
	//}}}

	//{{{ updateListModel() method
	private void updateListModel()
	{
		
		///list.setCellRenderer(completion.getRenderer());
	} //}}}

	//{{{ setListModel() method
	private void setListModel(final List items)
	{
		ListModel model = new ListModel()
		{
			public int getSize()
			{
				return items.size();
			}

			public Object getElementAt(int index)
			{
				return items.get(index);
			}

			public void addListDataListener(ListDataListener l) {}
			public void removeListDataListener(ListDataListener l) {}
		};

		list.setModel(model);
		list.setSelectedIndex(0);
	} //}}}

	//{{{ insertSelected() method
	private void insertSelected(char ch)
	{
		// XXX
	} //}}}

	//}}}

	//{{{ KeyHandler class
	class KeyHandler extends KeyAdapter
	{
		//{{{ keyPressed() method
		public void keyPressed(KeyEvent evt)
		{
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;

			// XXX
			/* switch(evt.getKeyCode())
			{
			case KeyEvent.VK_ENTER:
				insertSelected('\n');
				evt.consume();
				break;
			case KeyEvent.VK_TAB:
				insertSelected('\t');
				evt.consume();
				break;
			case KeyEvent.VK_ESCAPE:
				dispose();
				evt.consume();
				break;
			case KeyEvent.VK_UP:
				int selected = list.getSelectedIndex();

				if(selected == 0)
					selected = list.getModel().getSize() - 1;
				else if(getFocusOwner() == list)
					return;
				else
					selected = selected - 1;

				list.setSelectedIndex(selected);
				list.ensureIndexIsVisible(selected);

				evt.consume();
				break;
			case KeyEvent.VK_DOWN:
				/ int / selected = list.getSelectedIndex();

				if(selected == list.getModel().getSize() - 1)
					selected = 0;
				else if(getFocusOwner() == list)
					return;
				else
					selected = selected + 1;

				list.setSelectedIndex(selected);
				list.ensureIndexIsVisible(selected);

				evt.consume();
				break;
			case KeyEvent.VK_SPACE:
				break;
			case KeyEvent.VK_BACK_SPACE:
				if(text.length() == 0)
				{
					textArea.backspace();
					dispose();
				}
				else
				{
					text = text.substring(0,text.length() - 1);
					textArea.backspace();
					setUpListModel();
				}
				evt.consume();
				break;
			default:
				if(evt.isActionKey())
				{
					dispose();
					view.processKeyEvent(evt);
				}
				break;
			} */
		} //}}}

		//{{{ keyTyped() method
		public void keyTyped(KeyEvent evt)
		{
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;

			// XXX
			/* char ch = evt.getKeyChar();
			if(ch == '\b')
				return;
                        
			if(ch == ';' || ch == '>'
				|| ch == ' ' || ch == '\t')
			{
				insertSelected(ch);
			}
			else if(ch == '/')
			{
				// in an XML file, a closing tag
				// must always close the most
				// recently opened tag.
				XmlActions.completeClosingTag(view);
				dispose();
			}
			else
			{
				textArea.userInput(ch);
				text = text + ch;
				setUpListModel();
			}
                        
			evt.consume(); */
		} //}}}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			insertSelected('\0');
		}
	} //}}}
}
