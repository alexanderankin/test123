/*
 * SideKickCompletionPopup.java - Completer popup
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright 2000, 2005 Slava Pestov
 *		   2005 Robert McKinnon
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
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;
//}}}

public class SideKickCompletionPopup extends JWindow
{
	//{{{ fitInScreen() method
	public static Point fitInScreen(Point p, Window w, int lineHeight)
	{
		Rectangle screenSize = w.getGraphicsConfiguration().getBounds();
		if(p.y + w.getHeight() >= screenSize.height)
			p.y = p.y - w.getHeight() - lineHeight;
		return p;
	} //}}}
	
	//{{{ SideKickCompletionPopup constructor
	public SideKickCompletionPopup(View view, SideKickParser parser,
		int caret, SideKickCompletion complete)
	{
		super(view);

		this.view = view;
		this.parser = parser;
		this.textArea = view.getTextArea();

		list = new JList();

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
		list.addListSelectionListener(new ListHandler());
		view.setKeyEventInterceptor(keyHandler);

		GUIUtilities.requestFocus(this,list);

		this.complete = complete;
		updateListModel();

		Point location = textArea.offsetToXY(caret - complete.getTokenLength());
		location.y += textArea.getPainter().getFontMetrics().getHeight();

		SwingUtilities.convertPointToScreen(location,
			textArea.getPainter());

		setLocation(fitInScreen(location,this,
			textArea.getPainter().getFontMetrics()
			.getHeight()));

		handleFocusOnDispose = true;
		textAreaFocusListener = new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				handleFocusOnDispose = false;
				dispose();
			}
		};

		setVisible(true);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				textArea.requestFocus(); // fix for focus problems under windows - Rob
                textArea.addFocusListener(textAreaFocusListener);
			}
		});
	} //}}}

	//{{{ dispose() method
	public void dispose()
	{
		view.setKeyEventInterceptor(null);
		textArea.removeFocusListener(textAreaFocusListener);
		super.dispose();
		if (handleFocusOnDispose) {
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					view.getTextArea().requestFocus();
				}
			});
		}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private JEditTextArea textArea;
	private JList list;
	private SideKickParser parser;
	private SideKickCompletion complete;
	private FocusListener textAreaFocusListener;
	private boolean handleFocusOnDispose;
	//}}}

	//{{{ updateListModel() method
	private void updateListModel()
	{
		if(complete == null || complete.size() == 0)
		{
			list.setListData(new String[] {
				jEdit.getProperty("sidekick-complete.none")
			});
			list.setCellRenderer(new DefaultListCellRenderer());
			list.setVisibleRowCount(1);
		}
		else
		{
			setListModel(complete);
			list.setCellRenderer(complete.getRenderer());
			
			list.setVisibleRowCount(Math.min(8,complete.size()));
			list.setFixedCellHeight(list.getCellBounds(0,0).height);
		}

		pack();
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

	//{{{ updateSelection() method
	void updateSelection()
	{
		int index = list.getSelectedIndex();
		if(index == -1)
			return;

		String description = complete.getCompletionDescription(index);
		view.getStatus().setMessageAndClear(description);
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

			int selected = list.getSelectedIndex();
			int numRows = list.getVisibleRowCount()-1;
			int newSelect = -1;
			switch(evt.getKeyCode())
			{
			case KeyEvent.VK_PAGE_UP:
				newSelect = selected - numRows;
				if (newSelect < 0) newSelect = 0;
				list.setSelectedIndex(newSelect);
				list.ensureIndexIsVisible(newSelect);
				evt.consume();
				break;
			case KeyEvent.VK_PAGE_DOWN:
				newSelect = selected + numRows;
				if (newSelect >= list.getModel().getSize()) newSelect = list.getModel().getSize() - 1; 
				list.setSelectedIndex(newSelect);
				list.ensureIndexIsVisible(newSelect);
				evt.consume();
				break;
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
				evt.consume();
				if(selected == 0)
					break;
				else if(getFocusOwner() == list)
					break;
				else
					selected = selected - 1;

				list.setSelectedIndex(selected);
				list.ensureIndexIsVisible(selected);

				break;
			case KeyEvent.VK_DOWN:
				evt.consume();
				if(selected >= list.getModel().getSize())
					break;
				if(getFocusOwner() == list)
					break;
				selected = selected + 1;
				list.setSelectedIndex(selected);
				list.ensureIndexIsVisible(selected);
				break;
			case KeyEvent.VK_SPACE:
				break;
			case KeyEvent.VK_BACK_SPACE:
				 if(parser.canHandleBackspace())
				 {
					 keyPressedDefault(evt);
				 }
				 else {
					 dispose();
					 view.processKeyEvent(evt,true);
				 }
				 break;
			case KeyEvent.VK_DELETE:
				dispose();
				view.processKeyEvent(evt,true);
				break;
			default:
				keyPressedDefault(evt);
				break;
			}
		} //}}}

		private void keyPressedDefault(KeyEvent evt) {
			// from DefaultInputHandler
			if(!(evt.isControlDown() || evt.isAltDown() || evt.isMetaDown()))
			{
				if(!evt.isActionKey())
				{
					return;
				}
			}

			dispose();
			view.processKeyEvent(evt,true);
			return;
		}

		//{{{ keyTyped() method
		public void keyTyped(KeyEvent evt)
		{
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;

			char ch = evt.getKeyChar();
			if(ch == '\b' && !parser.canHandleBackspace())
				return;

			keyTyped(ch);

			evt.consume();
		} //}}}

		//{{{ keyTyped() method
		private void keyTyped(char ch)
		{
			if(complete == null)
			{
				view.getTextArea().userInput(ch);
				dispose();
			}
			else if(complete.handleKeystroke(list.getSelectedIndex(), ch))
			{
				EditPane editPane = view.getEditPane();
				int caret = editPane.getTextArea().getCaretPosition();
				if(!complete.updateInPlace(editPane, caret))
				{
					complete = parser.complete(editPane, caret);
				}
				updateListModel();
			}
			else {
				dispose();
			}
		} //}}}
	} //}}}

	//{{{ ListHandler class
	class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateSelection();
		}
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
