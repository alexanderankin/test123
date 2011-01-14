/*
 * Console.java - The console window
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2005 Slava Pestov
 * parts Copyright (C) 2006 Alan Ezust
 * parts Copyright (C) 2010 Eric Le Lay
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
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.msg.VFSPathSelected;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;

import console.SystemShell.ConsoleState;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import org.gjt.sp.util.StandardUtilities;
//}}}

// {{{ class Console
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

	private RolloverButton runAgain, run, toBuffer, stop, clear;
	private JLabel animationLabel;
	private AnimatedIcon animation;
	private ConsolePane text;
	private Color infoColor, warningColor, errorColor, plainColor;
	private DefaultErrorSource errorSource;
	private ProjectTreeListener listener;
	// }}}
	// }}}

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
		load();
		
		
	} //}}}

	// {{{ methods

	//{{{ focusOnDefaultComponent() method
	public void focusOnDefaultComponent()
	{
		text.requestFocus();
	} //}}}

	//{{{ load() method
	public void load()
	{
		EditBus.addToBus(this);
		addProjectListener();
		errorSource = new DefaultErrorSource("error parsing");
	} //}}}
	
	void addProjectListener()
	{
		if (listener != null) return;
		listener = new ProjectTreeListener(this);
	}
	
	//{{{ unload() method
	public void unload()
	{
		EditBus.removeFromBus(this);
		if (listener != null) {
			EditBus.removeFromBus(listener);
			listener.finalize();
		}
		ErrorSource.unregisterErrorSource(errorSource);
		Iterator<ShellState> iter = shellStateMap.values().iterator();
		while(iter.hasNext())
		{
			ShellState state = iter.next();
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
	public Shell setShell(String shellStr)
	{
		Shell shell = Shell.getShell(shellStr);
		if (shell == null) throw new RuntimeException("Unknown Shell: " + shellStr);
		return setShell(shell);
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
		String shellHistory = getShellHistory(shell);
		String limit = jEdit.getProperty("console.historyLimit");
		if(limit != null)
		{
			try
			{
				HistoryModel.getModel(shellHistory).setSize(
					Integer.parseInt(limit));
			}
			catch(NumberFormatException nfe)
			{
				jEdit.unsetProperty("console.historyLimit");
			}
		}
		text.setHistoryModel(shellHistory);

		shellState = shellStateMap.get(name);
		if(shellState == null)
		{
			shellState = new ShellState(shell);
			shellStateMap.put(shell.getName(),shellState);
			shell.printInfoMessage(shellState);
			shell.printPrompt(this,shellState);
		}

		text.setDocument(shellState.scrollback);
		if (shell != this.currentShell) {
			shellCombo.setSelectedItem(name);
		}
		this.currentShell = shell;
		scrollToBottom();
		return shell;
	} //}}}

	public void scrollToBottom() {
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				text.setCaretPosition(text.getDocument().getLength());
				text.scrollRectToVisible(text.getVisibleRect());
				updateAnimation();
			}
		});
	}
	
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

	//{{{ getOutput() methods
	/**
	 * Returns the Output corresponding to a particular Shell, without changing 
	 * the selected Shell.
	 */
	public Output getOutput(String shellName) {
		ShellState retval = shellStateMap.get(shellName);
		if (retval == null) {
			Shell s = Shell.getShell(shellName);
			retval = new ShellState(s);
			shellStateMap.put(shellName, retval);
		}
		return retval;
	}
	/**
	 * Returns the output instance for the currently selected Shell.
	 * @since Console 3.6
	 */
	public Output getOutput()
	{
		return shellState;
	} //}}}

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
		if(msg instanceof PropertiesChanged) propertiesChanged();
		else if (msg instanceof DockableWindowUpdate) {
			DockableWindowUpdate dwu = (DockableWindowUpdate) msg;
			if (dwu.getWhat() != null &&  dwu.getWhat().equals(DockableWindowUpdate.ACTIVATED))
				if (dwu.getDockable().equals("console")) 
					scrollToBottom();
		}
		else if(msg instanceof PluginUpdate)
			handlePluginUpdate((PluginUpdate)msg);
		else if (msg instanceof VFSPathSelected)
			handleNodeSelected((VFSPathSelected)msg);
		else if (msg instanceof ViewUpdate)
			handleViewUpdate((ViewUpdate)msg);
		else if (listener != null) {
			listener.handleMessage(msg);
		}
	} // }}}

	//{{{ handleViewUpdate() method
	private void handleViewUpdate(ViewUpdate vu) {
		if (vu.getWhat() == ViewUpdate.CLOSED && vu.getView() == view)
			unload();
	}
	//}}}
		
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

	// {{{ getPlainColor() method
	public Color getPlainColor() {
		return plainColor;
	} // }}}

	// {{{ getId() method
	/** @return a unique identifier starting at 0, representing which instance of Console this is,
	    or -1 if that value can not be determined. 
	*/
	public int getId() {
		int retval = 0;
		View v = jEdit.getFirstView();
		while (v != null) {
			/* In fact, this is not unique: there can be more than one Console per View.
			 * A better way of enumerating instances of Console for new floating
			 * instances is desired. */
			if (v == view) return retval;
			++retval;
			v = v.getNext();
		}
		return -1;
	} // }}}
	
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
	 * @returns the Output of a Shell, assuming the Shell was already created....
	 * 
	 * @since Console 4.0.2.
	 */
	public ShellState getShellState(Shell shell)
	{
		return shellStateMap.get(shell.getName());
	} //}}}

	// {{{ stopAnimation() method
	public void stopAnimation() {
		shellState.commandRunning=false;
		animation.stop();
	} // }}}

	// {{{ startAnimation method
	public void startAnimation() {
		currentShell = getShell();
		shellState = getShellState(currentShell);
		shellState.commandRunning = true;
		animationLabel.setVisible(true);
		animation.start();
	} // }}}

	//{{{ run() methods
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
	}


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
					+ StandardUtilities.charsToEscapes(cmd)
					+ "\");");
			}
			else
			{
				recorder.record("runCommandInConsole(view,\""
					+ shell.getName()
					+ "\",\""
					+ StandardUtilities.charsToEscapes(cmd)
					+ "\");");
			}
		}

		if(printInput)
			error.print(infoColor, cmd);
		else
			error.print(null,"");

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

		initAnimation();
		
		animationLabel.setIcon(animation);
		animationLabel.setVisible(false);
		animation.stop();
		box.add(animationLabel);


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

		/* Press ctrl-enter to run command to buffer */
		KeyStroke ctrlEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK);
		inputMap.put(ctrlEnter, new RunToBuffer());

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
	
	//{{{ initAnimation() method
	private void initAnimation()
	{
		// TODO: First frame of animation icon should be visible at gui init
		
		Toolkit toolkit = getToolkit();
		Image processImg = toolkit.getImage(Console.class.getResource("/console/process-working.png"));
		Image standbyImg = null;
		
		int iconSize = 22;
		
		ArrayList<Image> frames = new ArrayList<Image>();
		
		// Wait for the image to load by setting up an icon and discarding it again
		new ImageIcon(processImg).getImage();
		int procImgWidth = processImg.getWidth(null);
		int procImgHeight = processImg.getHeight(null);
		
		int currentX = 0, currentY = 0;
		int frameNo = 0;
		while(currentY < procImgHeight)
		{
			BufferedImage bufImg = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bufGraphics = bufImg.createGraphics();
			bufGraphics.drawImage(processImg,
				0,
				0,
				iconSize-1,
				iconSize-1,
				currentX,
				currentY,
				currentX + iconSize - 1,
				currentY + iconSize - 1,
				null);
			
			// First frame is the standby icon
			if(frameNo == 0)
				standbyImg = bufImg;
			else
				frames.add(bufImg);
			
			frameNo++;
			currentX += iconSize;
			if(currentX + iconSize > procImgWidth)
			{
				currentX = 0;
				currentY += iconSize;
			}
		}
		
		animation = new AnimatedIcon(
			standbyImg,
			frames.toArray(new Image[0]),
			25,
			animationLabel
		);
	}
	//}}}

	//{{{ updateShellList() method
	private void updateShellList()
	{
		String[] shells = Shell.getShellNames();
		Arrays.sort(shells,new StandardUtilities.StringCompare<String>(true));
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

	// {{{ handleNodeSelected()
	public void handleNodeSelected(VFSPathSelected msg) {
//		Log.log(Log.WARNING, this, "VFSPathSelected: " + msg.getPath());
		if (view != msg.getView()) return;
		if (!isVisible()) return;
		if (!jEdit.getBooleanProperty("console.changedir.nodeselect")) return;
		String path = msg.getPath();
		File f = new File(path);
		if (!f.isDirectory()) 
		{
			path = f.getParent();
			f = new File(path);
			if (!f.isDirectory()) return;
		}
		Shell sysShell = Shell.getShell("System");
		SystemShell ss = (SystemShell) sysShell;
		ConsoleState cs = ss.getConsoleState(this);
		if (cs.currentDirectory.equals(path)) return;
		Output output = getShellState(sysShell);
		String cmd = "cd \"" + path + "\"";
		sysShell.execute(this, cmd, output);
		output.print(getPlainColor(), "\n");
		sysShell.printPrompt(this, output);		
	} //}}}
	
	//{{{ handlePluginUpdate() method
	public void handlePluginUpdate(PluginUpdate pmsg)
	{
		if(pmsg.getWhat() == PluginUpdate.LOADED
			|| pmsg.getWhat() == PluginUpdate.UNLOADED)
		{
			boolean resetShell = false;

			updateShellList();

			Iterator<String> iter = shellStateMap.keySet().iterator();
			while(iter.hasNext())
			{
				String name = iter.next();
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

			Arrays.sort(info.completions,new StandardUtilities.StringCompare<String>(true));

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
	// }}}

	// {{{ Inner classes
	// {{{ ShellState class

	/**
	 * Each Shell of a Console has its own ShellState
	 * A ShellState is a writable Output. 
	 * It holds the document which is the "scrollback buffer".
	 */
	public class ShellState implements Output
	{
		Shell shell;
		Document scrollback;
		private boolean commandRunning;

		//{{{ getDocument() method
		public Document getDocument()
		{
			return scrollback;
		} //}}}

		public ShellState(Shell shell)
		{
			this.shell = shell;
			commandRunning = false;
			scrollback = new DefaultStyledDocument();
			((DefaultStyledDocument)scrollback).setDocumentFilter(new LengthFilter());
			
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
				Integer.valueOf(cmdStart));
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

		//{{{ setAttrs() method
		public void setAttrs(final int length, final AttributeSet attrs)
		{
			if(SwingUtilities.isEventDispatchThread())
			setSafely(scrollback.getLength() - length, length, attrs);
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						setSafely(scrollback.getLength() - length, length, attrs);
					//writeSafely(null, " - " + (scrollback.getLength() - length) + ":" + length);
					}
				});
			}
		} //}}}

		//{{{ setSafely() method
		private void setSafely(int start, int length, AttributeSet attrs)
		{
			((DefaultStyledDocument)scrollback).setCharacterAttributes(start, length,
				attrs, true);
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
			if(attrs != null && StyleConstants.getIcon(attrs) != null)
				msg = " ";
			try
			{
				// Rather than just print the output, this code searches through it line-by-line
				// looking for a file-reference match. If it finds one, then it creates a new
				// AttributeSet with underline for embedding a link to that file in the output

				// This code only supports java exceptions; it should be extended to support other
				// programs as well
				String[] lines = msg.split("\n");
				for (int i = 0; i<lines.length; i++) {
					String token = lines[i];
					// This regular expression tests for a java exception
					if (token.matches("^\\s*?at .*?\\(.*?\\.java:\\d*?\\)$")) {
						// Create new attribute set with a click action
						MutableAttributeSet linkAttrs = new SimpleAttributeSet(attrs);
						StyleConstants.setUnderline(linkAttrs, true);
						linkAttrs.addAttribute(text.Actions, new Object[] { new JavaExceptionFileReference(token) });

						// This is probably doing it the hard way, but all this does is limit the link
						// to inside the parentheses; this makes the result cleaner
						int pre = token.indexOf("(")+1;
						int post = token.lastIndexOf(")");
						scrollback.insertString(scrollback.getLength(),
								token.substring(0, pre), attrs);
						scrollback.insertString(scrollback.getLength(),
								token.substring(pre, post), linkAttrs);
						scrollback.insertString(scrollback.getLength(),
								token.substring(post), attrs);
					} else {
						scrollback.insertString(scrollback.getLength(), token, attrs);
					}
					// A workaround to make sure that newlines get printed
					if (i<(lines.length-1)) {
						scrollback.insertString(scrollback.getLength(), "\n", attrs);
					}
				}
				if (msg.endsWith("\n"))
					scrollback.insertString(scrollback.getLength(), "\n", attrs);

				//scrollback.insertString(scrollback.getLength(),
				//	msg,attrs);
			}
			catch(BadLocationException bl)
			{
				Log.log(Log.ERROR,this,bl);
			}

			setInputStart(scrollback.getLength());
		} //}}}
	} //}}}

		//{{{ JavaExceptionFileReference class
		/**
		 * Experimental support for adding hyperlinks to files from java exceptions.
		 * This class should probably go somewhere else
		 */
		private class JavaExceptionFileReference extends AbstractAction {

			private String lineText;

			public JavaExceptionFileReference(String lineText) {
				super("Go to location");
				// Trim off whitespace and the leading "at "
				this.lineText = lineText.trim().substring(3);
			}

			public void actionPerformed(ActionEvent e) {
				// This is also probably pulling this information out the hard way,
				// but it simply pulls out the class name (ignoring the method name) and 
				// line number.

				// The file is located by converting the class name to a
				// relative file path, and checking to see if that file exists relative
				// to the current working directory.
				String clazz = lineText.substring(0, lineText.lastIndexOf(".",
							lineText.indexOf("(")));
				Integer line = Integer.valueOf(lineText.substring(lineText.lastIndexOf(":")+1, lineText.lastIndexOf(")")));
				String file = clazz.replace(".", File.separator)+".java";
		
				// TODO: Support searching through the classpath
				// Also add support for opening files in jar archives w/ Archive plugin
				SystemShell system = (SystemShell) Shell.getShell("System");
				String workingDirectory = system.getConsoleState(Console.this).currentDirectory;
				File f = new File(workingDirectory, file);
				if (f.exists()) {
					// Open the file, then jump to the line
					Buffer buffer = jEdit.openFile(getView(), f.getPath());
					VFSManager.waitForRequests();
					// ???: Just go to the line, or select it?
					getView().getTextArea().setCaretPosition(buffer.getLineStartOffset(line-1));
					getView().getTextArea().requestFocus();
				}
			}
		}
		//}}}

	
	// {{{ LengthFilter class
	static private class LengthFilter extends DocumentFilter
	{
		public LengthFilter()
		{
			super();
		}
	
		//{{{ insertString() method
		public void insertString(DocumentFilter.FilterBypass fb, int offset,
			String str, AttributeSet attr) throws BadLocationException
		{
			replace(fb, offset, 0, str, attr);
		} //}}}
	
		//{{{ replace() method
		public void replace(DocumentFilter.FilterBypass fb, int offset,
			int length, String str, AttributeSet attrs)
				throws BadLocationException
		{
			int newLength = fb.getDocument().getLength() -
				length + str.length();
			fb.replace(offset, length, str, attrs);
			int limit = jEdit.getIntegerProperty("console.outputLimit", DEFAULT_LIMIT);
			if(newLength > limit)
				fb.remove(0, newLength - limit - 1);
		} //}}}
	
		// Not so large default limit to avoid performance down
		// with large output.
		// This will be sufficient to first use.
		private final int DEFAULT_LIMIT = 80/*column*/ * 1000/*lines*/;
	} //}}}

	// {{{ EvalAction class
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

	// {{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

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

	// {{{ RunActionHandler class
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

			run(getShell(), input, output, shellState, cmd, printInput);
		}
	} //}}}

	// {{{ runToBuffer class
	class RunToBuffer extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt) {
			String cmd = text.getInput();
			Output output = new BufferOutput(Console.this);
			run(getShell(), null, output, shellState, cmd, false);
		}

	}
	// }}}

	// {{{ CompletionAction class
	class CompletionAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			complete();
		}
	} //}}}

	// {{{ EOFAction class
	class EOFAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			currentShell.endOfFile(Console.this);
		}
	} //}}}

	// {{{ DetachAction class
	class DetachAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent evt)
		{
			currentShell.detach(Console.this);
		}
	} //}}}
	// }}}
	private static final long serialVersionUID = -9185531673809120587L;
} // }}}
