/*
 * Console.java - The console window
 * Copyright (C) 2000, 2001 Slava Pestov
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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class Console extends JPanel implements DockableWindow, EBComponent
{
	Console(View view)
	{
		super(new BorderLayout());

		this.view = view;

		shellCombo = new JComboBox(Shell.getShells());
		shellCombo.addActionListener(new ActionHandler());

		JPanel panel = new JPanel(new BorderLayout(6,0));
		panel.add(BorderLayout.WEST,shellCombo);

		ActionHandler actionHandler = new ActionHandler();

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		command = new HistoryTextField("console");
		command.addActionListener(actionHandler);
		Dimension dim = command.getPreferredSize();
		dim.width = Integer.MAX_VALUE;
		command.setMaximumSize(dim);
		box.add(command);
		box.add(Box.createGlue());
		panel.add(BorderLayout.CENTER,box);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(run = new JButton(jEdit.getProperty("console.run")));
		Insets margin = new Insets(1,1,1,3);
		run.setMargin(margin);
		run.addActionListener(actionHandler);
		run.setRequestFocusEnabled(false);

		buttonBox.add(stop = new JButton(jEdit.getProperty("console.stop")));
		stop.setMargin(margin);
		stop.addActionListener(actionHandler);
		stop.setRequestFocusEnabled(false);

		buttonBox.add(toBuffer = new JButton(jEdit.getProperty("console.to-buffer")));
		toBuffer.setMargin(margin);
		toBuffer.addActionListener(actionHandler);
		toBuffer.setRequestFocusEnabled(false);

		panel.add(BorderLayout.EAST,buttonBox);

		add(BorderLayout.NORTH,panel);

		output = new JTextPane();
		add(BorderLayout.CENTER,new JScrollPane(output));

		propertiesChanged();
		setShell(ConsolePlugin.SYSTEM_SHELL);
	}

	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
		SystemShell.consoleOpened(this);

		errorSource = new DefaultErrorSource("error parsing");
		EditBus.addToBus(errorSource);
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
		SystemShell.consoleClosed(this);

		errorSource.clear();
		EditBus.removeFromBus(errorSource);
		EditBus.removeFromNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
	}

	public View getView()
	{
		return view;
	}

	// dockable window implementation
	public String getName()
	{
		return "console";
	}

	public Component getComponent()
	{
		return this;
	}

	public void setShell(String shell)
	{
		Shell[] shells = Shell.getShells();
		for(int i = 0; i < shells.length; i++)
		{
			if(shells[i].getName().equals(shell))
			{
				setShell(shells[i]);
				return;
			}
		}
	}

	public void setShell(Shell shell)
	{
		if(this.shell == shell)
			return;

		this.shell = shell;

		shellCombo.setSelectedItem(shell);
		command.setModel("console." + shell.getName());

		shell.printInfoMessage(this);
	}

	public HistoryTextField getTextField()
	{
		return command;
	}

	public boolean runAndWait(String cmd)
	{
		run(cmd);
		return shell.waitFor();
	}

	public void run(String cmd)
	{
		// Add to history
		command.getModel().addItem(cmd);

		// Record the command
		Macros.Recorder recorder = view.getMacroRecorder();
		if(recorder != null)
		{
			recorder.record("runCommandInConsole(view,\""
				+ shell.getName()
				+ "\",\""
				+ MiscUtilities.charsToEscapes(cmd)
				+ "\")");
		}

		printInfo("> " + cmd);

		shell.execute(this,cmd);
	}

	/**
	 * Meant to be used as a user action.
	 */
	public void runLastCommand()
	{
		HistoryModel history = command.getModel();
		if(history.getSize() == 0)
		{
			getToolkit().beep();
			return;
		}
		else
			run(history.getItem(0));
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
	}

	/**
	 * Returns this console's error source instance. Plugin shells can
	 * either add errors to this error source, or use their own; both
	 * methods will look the same to the user.
	 */
	public DefaultErrorSource getErrorSource()
	{
		return errorSource;
	}

	/**
	 * Prints the specified line of text, checking it against defined
	 * error patterns.
	 */
	public void printAndParseError(String line, String directory)
	{
		int type = ConsolePlugin.parseLine(line,directory,errorSource);

		switch(type)
		{
		case ErrorSource.ERROR:
			printError(line);
			break;
		case ErrorSource.WARNING:
			printWarning(line);
			break;
		default:
			printPlain(line);
			break;
		}
	}

	/**
	 * Convinience wrapper around print().
	 */
	public void printPlain(String msg)
	{
		print(msg,null);
	}

	/**
	 * Convinience wrapper around print().
	 */
	public void printInfo(String msg)
	{
		print(msg,infoColor);
	}

	/**
	 * Convinience wrapper around print().
	 */
	public void printWarning(String msg)
	{
		print(msg,warningColor);
	}

	/**
	 * Convinience wrapper around print().
	 */
	public void printError(String msg)
	{
		print(msg,errorColor);
	}

	/**
	 * Returns the informational text color.
	 */
	public Color getInfoColor()
	{
		return infoColor;
	}

	/**
	 * Returns the warning text color.
	 */
	public Color getWarningColor()
	{
		return warningColor;
	}

	/**
	 * Returns the error text color.
	 */
	public Color getErrorColor()
	{
		return errorColor;
	}

	/**
	 * Prints a string of text with the specified color.
	 */
	public synchronized void print(String msg, Color color)
	{
		Document outputDocument = output.getDocument();

		SimpleAttributeSet style = new SimpleAttributeSet();

		if(color != null)
			style.addAttribute(StyleConstants.Foreground,color);

		try
		{
			// split long output text in small chunks of size 800:
			StringChunkTokenizer sct =
				new StringChunkTokenizer(msg,800," ,;\t\n",80);
			while (sct.hasMoreTokens())
			{
				String chunk = sct.nextToken();
				if (chunk.length() > 0)
					outputDocument.insertString(
						outputDocument.getLength(),
						chunk,style);
				if (chunk.length() == 0 ||
				    chunk.charAt(chunk.length()-1) != '\n')
					outputDocument.insertString(
						outputDocument.getLength(),
						"\n",null);
			}
		}
		catch(BadLocationException bl)
		{
			Log.log(Log.ERROR,this,bl);
		}

		output.setCaretPosition(outputDocument.getLength());
	}

	/**
	 * Removes all output.
	 */
	public void clear()
	{
		output.setText("");
	}

	// protected members
	protected JTextPane output;

	// private members
	private View view;
	private JComboBox shellCombo;
	private Shell shell;
	private HistoryTextField command;
	private JButton run, stop, toBuffer;

	private Color infoColor, warningColor, errorColor;

	private DefaultErrorSource errorSource;

	private void propertiesChanged()
	{
		output.setBackground(GUIUtilities.parseColor(jEdit.getProperty(
			"console.bgColor")));
		output.setForeground(GUIUtilities.parseColor(jEdit.getProperty(
			"console.plainColor")));
		infoColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.infoColor"));
		warningColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.warningColor"));
		errorColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.errorColor"));
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if(source == shellCombo)
				setShell((Shell)shellCombo.getSelectedItem());
			else if(source == command || source == run)
			{
				String cmd = command.getText();
				if(cmd == null || cmd.length() == 0)
					return;

				command.setText(null);
				run(cmd);
			}
			else if(source == stop)
			{
				shell.stop(Console.this);
			}
			else if(source == toBuffer)
			{
				Buffer buffer = jEdit.newFile(view);
				try
				{
					Document outputDocument = output.getDocument();
					String text = outputDocument.getText(0,
						outputDocument.getLength());
					buffer.insertString(0,text,null);
				}
				catch(BadLocationException bl)
				{
					Log.log(Log.ERROR,this,bl);
				}
			}
		}
	}
}
