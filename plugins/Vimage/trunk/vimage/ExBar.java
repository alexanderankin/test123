
/*
 * ExBar.java - Vimage plugin
 *
 * Copyright (C) 2005 Ollie Rutherfurd <oliver@jedit.org>
 * Copyright (C) 2009 Matthew Gilbert <gilbert@voxmea.net>
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

package vimage;

import java.awt.event.*;
import java.awt.*;
import java.util.Arrays;
import javax.swing.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.jedit.bsh.BshMethod;

public class ExBar extends JPanel implements FocusListener
{
	protected View view;
	protected HistoryTextField input;
	protected RolloverButton close;
    protected VimageMap map;

	public ExBar(final View view)
	{
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

		this.view = view;

		//add(Box.createHorizontalStrut(2));
		add(input = new ExTextField());
		input.addFocusListener(this);
		input.setEnterAddsToHistory(true);	// ??? really wanted
		Dimension max = input.getPreferredSize();
		max.width = Integer.MAX_VALUE;
		input.setMaximumSize(max);
		input.addActionListener(new ActionHandler());
		// XXX input.getDocument().addDocumentListener(new DocumentHandler());

		close = new RolloverButton(GUIUtilities.loadIcon("closebox.gif"));
		close.addActionListener(new ActionHandler());
		close.setToolTipText(jEdit.getProperty("view.action.close-tooltip"));
		add(close);
	}
	
	public void focusGained(FocusEvent e)
	{
	}
	
	public void focusLost(FocusEvent e)
	{
        view.removeToolBar(this);
	}

	public HistoryTextField getField()
	{
		return input;
	}

	public void goToExBar(String text)
	{
		input.setText(text);
		input.requestFocus();
	}

	private void invoke()
	{
        final View view = this.view;
        view.removeToolBar(this);
        view.getTextArea().requestFocus();
		SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
                    //view.getTextArea().requestFocus();
                    VimageInputHandler vih;
                    vih = (VimageInputHandler)view.getInputHandler();
                    VimageMap map = vih.getMap();

                    String text = input.getText();

                    // First word is the command, pass everything else as argv
                    String[] parts = text.split("(?<!\\\\)\\s");
                    if (parts.length == 0)
                        return;
                    String command = parts[0];
                    String[] argv = new String[] {};
                    if (parts.length > 1) {
                        try {
                            java.util.List<String> list = Arrays.asList(parts);
                            list = list.subList(1, parts.length - 1);
                            argv = list.toArray(argv);
                        } catch (java.lang.Exception ex) {
                            Log.log(Log.DEBUG, this, ex);
                        }
                    } 

                    try {
                    final BshMethod method = map.get("command", command);
                    if(method == null)
                    {
                        // Maybe a jEdit action?
                        if (parts.length == 1) {
                            EditAction ea = jEdit.getAction(command);
                            if (ea != null) {
                                ea.invoke(view);
                                return;
                            }
                        }

                        Log.log(Log.DEBUG, this, "no match for: " + input.getText());
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }

                    try {
                        NameSpace namespace = new NameSpace(BeanShell.getNameSpace(), "Vimage");
                        namespace.setVariable("mode", vih);
                        namespace.setVariable("argv", argv);
                        BeanShell.runCachedBlock(method, view, namespace);
                    } catch (java.lang.Exception ex) {
                        Log.log(Log.DEBUG, this, "command failed: " + ex);
                    }
                    } catch (java.lang.Exception ex) {
                        Log.log(Log.ERROR, this, ex);
                    }
				}
			});
	}

	private void close(boolean reset)
	{
		view.removeToolBar(this);
		view.getTextArea().requestFocus();
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == close)
				close(true);
			else
				invoke();
		}
	}

	class ExTextField extends HistoryTextField
	{
		ExTextField()
		{
			super("ex");
			setSelectAllOnFocus(false);
		}

        /*
		public boolean isManagingFocus()
		{
			return true;
		}
        */

		public boolean getFocusTraversalKeysEnabled()
		{
			return false;
		}

		public void processKeyEvent(KeyEvent evt)
		{
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;
			int keyCode = evt.getKeyCode();
			switch(evt.getID())
			{
			case KeyEvent.KEY_PRESSED:
				if(keyCode == KeyEvent.VK_ESCAPE)
				{
					evt.consume();
					close(true);
					return;
				}
				else if(keyCode == KeyEvent.VK_BACK_SPACE)
				{
					// XXX this is pretty crappy
					if(input.getText().length() == 1)
					{
						evt.consume();
						input.setText("");
						close(true);
						return;
					}
				}
			}
			super.processKeyEvent(evt);
		}
	}
}
