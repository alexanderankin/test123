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
	public SideKickCompletionPopup(View view, SideKickParser parser, int caret)
	{
		super(view);

		this.view = view;
		this.parser = parser;
		this.textArea = view.getTextArea();

		list = new JList();

		if(updateListModel())
		{
			Point location = textArea.offsetToXY(caret - complete.getTokenLength());
			location.y += textArea.getPainter().getFontMetrics().getHeight();

			SwingUtilities.convertPointToScreen(location,
				textArea.getPainter());

			list.addMouseListener(new MouseHandler());
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// stupid scrollbar policy is an attempt to work around
			// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
			JScrollPane scroller = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			getContentPane().add(scroller, BorderLayout.CENTER);

			KeyHandler keyHandler = new KeyHandler();
			addKeyListener(keyHandler);
			getRootPane().addKeyListener(keyHandler);
			list.addKeyListener(keyHandler);
			view.setKeyEventInterceptor(keyHandler);

			GUIUtilities.requestFocus(this,list);

			pack();
			setLocation(location);
			show();
		}
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
	private SideKickCompletion complete;
	//}}}

	//{{{ updateListModel() method
	private boolean updateListModel()
	{
		EditPane editPane = view.getEditPane();
		int caret = editPane.getTextArea().getCaretPosition();
		complete = parser.complete(editPane,caret);

		if(complete == null)
			return false;
		else if(complete.size() == 0)
		{
			list.setListData(new String[] {
				jEdit.getProperty("sidekick-complete.none")
			});
			list.setCellRenderer(new DefaultListCellRenderer());
			return true;
		}
		else
		{
			setListModel(complete);
			list.setCellRenderer(complete.getRenderer());
			return true;
		}
	} //}}}

	//{{{ setListModel() method
	private void setListModel(final SideKickCompletion items)
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

			switch(evt.getKeyCode())
			{
			case KeyEvent.VK_ENTER:
				keyTyped('\n');
				evt.consume();
				break;
			case KeyEvent.VK_TAB:
				keyTyped('\t');
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
				/* int */ selected = list.getSelectedIndex();

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
			case KeyEvent.VK_DELETE:
				dispose();
				view.processKeyEvent(evt);
				break;
			default:
				// from DefaultInputHandler
				if(!(evt.isControlDown() || evt.isAltDown() || evt.isMetaDown()))
				{
					if(!evt.isActionKey())
					{
						break;
					}
				}

				dispose();
				view.processKeyEvent(evt);
				break;
			}
		} //}}}

		//{{{ keyTyped() method
		public void keyTyped(KeyEvent evt)
		{
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;

			char ch = evt.getKeyChar();
			if(ch == '\b')
				return;

			keyTyped(ch);

			evt.consume();
		} //}}}

		//{{{ keyTyped() method
		private void keyTyped(char ch)
		{
			if(complete != null && complete.handleKeystroke(list.getSelectedIndex(),ch))
				updateListModel();
			else
				dispose();
		} //}}}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			// XXX
			if(complete != null)
				complete.handleKeystroke(list.getSelectedIndex(),'\n');
			dispose();
		}
	} //}}}
}
