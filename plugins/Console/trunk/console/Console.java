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
	public Console(View view)
	{
		super(new BorderLayout());

		this.view = view;

		shellCombo = new JComboBox(EditBus.getNamedList(Shell.SHELLS_LIST));
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
		Insets margin = new Insets(1,1,1,1);
		run.setMargin(margin);
		run.addActionListener(actionHandler);
		run.setRequestFocusEnabled(false);

		panel.add(BorderLayout.EAST,buttonBox);

		add(BorderLayout.NORTH,panel);

		output = new JTextPane();
		add(BorderLayout.CENTER,new JScrollPane(output));

		propertiesChanged();
		setShell(ConsolePlugin.CONSOLE_SHELL);
	}

	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
		ProcessManager.consoleOpened(view,this);
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
		ProcessManager.consoleClosed(view);
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
		Object[] shells = EditBus.getNamedList(Shell.SHELLS_LIST);
		for(int i = 0; i < shells.length; i++)
		{
			Shell sh = (Shell)shells[i];
			if(sh.getName().equals(shell))
			{
				setShell(sh);
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

		shell.execute(view,cmd,this);
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
	}

	public void printPlain(String msg)
	{
		addOutput(null,msg);
	}

	public void printInfo(String msg)
	{
		addOutput(infoColor,msg);
	}

	public void printWarning(String msg)
	{
		addOutput(warningColor,msg);
	}

	public void printError(String msg)
	{
		addOutput(errorColor,msg);
	}

	public void clear()
	{
		output.setText("");
	}

	// protected members
	protected View view;
	protected JComboBox shellCombo;
	protected HistoryTextField command;
	protected JButton run;
	protected JTextPane output;
	protected Shell shell;

	protected Color infoColor, warningColor, errorColor;

	protected void propertiesChanged()
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

		String family = jEdit.getProperty("console.font");
		int size;
		try
		{
			size = Integer.parseInt(jEdit.getProperty(
				"console.fontsize"));
		}
		catch(NumberFormatException nf)
		{
			size = 14;
		}
		int style;
		try
		{
			style = Integer.parseInt(jEdit.getProperty(
				"console.fontstyle"));
		}
		catch(NumberFormatException nf)
		{
			style = Font.PLAIN;
		}
		output.setFont(new Font(family,style,size));
	}

	protected synchronized void addOutput(Color color, String msg)
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
		}
	}
}
