/*
 * Console.java - The console window
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001, 2002 Slava Pestov
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
import java.util.Arrays;
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

		Shell[] shells = Shell.getShells();
		Arrays.sort(shells,new MiscUtilities.StringICaseCompare());
		shellCombo = new JComboBox(shells);
		shellCombo.addActionListener(new ActionHandler());
		shellCombo.setRequestFocusEnabled(false);

		JPanel panel = new JPanel(new BorderLayout(6,0));
		panel.add(BorderLayout.WEST,shellCombo);

		ActionHandler actionHandler = new ActionHandler();

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		command = new ConsoleTextField(view);
		command.addActionListener(actionHandler);
		Dimension dim = command.getPreferredSize();
		dim.width = Integer.MAX_VALUE;
		command.setMaximumSize(dim);
		box.add(command);
		box.add(Box.createGlue());
		panel.add(BorderLayout.CENTER,box);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(run = new RolloverButton(RUN));
		run.setToolTipText(jEdit.getProperty("console.run"));
		Insets margin = new Insets(0,0,0,0);
		run.setMargin(margin);
		run.addActionListener(actionHandler);
		run.setRequestFocusEnabled(false);

		buttonBox.add(toBuffer = new RolloverButton(TO_BUFFER));
		toBuffer.setToolTipText(jEdit.getProperty("console.to-buffer"));
		toBuffer.setMargin(margin);
		toBuffer.addActionListener(actionHandler);
		toBuffer.setRequestFocusEnabled(false);

		buttonBox.add(stop = new RolloverButton(STOP));
		stop.setToolTipText(jEdit.getProperty("console.stop"));
		stop.setMargin(margin);
		stop.addActionListener(actionHandler);
		stop.setRequestFocusEnabled(false);

		buttonBox.add(clear = new RolloverButton(CLEAR));
		clear.setToolTipText(jEdit.getProperty("console.clear"));
		clear.setMargin(margin);
		clear.addActionListener(actionHandler);
		clear.setRequestFocusEnabled(false);

		animationLabel = new JLabel();
		animationLabel.setBorder(new EmptyBorder(2,3,2,3));
		Toolkit toolkit = getToolkit();
		animation = new AnimatedIcon(
			toolkit.getImage(Console.class.getResource("/console/Blank.png")),
			new Image[] {
				toolkit.getImage(Console.class.getResource("/console/Active1.png")),
				toolkit.getImage(Console.class.getResource("/console/Active2.png")),
				toolkit.getImage(Console.class.getResource("/console/Active3.png")),
				toolkit.getImage(Console.class.getResource("/console/Active4.png"))
			},10,animationLabel
		);
		animationLabel.setIcon(animation);
		buttonBox.add(animationLabel);

		panel.add(BorderLayout.EAST,buttonBox);

		add(BorderLayout.NORTH,panel);

		output = new JTextPane();
		JScrollPane scroller = new JScrollPane(output);
		scroller.setPreferredSize(new Dimension(400,100));
		add(BorderLayout.CENTER,scroller);

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

		animation.stop();
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
		command.setShell(shell);

		clear();
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

	//{{{ clear() method
	public void clear()
	{
		output.setText("");
		shell.printInfoMessage(this);
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
		animation.start();

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

		try
		{
			shell.execute(this,output,cmd);
		}
		catch(RuntimeException e)
		{
			print(getErrorColor(),e.toString());
			Log.log(Log.ERROR,this,e);
			commandDone();
		}
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
			outputDocument.insertString(outputDocument.getLength(),
				msg,style);
			outputDocument.insertString(outputDocument.getLength(),
				"\n",style);
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
				animation.stop();
			}
		});
	} //}}}

	//{{{ Private members

	//{{{ Icons
	private static final Icon RUN = GUIUtilities.loadIcon("Run.png");
	private static final Icon TO_BUFFER = GUIUtilities.loadIcon("RunToBuffer.png");
	private static final Icon STOP = GUIUtilities.loadIcon("Cancel.png");
	private static final Icon CLEAR = GUIUtilities.loadIcon("Clear.png");
	//}}}

	//{{{ Instance variables
	private View view;
	private JComboBox shellCombo;
	private Shell shell;
	private ConsoleTextField command;
	private RolloverButton run, toBuffer, stop, clear;
	private JLabel animationLabel;
	private AnimatedIcon animation;

	private JTextPane output;
	private SimpleAttributeSet style;

	private Color infoColor, warningColor, errorColor;

	private DefaultErrorSource errorSource;
	//}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		style = new SimpleAttributeSet();

		output.setBackground(jEdit.getColorProperty("console.bgColor"));
		output.setForeground(jEdit.getColorProperty("console.plainColor"));
		output.setFont(jEdit.getFontProperty("console.font"));

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
				clear();
		}
	} //}}}
}
