/*
 * Console.java - The console window
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import errorlist.*;
//}}}

public class Console extends JPanel
implements EBComponent, Output, DefaultFocusComponent
{
	//{{{ Console constructor
	public Console(View view)
	{
		super(new BorderLayout());

		this.view = view;

		shellHash = new HashMap();

		initGUI();

		propertiesChanged();
		setShell("System");
	} //}}}

	//{{{ focusOnDefaultComponent() method
	public void focusOnDefaultComponent()
	{
		command.requestFocus();
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
		SystemShell.consoleOpened(this);

		errorSource = new DefaultErrorSource("error parsing");
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
		SystemShell.consoleClosed(this);

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
		return Shell.getShell((String)shellCombo.getSelectedItem());
	} //}}}

	//{{{ setShell() method
	public void setShell(String shell)
	{
		setShell(Shell.getShell(shell));
	} //}}}

	//{{{ setShell() method
	public void setShell(Shell shell)
	{
		if(shell == null)
			throw new NullPointerException();

		if(shell == this.shell)
			return;

		this.shell = shell;

		shellState = (ShellState)shellHash.get(shell.getName());

		shellCombo.setSelectedItem(shell.getName());
		command.setModel("console." + shell.getName());

		if(shellState != null)
			output.setDocument(shellState.scrollback);
		else
		{
			shellState = new ShellState(shell);
			output.setDocument(shellState.scrollback);
			shellHash.put(shell.getName(),shellState);
		}

		updateAnimation();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				output.setCaretPosition(
					output.getDocument().getLength()
				);
			}
		});
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
		getShell().printInfoMessage(shellState);
		getShell().printPrompt(this,shellState);
	} //}}}

	//{{{ getOutput() method
	/**
	 * Returns the output instance for the current shell.
	 * @since Console 3.6
	 */
	public Output getOutput()
	{
		return shellState;
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
	 * @param output The output stream. Either the return value of
	 * <code>getOutput()</code>, or a new instance of
	 * <code>BufferOutput</code>.
	 * @param cmd The command
	 */
	public void run(Shell shell, Output output, String cmd)
	{
		// backwards compatibility
		if(output == this)
			output = shellState;
		run(shell,view.getTextArea().getSelectedText(),
			output,getOutput(),cmd);
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
	 * @param input The input to send to the command
	 * @param output The output stream. Either the return value of
	 * <code>getOutput()</code>, or a new instance of
	 * <code>BufferOutput</code>.
	 * @param error The error stream. Either the return value of
	 * <code>getOutput()</code>, or a new instance of
	 * <code>BufferOutput</code>.
	 * @param cmd The command
	 */
	public void run(Shell shell, String input, Output output, 
		Output error, String cmd)
	{
		if(cmd.startsWith(":"))
		{
			Shell _shell = Shell.getShell(cmd.substring(1));
			if(_shell != null)
			{
				setShell(_shell);
				return;
			}
		}

		setShell(shell);

		ShellState state = (ShellState)shellHash.get(shell.getName());
		state.commandRunning = true;

		updateAnimation();

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

		errorSource.clear();
		ErrorSource.unregisterErrorSource(errorSource);

		try
		{
			shell.execute(this,input,output,error,cmd);
		}
		catch(RuntimeException e)
		{
			print(getErrorColor(),e.toString());
			Log.log(Log.ERROR,this,e);
			output.commandDone();
			error.commandDone();
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
			run(getShell(),getOutput(),history.getItem(0));
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
		else if(msg instanceof PluginUpdate)
			handlePluginUpdate((PluginUpdate)msg);
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
	 * @deprecated Do not use the console as an <code>Output</code>
	 * instance, use the <code>Output</code> given to you in
	 * <code>Shell.execute()</code> instead.
	 */
	public void print(Color color, String msg)
	{
		getOutput().print(color,msg);
	} //}}}

	//{{{ commandDone() method
	/**
	 * @deprecated Do not use the console as an <code>Output</code>
	 * instance, use the <code>Output</code> given to you in
	 * <code>Shell.execute()</code> instead.
	 */
	public void commandDone()
	{
		getOutput().commandDone();
	} //}}}

	//{{{ Private members

	//{{{ Icons
	private static final Icon RUN_AGAIN = GUIUtilities.loadIcon("RunAgain.png");
	private static final Icon RUN = GUIUtilities.loadIcon("Run.png");
	private static final Icon TO_BUFFER = GUIUtilities.loadIcon("RunToBuffer.png");
	private static final Icon STOP = GUIUtilities.loadIcon("Cancel.png");
	private static final Icon CLEAR = GUIUtilities.loadIcon("Clear.png");
	//}}}

	//{{{ Instance variables
	private View view;

	private Map shellHash;
	private ShellState shellState;
	private Shell shell;

	private JComboBox shellCombo;

	private ConsoleTextField command;
	private RolloverButton runAgain, run, toBuffer, stop, clear;
	private JLabel animationLabel;
	private AnimatedIcon animation;

	private JTextPane output;

	private Color infoColor, warningColor, errorColor;

	private DefaultErrorSource errorSource;
	//}}}

	//{{{ initGUI() method
	private void initGUI()
	{
		JPanel panel = new JPanel(new BorderLayout(6,0));

		String[] shells = Shell.getShellNames();
		Arrays.sort(shells,new MiscUtilities.StringICaseCompare());

		shellCombo = new JComboBox(shells);
		shellCombo.addActionListener(new ActionHandler());
		shellCombo.setRequestFocusEnabled(false);

		panel.add(BorderLayout.WEST,shellCombo);

		ActionHandler actionHandler = new ActionHandler();

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		command = new ConsoleTextField(view,this,null);
		command.addActionListener(actionHandler);
		Dimension dim = command.getPreferredSize();
		dim.width = Integer.MAX_VALUE;
		command.setMaximumSize(dim);
		box.add(command);
		box.add(Box.createGlue());
		panel.add(BorderLayout.CENTER,box);

		Box buttonBox = new Box(BoxLayout.X_AXIS);

		buttonBox.add(runAgain = new RolloverButton(RUN_AGAIN));
		runAgain.setToolTipText(jEdit.getProperty("run-last-console-command.label"));
		Insets margin = new Insets(0,0,0,0);
		runAgain.setMargin(margin);
		runAgain.addActionListener(actionHandler);
		runAgain.setRequestFocusEnabled(false);

		buttonBox.add(run = new RolloverButton(RUN));
		run.setToolTipText(jEdit.getProperty("console.run"));
		margin = new Insets(0,0,0,0);
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
	} //}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		output.setBackground(jEdit.getColorProperty("console.bgColor"));
		output.setForeground(jEdit.getColorProperty("console.plainColor"));
		output.setFont(jEdit.getFontProperty("console.font"));

		infoColor = jEdit.getColorProperty("console.infoColor");
		warningColor = jEdit.getColorProperty("console.warningColor");
		errorColor = jEdit.getColorProperty("console.errorColor");
	} //}}}

	//{{{ handlePluginUpdate() method
	public void handlePluginUpdate(PluginUpdate pmsg)
	{
		if(pmsg.getWhat() == PluginUpdate.LOADED
			|| pmsg.getWhat() == PluginUpdate.UNLOADED)
		{
			String[] shells = Shell.getShellNames();
			shellCombo.setModel(new DefaultComboBoxModel(shells));
			shellCombo.setSelectedItem(shell.getName());

			Iterator iter = shellHash.keySet().iterator();
			while(iter.hasNext())
			{
				String name = (String)iter.next();
				if(Shell.getShell(name) == null)
					iter.remove();
			}
		}
	} //}}}

	//{{{ updateAnimation() method
	private void updateAnimation()
	{
		if(shellState.commandRunning)
			animation.start();
		else
			animation.stop();
	} //}}}

	//}}}

	//{{{ ShellState class
	class ShellState implements Output
	{
		Shell shell;
		Document scrollback;
		boolean commandRunning;

		ShellState(Shell shell)
		{
			this.shell = shell;
			scrollback = new DefaultStyledDocument();
			shell.printInfoMessage(this);
			shell.printPrompt(Console.this,this);
		}

		//{{{ print() method
		public void print(final Color color,
			final String msg)
		{
			try
			{
				if(SwingUtilities.isEventDispatchThread())
					printSafely(color,msg);
				else
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						public void run()
						{
							printSafely(color,msg);
						}
					});
				}
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR,this,e);
			}
		} //}}}

		//{{{ commandDone() method
		public void commandDone()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if(commandRunning)
						shell.printPrompt(Console.this,ShellState.this);
					commandRunning = false;
					updateAnimation();
					if(errorSource.getErrorCount() != 0)
						ErrorSource.registerErrorSource(errorSource);
				}
			});
		} //}}}

		//{{{ printSafely() method
		private void printSafely(Color color, String msg)
		{
			SimpleAttributeSet style = new SimpleAttributeSet();

			if(color != null)
				style.addAttribute(StyleConstants.Foreground,color);
			else
				style.removeAttribute(StyleConstants.Foreground);

			try
			{
				scrollback.insertString(scrollback.getLength(),
					msg,style);
				scrollback.insertString(scrollback.getLength(),
					"\n",style);
			}
			catch(BadLocationException bl)
			{
				Log.log(Log.ERROR,this,bl);
			}
		} //}}}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if(source == shellCombo)
				setShell((String)shellCombo.getSelectedItem());
			else if(source == command || source == run)
			{
				String cmd = command.getText();
				if(cmd == null || cmd.length() == 0)
					return;

				command.setText(null);
				run(getShell(),Console.this,cmd);
			}
			else if(source == runAgain)
			{
				runLastCommand();
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
				getShell().stop(Console.this);
			else if(source == clear)
				clear();
		}
	} //}}}
}
