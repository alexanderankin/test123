/*
 * Console.java - The console window
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2005 Slava Pestov
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
//}}}

/**
 * Console - an instance of a panel inside a dockablewindow.
 * May contain multiple Shells, each with its own shell state.
 *  
 * 
 * @version $Id$
 */

public class Console extends JPanel
implements EBComponent, DefaultFocusComponent
{
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
	private Map<String, ShellState> shellStateMap;

	// A pointer to the currently selected Shell instance
	private Shell currentShell;

	// The Output instance corresponding to the current shell.
	private ShellState shellState;
	
	// The selector of shells
	private JComboBox shellCombo;
	
	private RolloverButton runAgain, run, toBuffer, stop, clear, configure;
	private JLabel animationLabel;
	private AnimatedIcon animation;
	private ConsolePane text;
	private Color infoColor, warningColor, errorColor, plainColor;
	private DefaultErrorSource errorSource;
	
	//{{{ Console constructor
	public Console(View view)
	{
		super(new BorderLayout());

		this.view = view;

		shellStateMap = new HashMap<String, ShellState>();

		initGUI();

		propertiesChanged();
		Shell s = Shell.getShell("System");
		setShell(s);
	} //}}}

		
	//{{{ focusOnDefaultComponent() method
	public void focusOnDefaultComponent()
	{
		text.requestFocus();
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);

		errorSource = new DefaultErrorSource("error parsing");
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);

		ErrorSource.unregisterErrorSource(errorSource);

		Iterator iter = shellStateMap.values().iterator();
		while(iter.hasNext())
		{
			ShellState state = (ShellState)iter.next();
			state.shell.closeConsole(this);
		}
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
		String shellName = (String)shellCombo.getSelectedItem();
		return Shell.getShell(shellName);
	} //}}}

	//{{{ setShell() method
	public Shell setShell(String shell)
	{
		return setShell(Shell.getShell(shell));
	} //}}}

	//{{{ setShell() method
	/**
	 * Creates a ShellState (output instance) if necessary.
	 * Sets the current active shell to be this new shell.
	 */
	public Shell setShell(Shell shell)
	{
		if(shell == null)
			return null;

		String name = shell.getName();
		text.setHistoryModel(getShellHistory(shell));

		shellState = (ShellState)shellStateMap.get(name);
		if(shellState == null)
		{
			shellState = new ShellState(shell);
			shellStateMap.put(shell.getName(),shellState);
			shell.printInfoMessage(shellState);
			shell.printPrompt(this,shellState);
		}

		text.setDocument(shellState.scrollback);
		updateAnimation();
		if (shell != this.currentShell) {
			shellCombo.setSelectedItem(name);
		}
		this.currentShell = shell;
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				text.setCaretPosition(text.getDocument().getLength());
				text.scrollRectToVisible(text.getVisibleRect());
				updateAnimation();
			}
		});
		return shell;
	} //}}}

	//{{{ getConsolePane() method
	public ConsolePane getConsolePane()
	{
		return text;
	} //}}}

	//{{{ getOutputPane() method
	/**
	 * @deprecated Use getConsolePane() instead.
	 */
	public JTextPane getOutputPane()
	{
		return text;
	} //}}}

	//{{{ clear() method
	public void clear()
	{
		text.setText("");
		getShell().printInfoMessage(shellState);
	} //}}}

	//{{{ getOutput() method
	/**
	 * Returns the output instance for the currently selected Shell.
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
		run(shell,input,output,error,cmd,true);
	} //}}}


	public void run(Shell shell, String command) {
		run (shell, null, null, null, command);
	}
	/**
	 * Convenience function currently used by some beanshell macros.
	 * @param shell the shell to execute it in
	 * @param output something to write to
	 * @param command the thing to execute
	 * 
	 */
	public void run(Shell shell, Output output, String command) {
		run(shell, null, output, null, command);
	}
	
	//{{{ runLastCommand() method
	/**
	 * Meant to be used as a user action.
	 */
	public void runLastCommand()
	{
		HistoryModel history = text.getHistoryModel();
		if(history.getSize() == 0)
		{
			getToolkit().beep();
			return;
		}
		else
			run(getShell(),null, getOutput(), null, history.getItem(0));
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

	public Color getPlainColor() {
		return plainColor;
	}
	
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

	//{{{ writeAttrs() method
	/**
	 * @deprecated Do not use the console as an <code>Output</code>
	 * instance, use the <code>Output</code> given to you in
	 * <code>Shell.execute()</code> instead.
	 * 
	 * see @ref Output for information about how to create additional 
	 *    console Output instances.
	 */
	public void writeAttrs(AttributeSet attrs, String msg)
	{
		getOutput().writeAttrs(attrs,msg);
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

	//{{{ getShellState() method
	/**
	 * @since Console 4.0.2.
	 */
	public ShellState getShellState(Shell shell)
	{
		return (ShellState)shellStateMap.get(shell.getName());
	} //}}}


	
	public void stopAnimation() {
		shellState.commandRunning=false;
		animation.stop();
	}

	
	public void startAnimation() {
		currentShell = getShell();
		shellState = getShellState(currentShell);
		shellState.commandRunning = true;
		animationLabel.setVisible(true);
		animation.start();
		animation.setRate(5);
	}

	
	//}}}

	//{{{ run() method

	private void run(Shell shell, String input, Output output,
		Output error, String cmd, boolean printInput)
	{
		if(cmd.length() != 0)
			HistoryModel.getModel(getShellHistory(shell)).addItem(cmd);
		text.setHistoryIndex(-1);

		if(cmd.startsWith(":"))
		{
			Shell _shell = Shell.getShell(cmd.substring(1));
			if(_shell != null)
			{
				text.setInput(null);
				setShell(_shell);
				return;
			}
		}

		setShell(shell);

		if(output == null)
			output = getOutput();
		if(error == null)
			error = getOutput(); 

		this.text.setCaretPosition(this.text.getDocument().getLength());

		ShellState state = getShellState(shell);
		state.commandRunning = true;

		updateAnimation();


		Macros.Recorder recorder = view.getMacroRecorder();
		if(recorder != null)
		{
			if(output instanceof BufferOutput)
			{
				recorder.record("runCommandToBuffer(view,\""
					+ shell.getName()
					+ "\",\""
					+ MiscUtilities.charsToEscapes(cmd)
					+ "\");");
			}
			else
			{
				recorder.record("runCommandInConsole(view,\""
					+ shell.getName()
					+ "\",\""
					+ MiscUtilities.charsToEscapes(cmd)
					+ "\");");
			}
		}

		if(printInput)
			output.print(infoColor,cmd);
		else
			output.print(null,"");

		errorSource.clear();
		ErrorSource.unregisterErrorSource(errorSource);
		try
		{
			shell.execute(this, input, output, null, cmd);
			startAnimation();
//			shell.execute(this,input,output,error,cmd);
		}
		catch(RuntimeException e)
		{
			print(getErrorColor(),e.toString());
			Log.log(Log.ERROR,this,e);
			output.commandDone();
//			error.commandDone();
		}
	} //}}}

	//{{{ initGUI() method
	private void initGUI()
	{
		Box box = new Box(BoxLayout.X_AXIS);

		shellCombo = new JComboBox();
		updateShellList();
		shellCombo.addActionListener(new ActionHandler());
		shellCombo.setRequestFocusEnabled(false);

		box.add(shellCombo);
		box.add(Box.createGlue());
		
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
		animationLabel.setVisible(false);
		animation.stop();
		box.add(animationLabel); 

		Image configimage = toolkit.getImage(Console.class.getResource("/console/Configure.png"));
		configure = new RolloverButton(new ImageIcon(configimage));
		configure.setToolTipText(jEdit.getProperty("plugin-manager.plugin-options"));
		configure.addActionListener(new ActionHandler());
		configure.setRequestFocusEnabled(false);
		box.add(configure);

		
		box.add(runAgain = new RolloverButton(RUN_AGAIN));
		runAgain.setToolTipText(jEdit.getProperty("run-last-console-command.label"));
		Insets margin = new Insets(0,0,0,0);
		runAgain.setMargin(margin);
		runAgain.addActionListener(new ActionHandler());
		runAgain.setRequestFocusEnabled(false);

		box.add(run = new RolloverButton(RUN));
		run.setToolTipText(jEdit.getProperty("console.run"));
		margin = new Insets(0,0,0,0);
		run.setMargin(margin);
		run.addActionListener(new RunActionHandler());
		run.setRequestFocusEnabled(false);

		box.add(toBuffer = new RolloverButton(TO_BUFFER));
		toBuffer.setToolTipText(jEdit.getProperty("console.to-buffer"));
		toBuffer.setMargin(margin);
		toBuffer.addActionListener(new RunActionHandler());
		toBuffer.setRequestFocusEnabled(false);

		box.add(stop = new RolloverButton(STOP));
		stop.setToolTipText(jEdit.getProperty("console.stop"));
		stop.setMargin(margin);
		stop.addActionListener(new ActionHandler());
		stop.setRequestFocusEnabled(false);

		box.add(clear = new RolloverButton(CLEAR));
		clear.setToolTipText(jEdit.getProperty("console.clear"));
		clear.setMargin(margin);
		clear.addActionListener(new ActionHandler());
		clear.setRequestFocusEnabled(false);

		
		add(BorderLayout.NORTH,box);

		text = new ConsolePane();
		InputMap inputMap = text.getInputMap();
		
		/* Press tab to complete input */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0),
			new CompletionAction());
		
		/* Press C+d to send EOF */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,
			InputEvent.CTRL_MASK),
			new EOFAction());
		
			
		
		/* Press C+z to detach process */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
			InputEvent.CTRL_MASK),
			new DetachAction());

		text.addActionListener(new RunActionHandler());
		JScrollPane scroller = new JScrollPane(text);
		scroller.setPreferredSize(new Dimension(400,100));
		add(BorderLayout.CENTER,scroller);
	} //}}}

	//{{{ updateShellList() method
	private void updateShellList()
	{
		String[] shells = Shell.getShellNames();
		Arrays.sort(shells,new MiscUtilities.StringICaseCompare());
		shellCombo.setModel(new DefaultComboBoxModel(shells));
		shellCombo.setMaximumSize(shellCombo.getPreferredSize());
	} //}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		text.setBackground(jEdit.getColorProperty("console.bgColor"));
		text.setForeground(jEdit.getColorProperty("console.plainColor"));
		text.setCaretColor(jEdit.getColorProperty("console.caretColor"));
		text.setFont(jEdit.getFontProperty("console.font"));

		infoColor = jEdit.getColorProperty("console.infoColor");
		warningColor = jEdit.getColorProperty("console.warningColor");
		errorColor = jEdit.getColorProperty("console.errorColor");
		plainColor = jEdit.getColorProperty("console.plainColor");		
	} //}}}

	//{{{ handlePluginUpdate() method
	public void handlePluginUpdate(PluginUpdate pmsg)
	{
		if(pmsg.getWhat() == PluginUpdate.LOADED
			|| pmsg.getWhat() == PluginUpdate.UNLOADED)
		{
			boolean resetShell = false;

			updateShellList();

			Iterator iter = shellStateMap.keySet().iterator();
			while(iter.hasNext())
			{
				String name = (String)iter.next();
				if(Shell.getShell(name) == null)
				{
					if(this.currentShell.getName().equals(name))
						resetShell = true;
					iter.remove();
				}
			}

			if(resetShell)
				setShell((String)shellStateMap.keySet().iterator().next());
			else
				shellCombo.setSelectedItem(currentShell.getName());
		}
	} //}}}

	//{{{ updateAnimation() method
	public void updateAnimation()
	{

		if(shellState.commandRunning) 
		{
			animationLabel.setVisible(true);
			animation.start();
		}
		else
		{
			animationLabel.setVisible(false);
			animation.stop();
		}
	} //}}}

	//{{{ complete() method
	/**
	 * TODO: update this so it uses the current APIs.
	 */
	private void complete()
	{
		String input = text.getInput();
		int cmdStart = text.getInputStart();
		int caret = text.getCaretPosition();
		int offset = caret - cmdStart;
		Shell.CompletionInfo info = currentShell.getCompletions(this,
			input.substring(0,offset));

		if(info == null || info.completions.length == 0)
			getToolkit().beep();
		else if(info.completions.length == 1)
		{
			text.select(cmdStart + info.offset,caret);
			text.replaceSelection(info.completions[0]);
		}
		else //if(info.completions.length > 1)
		{
			// Find a partial completion
			String longestCommonStart = MiscUtilities
				.getLongestPrefix(info.completions,
				ProcessRunner.getProcessRunner()
				.isCaseSensitive());

			if(longestCommonStart.length() != 0)
			{
				if(offset - info.offset
					!= longestCommonStart.length())
				{
					text.select(cmdStart + info.offset,caret);
					text.replaceSelection(longestCommonStart);
					return;
				}
			}

			getOutput().print(null,"");

			getOutput().print(getInfoColor(), jEdit.getProperty(
				"console.completions"));

			Arrays.sort(info.completions,new MiscUtilities
				.StringICaseCompare());

			for(int i = 0; i < info.completions.length; i++)
				print(null,info.completions[i]);

			getOutput().print(getInfoColor(),jEdit.getProperty(
				"console.completions-end"));

			currentShell.printPrompt(this,shellState);
			cmdStart = text.getDocument().getLength();
			getOutput().writeAttrs(null,input);
			text.setInputStart(cmdStart);
			text.setCaretPosition(cmdStart + offset);
		}
	} //}}}

	//{{{ getShellHistory() method
	private String getShellHistory(Shell shell)
	{
		return "console." + shell.getName();
	} //}}}

	//}}}

	//{{{ ShellState class
	
	/**
	 * 
	 * A ShellState brings together a Shell and its Output. 
	 * It holds the document which is the "scrollback buffer".
	 */
	public class ShellState implements Output
	{ 
		Shell shell;
		Document scrollback;
		private boolean commandRunning;

		public ShellState(Shell shell)
		{
			this.shell = shell;
			commandRunning = false;
			scrollback = new DefaultStyledDocument();
			// ick! talk about tightly coupling two classes. 
			shell.openConsole(Console.this);
		}

		//{{{ getInputStart() method
		public int getInputStart()
		{
			return ((Integer)scrollback.getProperty(
				ConsolePane.InputStart)).intValue();
		} //}}}

		//{{{ setInputStart() method
		public void setInputStart(int cmdStart)
		{
			scrollback.putProperty(ConsolePane.InputStart,
				new Integer(cmdStart));
		} //}}}

		//{{{ print() method
		public void print(Color color, String msg)
		{
			writeAttrs(ConsolePane.colorAttributes(color),
				msg + "\n");
		} //}}}

		//{{{ writeAttrs() method
		public void writeAttrs(final AttributeSet attrs,
			final String msg)
		{
			if(SwingUtilities.isEventDispatchThread())
				writeSafely(attrs,msg);
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						writeSafely(attrs,msg);
					}
				});
			}
		} //}}}

		//{{{ commandDone() method
		public void commandDone()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					// WTF?
					if(commandRunning)
						shell.printPrompt(Console.this, ShellState.this);
					commandRunning = false;
//					updateAnimation();
					stopAnimation();
					if(errorSource.getErrorCount() != 0)
						ErrorSource.registerErrorSource(errorSource);
				}
			});
		} //}}}

		//{{{ writeSafely() method
		private void writeSafely(AttributeSet attrs, String msg)
		{
			try
			{
				if(attrs != null && StyleConstants.getIcon(attrs) != null)
					msg = " ";
				scrollback.insertString(scrollback.getLength(),
					msg,attrs);
			}
			catch(BadLocationException bl)
			{
				Log.log(Log.ERROR,this,bl);
			}

			setInputStart(scrollback.getLength());
		} //}}}

		
		
	} //}}}

	//{{{ EvalAction class
	public static class EvalAction extends AbstractAction
	{
		private String command;
		
		public EvalAction(String label, String command)
		{
			super(label);
			this.command = command;
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			Console console = (Console)GUIUtilities.getComponentParent(
				(Component)evt.getSource(),Console.class);
			console.run(console.getShell(),null, console.getOutput(), null, command);
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if (source == configure) {
				EditAction ea = jEdit.getAction("plugin-options");
				ea.invoke(jEdit.getActiveView());
			}
			if(source == shellCombo)
				setShell((String)shellCombo.getSelectedItem());
			else if(source == runAgain)
				runLastCommand();
			else if(source == stop)
				getShell().stop(Console.this);
			else if(source == clear)
			{
				clear();
				getShell().printPrompt(Console.this,shellState);
			}
		}
	} //}}}

	//{{{ RunActionHandler class
	class RunActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			String cmd = text.getInput();
			Object source = evt.getSource();
			String input = null;
			Output output = shellState;
			boolean printInput = false;

			if(source == run)
				printInput = true;
			else if(source == toBuffer)
			{
				input = view.getTextArea().getSelectedText();
				output = new BufferOutput(Console.this);
			}

			run(getShell(),input,output,shellState,cmd,printInput);
		}
	} //}}}

	//{{{ CompletionAction class
	class CompletionAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			complete();
		}
	} //}}}


	//{{{ EOFAction class
	class EOFAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			currentShell.endOfFile(Console.this);
		}
	} //}}}

	//{{{ DetachAction class
	class DetachAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			currentShell.detach(Console.this);
		}
	} //}}}


	
}
