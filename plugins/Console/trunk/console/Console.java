/*
 * Console.java - The console window
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

//{{{ Imports
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import errorlist.*;
//}}}

public class Console extends JPanel
implements EBComponent, Output
{
	//{{{ Console constructor
	public Console(View view)
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
		buttonBox.add(run = new JButton(RUN));
		run.setToolTipText(jEdit.getProperty("console.run"));
		Insets margin = new Insets(0,0,0,0);
		run.setMargin(margin);
		run.addActionListener(actionHandler);
		run.setRequestFocusEnabled(false);

		buttonBox.add(toBuffer = new JButton(TO_BUFFER));
		toBuffer.setToolTipText(jEdit.getProperty("console.to-buffer"));
		toBuffer.setMargin(margin);
		toBuffer.addActionListener(actionHandler);
		toBuffer.setRequestFocusEnabled(false);

		buttonBox.add(stop = new JButton(STOP));
		stop.setToolTipText(jEdit.getProperty("console.stop"));
		stop.setMargin(margin);
		stop.addActionListener(actionHandler);
		stop.setRequestFocusEnabled(false);

		buttonBox.add(clear = new JButton(CLEAR));
		clear.setToolTipText(jEdit.getProperty("console.clear"));
		clear.setMargin(margin);
		clear.addActionListener(actionHandler);
		clear.setRequestFocusEnabled(false);

		animation = new JLabel(NO_ANIMATION);
		animation.setBorder(new EmptyBorder(1,3,1,1));
		buttonBox.add(animation);

		panel.add(BorderLayout.EAST,buttonBox);

		add(BorderLayout.NORTH,panel);

		output = new JTextPane();
		add(BorderLayout.CENTER,new JScrollPane(output));

		propertiesChanged();
		setShell(ConsolePlugin.SYSTEM_SHELL);
	} //}}}

	//{{{ requestDefaultFocus() method
	public boolean requestDefaultFocus()
	{
		command.requestFocus();
		return true;
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
		SystemShell.consoleOpened(this);

		errorSource = new DefaultErrorSource("error parsing");
		ErrorSource.registerErrorSource(errorSource);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
		SystemShell.consoleClosed(this);

		errorSource.clear();
		ErrorSource.unregisterErrorSource(errorSource);
	} //}}}

	//{{{ getView() method
	public View getView()
	{
		return view;
	} //}}}

	//{{{ getShell() method
	public Shell getShell()
	{
		return (Shell)shellCombo.getSelectedItem();
	} //}}}

	//{{{ setShell() method
	public void setShell(Shell shell)
	{
		if(this.shell == shell)
			return;

		this.shell = shell;

		shellCombo.setSelectedItem(shell);
		command.setModel("console." + shell.getName());

		shell.printInfoMessage(this);
	} //}}}

	//{{{ getTextField() method
	public HistoryTextField getTextField()
	{
		return command;
	} //}}}

	//{{{ getOutputPane() method
	public JTextPane getOutputPane()
	{
		return output;
	} //}}}

	//{{{ run() method
	/**
	 * Runs the specified command. Note that with most shells, this
	 * method returns immediately, and execution of the command continues
	 * in a different thread. If you want to wait for command completion,
	 * call the <code>waitFor()</code> method of the shell instance.
	 *
	 * @param shell The shell instance. Obtain one either with
	 * <code>Console.getShell()</code> or <code>Shell.getShell()</code>.
	 * @param output The output instance. Either the console instance,
	 * or a new instance of <code>BufferOutput</code>.
	 * @param cmd The command
	 */
	public void run(Shell shell, Output output, String cmd)
	{
		animation.setIcon(ANIMATION);

		Macros.Recorder recorder = view.getMacroRecorder();
		if(recorder != null)
		{
			if(output == this)
			{
				recorder.record("runCommandInConsole(view,\""
					+ shell.getName()
					+ "\",\""
					+ MiscUtilities.charsToEscapes(cmd)
					+ "\");");
			}
			else if(output instanceof BufferOutput)
			{
				recorder.record("runCommandToBuffer(view,\""
					+ shell.getName()
					+ "\",\""
					+ MiscUtilities.charsToEscapes(cmd)
					+ "\");");
			}
		}

		HistoryModel.getModel("console." + shell.getName()).addItem(cmd);
		print(infoColor,"> " + cmd);
		shell.execute(this,output,cmd);
	} //}}}

	//{{{ runLastCommand() method
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
			run(getShell(),this,history.getItem(0));
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
	} //}}}

	//{{{ getErrorSource() method
	/**
	 * Returns this console's error source instance. Plugin shells can
	 * either add errors to this error source, or use their own; both
	 * methods will look the same to the user.
	 */
	public DefaultErrorSource getErrorSource()
	{
		return errorSource;
	} //}}}

	//{{{ getInfoColor() method
	/**
	 * Returns the informational text color.
	 */
	public Color getInfoColor()
	{
		return infoColor;
	} //}}}

	//{{{ getWarningColor() method
	/**
	 * Returns the warning text color.
	 */
	public Color getWarningColor()
	{
		return warningColor;
	} //}}}

	//{{{ getErrorColor() method
	/**
	 * Returns the error text color.
	 */
	public Color getErrorColor()
	{
		return errorColor;
	} //}}}

	//{{{ print() method
	/**
	 * Prints a string of text with the specified color.
	 * @param color The color. If null, the default color will be used
	 * @param msg The message
	 */
	public synchronized void print(Color color, String msg)
	{
		final Document outputDocument = output.getDocument();

		if(color != null)
			style.addAttribute(StyleConstants.Foreground,color);
		else
			style.removeAttribute(StyleConstants.Foreground);

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

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				output.setCaretPosition(outputDocument.getLength());
			}
		});
	} //}}}

	//{{{ commandDone() method
	/**
	 * Called when the command finishes executing.
	 */
	public void commandDone()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				animation.setIcon(NO_ANIMATION);
			}
		});
	} //}}}

	//{{{ Private members

	//{{{ Icons
	private static final ImageIcon RUN = new ImageIcon(
		Console.class.getResource("/console/Play16.gif"));
	private static final ImageIcon TO_BUFFER = new ImageIcon(
		Console.class.getResource("/console/Edit16.gif"));
	private static final ImageIcon STOP = new ImageIcon(
		Console.class.getResource("/console/Stop16.gif"));
	private static final ImageIcon CLEAR = new ImageIcon(
		Console.class.getResource("/console/New16.gif"));
	private static final ImageIcon ANIMATION = new ImageIcon(
		Console.class.getResource("/console/fish_anim.gif"));
	private static final ImageIcon NO_ANIMATION = new ImageIcon(
		Console.class.getResource("/console/fish.gif"));
	//}}}

	//{{{ Instance variables
	private View view;
	private JComboBox shellCombo;
	private Shell shell;
	private HistoryTextField command;
	private JButton run, toBuffer, stop, clear;
	private JLabel animation;

	private JTextPane output;
	private SimpleAttributeSet style;

	private Color infoColor, warningColor, errorColor;

	private DefaultErrorSource errorSource;
	//}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		style = new SimpleAttributeSet();

		StyleConstants.setFontFamily(style,jEdit.getProperty("console.font"));
		StyleConstants.setFontSize(style,jEdit.getIntegerProperty("console.fontsize",12));

		int _style = jEdit.getIntegerProperty("console.fontstyle",Font.PLAIN);
		StyleConstants.setBold(style,(_style & Font.BOLD) != 0);
		StyleConstants.setItalic(style,(_style & Font.ITALIC) != 0);

		output.setBackground(jEdit.getColorProperty("console.bgColor"));
		output.setForeground(jEdit.getColorProperty("console.plainColor"));
		infoColor = jEdit.getColorProperty("console.infoColor");
		warningColor = jEdit.getColorProperty("console.warningColor");
		errorColor = jEdit.getColorProperty("console.errorColor");
	} //}}}

	//}}}

	//{{{ ActionHandler class
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
				run(getShell(),Console.this,cmd);
			}
			else if(source == toBuffer)
			{
				String cmd = command.getText();
				if(cmd == null || cmd.length() == 0)
					return;

				command.setText(null);
				run(getShell(),new BufferOutput(Console.this),cmd);
			}
			else if(source == stop)
				shell.stop(Console.this);
			else if(source == clear)
				output.setText("");
		}
	} //}}}
}
