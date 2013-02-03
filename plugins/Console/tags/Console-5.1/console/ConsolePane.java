/*
 * ConsolePane.java - The console input/output pane
 * :tabSize=4:indentSize=4:noTabs=false:
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
import java.util.regex.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.jedit.jEdit;

public class ConsolePane extends JTextPane
{
	// {{{ Members
	/**
	 *  Name of document's property which keeps InputStart position
	 *  inside of the document.
	 */
	public static final String InputStart = "InputStart";

	public static final Object Input = new Object();

	public static final Object Actions = new Object();

	private static final Cursor MoveCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	private static final Cursor DefaultCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);

	private EventListenerList listenerList;

	private ConsoleHistoryText history;

	transient private DocumentHandler documentHandler;
	
	private static Pattern eolReplacingPattern = null;
	// }}}
	
	// {{{ static initialization
	static
	{
		if ( !System.getProperty("line.separator").equals("\n") )
		{
			eolReplacingPattern = Pattern.compile( System.getProperty("line.separator") );
		}
	}
	// }}}
	
	// {{{ ConsolePane constructor
	public ConsolePane()
	{
		setInputStart(0);
		MouseHandler mouse = new MouseHandler();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);

		history = new ConsoleHistoryText(this);

		listenerList = new EventListenerList();

		ActionMap actionMap = getActionMap();
		InputMap inputMap = getInputMap();

		/* Press enter to evaluate the input */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new EnterAction());

		/* Press backspace to stop backspacing over the prompt */
		inputMap.put(KeyStroke.getKeyStroke('\b'), new BackspaceAction());

		/* Press C+u to delete what you typed */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U,
			InputEvent.CTRL_MASK),
			new DeleteInputAction());

		
		/* Press home to move to start of input area */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), new HomeAction());
		
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.SHIFT_MASK),
			new SelectHomeAction());

		/* Press Up/Down to access history */
		
		HistoryUpAction hup =  new HistoryUpAction(actionMap.get("caret-up"));
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), hup);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK), hup);
		
		HistoryDownAction down =  new HistoryDownAction(actionMap.get("caret-down"));
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), down);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK), down);
		
		/* Press S+Up/Down to select history */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK),
			new SearchUpAction(actionMap.get("selection-up")));

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK),
			new SearchDownAction(actionMap.get("selection-down")));

		/* Workaround */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), new DummyAction());

		documentHandler = new DocumentHandler();
		setDocument(getDocument());

	} // }}}

	// {{{ paste()
	/** Overridden to avoid pasting in the output of the Console itself. */
	public void paste() {
		Document d = getDocument();
		if (d != null) {
			int endpos = d.getLength();
			int startpos = getInputStart(); 
			if ( getCaretPosition() < startpos) setCaretPosition(endpos);
		}
		super.paste();
	} // }}}
	
	// {{{ setDocument() method
	public void setDocument(Document doc)
	{
		if (documentHandler != null && getDocument() != null)
			getDocument().removeDocumentListener(documentHandler);

		super.setDocument(doc);
		doc.addDocumentListener(documentHandler);
	} // }}}

	// {{{ getHistoryModel() method
	public HistoryModel getHistoryModel()
	{
		return history.getModel();
	} // }}}

	// {{{ setHistoryModel() method
	public void setHistoryModel(String name)
	{
		history.setModel(name);
	} // }}}

	// {{{ setHistoryIndex() method
	public void setHistoryIndex(int index)
	{
		history.setIndex(index);
	} // }}}

	// {{{ addActionListener() method
	public void addActionListener(ActionListener l)
	{
		listenerList.add(ActionListener.class, l);
	} // }}}

	// {{{ removeActionListener() method
	public void removeActionListener(ActionListener l)
	{
		listenerList.remove(ActionListener.class, l);
	} // }}}

	// {{{ fireActionEvent() method
	public void fireActionEvent(String code)
	{
		ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, code);

		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i++)
		{
			if (listeners[i] == ActionListener.class)
			{
				ActionListener l = (ActionListener) listeners[i + 1];
				l.actionPerformed(evt);
			}
		}
	} // }}}

	// {{{ getInput() method
	public String getInput()
	{
		try
		{
			Document doc = getDocument();
			int cmdStart = getInputStart();
			int length = doc.getLength();
			String line = doc.getText(cmdStart, length - cmdStart);
			if (line.endsWith("\n"))
				return line.substring(0, line.length() - 1);
			else
				return line;
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	} // }}}

	// {{{ setInput() method
	public void setInput(String line)
	{
		try
		{
			Document doc = getDocument();
			int cmdStart = getInputStart();
			doc.remove(cmdStart, doc.getLength() - cmdStart);
			doc.insertString(cmdStart, line, null);
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	} // }}}

	// {{{ getInputStart() method
	/**
	 *  Return InputStart position inside of current (associated with
	 *  ConsolePane) document.
	 */
	public int getInputStart()
	{
		Document d = getDocument();
		Object p = d.getProperty(InputStart);
		Integer i = (Integer) p;
		if (i == null) return 0;
		return i.intValue();
		/*
		 * return ((Integer)getDocument().getProperty(InputStart))
		 * .intValue();
		 */
	} // }}}

	// {{{ setInputStart() method
	/**
	 *  Setup InputStart position inside of current (associated with
	 *  ConsolePane) document.
	 *  ShellState class changes its InputStart position by oneself.
	 *  (see "Console.ShellState.writeSafely()" ).
	 */
	public void setInputStart(int cmdStart)
	{
		getDocument().putProperty(InputStart, Integer.valueOf(cmdStart));
	} // }}}

	// {{{ getPartialInput() method
	public String getPartialInput()
	{
		try
		{
			return getDocument().getText(getInputStart(),
				getCaretPosition() - getInputStart());
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	} // }}}

	// {{{ eval() method
	public void eval(String eval)
	{
		if (eval == null)
			return;

		try
		{
			StyledDocument doc = (StyledDocument) getDocument();
			setCaretPosition(doc.getLength());
			doc.insertString(doc.getLength(), eval + "\n", getCharacterAttributes());
		}
		catch (BadLocationException ble)
		{
			ble.printStackTrace();
		}

		fireActionEvent(eval);
	} // }}}

	// {{{ colorAttributes() method
	public static AttributeSet colorAttributes(Color color)
	{
		SimpleAttributeSet style = new SimpleAttributeSet();

		if (color != null) 
			style.addAttribute(StyleConstants.Foreground, color);
		else
		{
			if (jEdit.getBooleanProperty("textColors")) 
				color = jEdit.getColorProperty("view.fgColor", Color.BLACK);
			else
				color = jEdit.getColorProperty("console.plainColor", Color.BLACK);
			style.addAttribute(StyleConstants.Foreground, color);
		}
		 
		return style;
	} // }}}
	
	// {{{ eolExchangeRequired() method
	public static boolean eolExchangeRequired()
	{
		return eolReplacingPattern != null;
	} // }}}
	
	// {{{ eolExchanging() method
	public static String eolExchanging(String prevString)
	{
		if (eolReplacingPattern != null)
		{
			return eolReplacingPattern.matcher(prevString).replaceAll("\n");
		}
		
		return prevString;
	} // }}}
	
	// {{{ processKeyEvent method
	@Override
	protected void processKeyEvent(KeyEvent e)
	{
		int endpos = getDocument().getLength();
		int startpos = getInputStart();
		
		if (e.getID() == KeyEvent.KEY_TYPED && getCaretPosition() < startpos) 
			setCaretPosition(endpos);			
		
		super.processKeyEvent(e);
	} // }}}
	
	// {{{ Private members


	// {{{ getAttributes() method
	private AttributeSet getAttributes(int pos)
	{
		StyledDocument doc = (StyledDocument) getDocument();
		Element e = doc.getCharacterElement(pos);
		return e.getAttributes();
	} // }}}

	// {{{ getActions() method
	private Object[] getActions(int pos)
	{
		AttributeSet a = getAttributes(pos);
		if (a == null)
			return null;
		else
			return (Object[]) a.getAttribute(Actions);
	} // }}}

	// {{{ clickLink() method
	private void clickLink(int pos)
	{
		Object[] actions = getActions(pos);
		if (actions == null || actions.length == 0)
			return;

		if (actions.length == 0)
		{
			((Action) actions[0]).actionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, null));
			return;
		}

		JPopupMenu popup = new JPopupMenu();
		for (int i = 0; i < actions.length; i++)
			popup.add(new JMenuItem((Action) actions[i]));

		try
		{
			StyledDocument doc = (StyledDocument) getDocument();
			Element e = doc.getCharacterElement(pos);
			Point pt = modelToView(e.getStartOffset()).getLocation();
			FontMetrics fm = getFontMetrics(getFont());

			popup.show(this, pt.x, pt.y + fm.getHeight());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	} // }}}

	// }}}

	// {{{ Inner classes
	// {{{ MouseHandler class
	class MouseHandler extends MouseInputAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			Point pt = new Point(e.getX(), e.getY());
			int pos = viewToModel(pt);
			if (pos >= 0)
				clickLink(pos);
		}

		public void mouseMoved(MouseEvent e)
		{
			Point pt = new Point(e.getX(), e.getY());
			int pos = viewToModel(pt);
			if (pos >= 0)
			{
				Cursor cursor;
				if (getActions(pos) != null)
					cursor = MoveCursor;
				else
					cursor = DefaultCursor;

				if (getCursor() != cursor)
					setCursor(cursor);
			}
		}
	} // }}}

	// {{{ EnterAction class
	class EnterAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			/*
			 * setCaretPosition(getDocument().getLength());
			 * replaceSelection("\n");
			 * 
			 * history.addCurrentToHistory(); history.setIndex(-1);
			 */

			fireActionEvent(getInput());
		}
	} // }}}

	// {{{ BackspaceAction class
	class BackspaceAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (getSelectionStart() != getSelectionEnd())
			{
				replaceSelection("");
				return;
			}

			int caret = getCaretPosition();
			if (caret == getInputStart())
			{
				getToolkit().beep();
				return;
			}

			try
			{
				getDocument().remove(caret - 1, 1);
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
	} // }}}

	// {{{ DeleteInputAction class 
	/** Deletes the input that was typed by the user so far */
	class DeleteInputAction extends SelectHomeAction
	{
		public void actionPerformed(ActionEvent evt) {
			super.actionPerformed(evt);
			replaceSelection("");
		}
		
	}
	// }}}
	
	// {{{ HomeAction class
	class HomeAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			setCaretPosition(getInputStart());
		}
	} // }}}

	// {{{ SelectHomeAction class
	class SelectHomeAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			select(getInputStart(), getCaretPosition());
			
		}
	} // }}}

	// {{{ HistoryUpAction class
	class HistoryUpAction extends AbstractAction
	{
		private Action delegate;

		HistoryUpAction(Action delegate)
		{
			this.delegate = delegate;
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (getCaretPosition() >= getInputStart())
				history.historyPrevious();
			else
				delegate.actionPerformed(evt);
		}
	} // }}}

	// {{{ HistoryDownAction class
	class HistoryDownAction extends AbstractAction
	{
		private Action delegate;

		HistoryDownAction(Action delegate)
		{
			this.delegate = delegate;
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (getCaretPosition() >= getInputStart())
				history.historyNext();
			else
				delegate.actionPerformed(evt);
		}
	} // }}}

	// {{{ SearchUpAction class
	class SearchUpAction extends AbstractAction
	{
		private Action delegate;

		SearchUpAction(Action delegate)
		{
			this.delegate = delegate;
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (getCaretPosition() >= getInputStart())
				history.doBackwardSearch();
			else
				delegate.actionPerformed(evt);
		}
	} // }}}

	// {{{ SearchDownAction class
	class SearchDownAction extends AbstractAction
	{
		private Action delegate;

		SearchDownAction(Action delegate)
		{
			this.delegate = delegate;
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (getCaretPosition() >= getInputStart())
				history.doForwardSearch();
			else
				delegate.actionPerformed(evt);
		}
	} // }}}

	// {{{ DummyAction class
	class DummyAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
		}
	} // }}}

	// {{{ DocumentHandler class
	/**
	 *  This class adjusts InputStart position only when
	 *  document's content is changed BEFORE InputStart position.
	 */
	class DocumentHandler implements DocumentListener
	{
		public void insertUpdate(DocumentEvent e)
		{
			int offset = e.getOffset();
			int length = e.getLength();

			int cmdStart = getInputStart();
			if (offset < cmdStart)
				cmdStart += length;
			setInputStart(cmdStart);
		}

		public void removeUpdate(DocumentEvent e)
		{
			int offset = e.getOffset();
			int length = e.getLength();

			int cmdStart = getInputStart();
			if (offset < cmdStart)
			{
				if (offset + length > cmdStart)
					cmdStart = offset;
				else
					cmdStart -= length;
			}
			setInputStart(cmdStart);
		}

		public void changedUpdate(DocumentEvent e)
		{
		}
	} // }}}
	// }}}

}
