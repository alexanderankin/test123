/*
 * ConsolePane.java - The console input/output pane
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2004 Slava Pestov
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

package console;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import org.gjt.sp.jedit.gui.HistoryModel;

public class ConsolePane extends JTextPane
{
	public static final String InputStart = "InputStart";

	public static final Object Input = new Object();
	public static final Object Actions = new Object();

	//{{{ ConsolePane constructor
	public ConsolePane()
	{
		MouseHandler mouse = new MouseHandler();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);

		history = new ConsoleHistoryText(this);

		listenerList = new EventListenerList();

		InputMap inputMap = getInputMap();
		
		/* Press enter to evaluate the input */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),
			new EnterAction());

		/* Press backspace to stop backspacing over the prompt */
		inputMap.put(KeyStroke.getKeyStroke('\b'),
			new BackspaceAction());

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME,0),
			new HomeAction());

		/* Press Up/Down to access history */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),
			new HistoryUpAction());

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),
			new HistoryDownAction());

		/* Workaround */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0),
			new DummyAction());

		documentHandler = new DocumentHandler();
		setDocument(getDocument());
	} //}}}

	//{{{ setDocument() method
	public void setDocument(Document doc)
	{
		if(documentHandler != null && getDocument() != null)
			getDocument().removeDocumentListener(documentHandler);

		super.setDocument(doc);
		doc.addDocumentListener(documentHandler);
	} //}}}

	//{{{ insertWithAttrs() method
	public void insertWithAttrs(String text, AttributeSet attrs)
		throws BadLocationException
	{
		if(text == null)
			throw new NullPointerException();

		StyledDocument doc = (StyledDocument)getDocument();
		int offset1 = doc.getLength();
		doc.insertString(offset1,text,null);
		int offset2 = offset1 + text.length();
		doc.setCharacterAttributes(offset1,offset2,attrs,true);
		setCaretPosition(offset2);

		setInputStart(offset2);
	} //}}}

	//{{{ setHistoryModel() method
	public void setHistoryModel(String name)
	{
		history.setModel(name);
	} //}}}

	//{{{ getHistoryModel() method
	public HistoryModel getHistoryModel()
	{
		return history.getModel();
	} //}}}

	//{{{ addActionListener() method
	public void addActionListener(ActionListener l)
	{
		listenerList.add(ActionListener.class,l);
	} //}}}

	//{{{ removeActionListener() method
	public void removeActionListener(ActionListener l)
	{
		listenerList.remove(ActionListener.class,l);
	} //}}}

	//{{{ eval() method
	public void eval(String eval)
	{
		if(eval == null)
			return;

		try
		{
			StyledDocument doc = (StyledDocument)getDocument();
			setCaretPosition(doc.getLength());
			doc.insertString(doc.getLength(),eval + "\n",
				getCharacterAttributes());
		}
		catch(BadLocationException ble)
		{
			ble.printStackTrace();
		}

		fireActionEvent(eval);
	} //}}}

	//{{{ fireActionEvent() method
	public void fireActionEvent(String code)
	{
		ActionEvent evt = new ActionEvent(this,
			ActionEvent.ACTION_PERFORMED,code);

		Object[] listeners = listenerList.getListenerList();
		for(int i = 0; i < listeners.length; i++)
		{
			if(listeners[i] == ActionListener.class)
			{
				ActionListener l = (ActionListener)
					listeners[i+1];
				l.actionPerformed(evt);
			}
		}
	} //}}}

	//{{{ getAttributes() method
	private AttributeSet getAttributes(int pos)
	{
		StyledDocument doc = (StyledDocument)getDocument();
		Element e = doc.getCharacterElement(pos);
		return e.getAttributes();
	} //}}}

	//{{{ getActions() method
	/* private Cons getActions(int pos)
	{
		AttributeSet a = getAttributes(pos);
		if(a == null)
			return null;
		else
			return (Cons)a.getAttribute(Actions);
	} */ //}}}

	//{{{ getActionsPopup() method
	private JPopupMenu getActionsPopup(int pos)
	{
		/* List actions = getActions(pos);
		if(actions == null) */
			return null;

		/* JPopupMenu popup = new JPopupMenu();
		while(actions != null)
		{
			Cons action = (Cons)actions.car;
			JMenuItem item = new JMenuItem((String)action.cdr);
			item.setActionCommand((String)action.car);
			item.addActionListener(new EvalAction());
			popup.add(item);
			actions = actions.next();
		}

		return popup; */
	} //}}}

	//{{{ showPopupMenu() method
	private void showPopupMenu(int pos)
	{
		JPopupMenu actions = getActionsPopup(pos);
		if(actions == null)
			return;

		try
		{
			StyledDocument doc = (StyledDocument)getDocument();
			Element e = doc.getCharacterElement(pos);
			Point pt = modelToView(e.getStartOffset())
				.getLocation();
			FontMetrics fm = getFontMetrics(getFont());

			actions.show(this,pt.x,pt.y + fm.getHeight());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	} //}}}

	//{{{ getInput() method
	public String getInput()
	{
		try
		{
			Document doc = getDocument();
			int cmdStart = getInputStart();
			String line = doc.getText(cmdStart,doc.getLength() - cmdStart);
			if(line.endsWith("\n"))
				return line.substring(0,line.length() - 1);
			else
				return line;
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	} //}}}

	//{{{ setInput() method
	public void setInput(String line)
	{
		try
		{
			Document doc = getDocument();
			int cmdStart = getInputStart();
			doc.remove(cmdStart,doc.getLength() - cmdStart);
			doc.insertString(cmdStart,line,null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	} //}}}

	//{{{ getInputStart() method
	public int getInputStart()
	{
		return ((Integer)getDocument().getProperty(InputStart))
			.intValue();
	} //}}}

	//{{{ setInputStart() method
	public void setInputStart(int cmdStart)
	{
		getDocument().putProperty(InputStart,new Integer(cmdStart));
	} //}}}

	//{{{ Private members
	private static final Cursor MoveCursor
		= Cursor.getPredefinedCursor
		(Cursor.HAND_CURSOR);
	private static final Cursor DefaultCursor
		= Cursor.getPredefinedCursor
		(Cursor.TEXT_CURSOR);

	private EventListenerList listenerList;

	private ConsoleHistoryText history;

	private DocumentHandler documentHandler;
	//}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseInputAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			Point pt = new Point(e.getX(), e.getY());
			int pos = viewToModel(pt);
			if(pos >= 0)
				showPopupMenu(pos);
		}

		public void mouseMoved(MouseEvent e)
		{
			Point pt = new Point(e.getX(), e.getY());
			int pos = viewToModel(pt);
			if(pos >= 0)
			{
				Cursor cursor;
				/* if(getActions(pos) != null)
					cursor = MoveCursor;
				else */
					cursor = DefaultCursor;

				if(getCursor() != cursor)
					setCursor(cursor);
			}
		}
	} //}}}

	//{{{ EvalAction class
	class EvalAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			eval(evt.getActionCommand());
		}
	} //}}}

	//{{{ EnterAction class
	class EnterAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			/* setCaretPosition(getDocument().getLength());
			replaceSelection("\n");

			history.addCurrentToHistory();
			history.setIndex(-1); */

			fireActionEvent(getInput());
		}
	} //}}}

	//{{{ BackspaceAction class
	class BackspaceAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(getSelectionStart() != getSelectionEnd())
			{
				replaceSelection("");
				return;
			}

			int caret = getCaretPosition();
			if(caret == getInputStart())
			{
				getToolkit().beep();
				return;
			}

			try
			{
				getDocument().remove(caret - 1,1);
			}
			catch(BadLocationException e)
			{
				e.printStackTrace();
			}
		}
	} //}}}

	//{{{ HomeAction class
	class HomeAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			setCaretPosition(getInputStart());
		}
	} //}}}

	//{{{ HistoryUpAction class
	class HistoryUpAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			history.historyPrevious();
		}
	} //}}}

	//{{{ HistoryDownAction class
	class HistoryDownAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			history.historyNext();
		}
	} //}}}

	//{{{ DummyAction class
	class DummyAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
		}
	} //}}}

	//{{{ DocumentHandler class
	class DocumentHandler implements DocumentListener
	{
		public void insertUpdate(DocumentEvent e)
		{
			int offset = e.getOffset();
			int length = e.getLength();

			int cmdStart = getInputStart();
			if(offset < cmdStart)
				cmdStart += length;
			setInputStart(cmdStart);
		}

		public void removeUpdate(DocumentEvent e)
		{
			int offset = e.getOffset();
			int length = e.getLength();

			int cmdStart = getInputStart();
			if(offset < cmdStart)
			{
				if(offset + length > cmdStart)
					cmdStart = offset;
				else
					cmdStart -= length;
			}
			setInputStart(cmdStart);
		}

		public void changedUpdate(DocumentEvent e) {}
	} //}}}
}
