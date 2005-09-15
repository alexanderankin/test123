/*
 * CommandoDialog.java - Commando dialog box
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
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

package console.commando;

//{{{ Imports
import bsh.*;
import com.microstar.xml.*;
import console.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

public class CommandoDialog extends EnhancedDialog
{
	private static final long serialVersionUID = -9156953920974073657L;
	//{{{ CommandoDialog constructor
	public CommandoDialog(View view, String command)
	{
		super(view,jEdit.getProperty("commando.title"),false);

		this.view = view;

		JPanel content = new JPanel(new BorderLayout(0,12));
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		JPanel top = new JPanel(new BorderLayout());

		JLabel label = new JLabel(jEdit.getProperty("commando.caption"));
		label.setBorder(new EmptyBorder(0,0,0,12));
		top.add(BorderLayout.WEST,label);

		content.add(BorderLayout.NORTH,top);

		EditAction[] commands = ConsolePlugin.getCommandoCommands();

		ActionHandler actionListener = new ActionHandler();

		commandCombo = new JComboBox(commands);
		commandCombo.setRenderer(new Renderer());
		commandCombo.addActionListener(actionListener);
		top.add(BorderLayout.CENTER,commandCombo);

		tabs = new JTabbedPane();
		tabs.addTab(jEdit.getProperty("commando.settings"),
			settings = new SettingsPane());
		tabs.addTab(jEdit.getProperty("commando.commands"),
			commandLine = new TextAreaPane());
		tabs.addChangeListener(new ChangeHandler());

		if(command == null)
			command = jEdit.getProperty("commando.last-command");

		for(int i = 0; i < commands.length; i++)
		{
			if(commands[i].getName().equals(command))
			{
				commandCombo.setSelectedIndex(i);
				break;
			}
		}

		load((CommandoCommand)commandCombo.getSelectedItem());

		content.add(BorderLayout.CENTER,tabs);

		Box buttons = new Box(BoxLayout.X_AXIS);
		buttons.add(Box.createGlue());

		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(actionListener);
		getRootPane().setDefaultButton(ok);
		buttons.add(ok);

		buttons.add(Box.createHorizontalStrut(6));

		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(actionListener);
		buttons.add(cancel);

		buttons.add(Box.createGlue());

		content.add(BorderLayout.SOUTH,buttons);

		pack();
		setLocationRelativeTo(view);
		setVisible(true);
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		updateNameSpace();

		save();

		Vector commands = new Vector();

		for(int i = 0; i < scripts.size(); i++)
		{
			CommandoHandler.Script script
				= (CommandoHandler.Script)
				scripts.get(i);
			CommandoHandler.Command command = script.getCommand();
			if(command == null)
			{
				// user has already seen the BeanShell error,
				// so just exit
				return;
			}

			commands.addElement(command);
		}

		// open a console
		DockableWindowManager wm = view.getDockableWindowManager();
		wm.addDockableWindow("console");

		CommandoThread thread = new CommandoThread(
			(Console)wm.getDockable("console"),
			commands);
		thread.start();

		dispose();
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		jEdit.setProperty("commando.last-command",command.getName());
		dispose();
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;

	private JComboBox commandCombo;
	private JTabbedPane tabs;
	private SettingsPane settings;
	private TextAreaPane commandLine;
	private JButton ok;
	private JButton cancel;

	private CommandoCommand command;
	private NameSpace nameSpace;
	private List scripts;
	private List components;

	private boolean init;
	//}}}

	// 
	void load(CommandoCommand command)
	{
		init = true;

		this.command = command;
		settings.removeAll();
		components = new ArrayList();
		commandLine.setText(null);

		nameSpace = new NameSpace(BeanShell.getNameSpace(),
			"commando");
		scripts = new ArrayList();

		XmlParser parser = new XmlParser();
		CommandoHandler handler = new CommandoHandler(view,command,
			settings,nameSpace,components,scripts);
		parser.setHandler(handler);
		Reader in = null;
		try
		{
			in = command.openStream();
			parser.parse(null, null, in);
		}
		catch(XmlException xe)
		{
			Log.log(Log.ERROR,this,xe);

			int line = xe.getLine();
			String message = xe.getMessage();

			Object[] pp = { command.getLabel() + ".xml", new Integer(line),
				message };
			GUIUtilities.error(null,"commando.xml-error",pp);
		}
		catch(IOException io)
		{
			Log.log(Log.ERROR,this,io);

			Object[] pp = { command.getLabel() + ".xml", io.toString() };
			GUIUtilities.error(null,"read-error",pp);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
		}
		
		finally
		{
			try
			{
				if(in != null)
					in.close();
			}
			catch(IOException io)
			{
				Log.log(Log.ERROR,this,io);
			}
		}

		getRootPane().revalidate();
		pack();

		init = false;

		tabs.setSelectedIndex(0);
	} //}}}

	//{{{ save() method
	private void save()
	{
		jEdit.setProperty("commando.last-command",command.getName());

		try
		{
			String[] names = nameSpace.getVariableNames();
			for(int i = 0; i < names.length; i++)
			{
				Object var = nameSpace.getVariable(names[i]);
				if(var == Primitive.VOID)
					continue;
				jEdit.setProperty(command.getPropertyPrefix() + names[i],
					String.valueOf(var));
			}
		}
		catch(UtilEvalError e)
		{
			Log.log(Log.ERROR,this,e);
		}
	} //}}}
	
	//{{{ updateNameSpace() method
	private void updateNameSpace()
	{
		for(int i = 0; i < components.size(); i++)
		{
			This t = (This)components.get(i);
			try
			{
				t.invokeMethod("valueChanged",new Object[0]);
			}
			catch(EvalError e)
			{
				Log.log(Log.ERROR,this,e);
			}
		}
	} //}}}

	//{{{ updateTextArea() method
	private void updateTextArea()
	{
		if(init)
			return;

		StringBuffer buf = new StringBuffer();

		for(int i = 0; i < scripts.size(); i++)
		{
			CommandoHandler.Script script
				= (CommandoHandler.Script)
				scripts.get(i);
			CommandoHandler.Command command = script.getCommand();
			if(command == null)
			{
				// user has already seen the BeanShell error,
				// so just exit
				return;
			}

			buf.append(command.shell);
			buf.append(": ");
			buf.append(command.command);
			buf.append('\n');
		}

		commandLine.setText(buf.toString());
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ Renderer class
	class Renderer extends DefaultListCellRenderer
	{
		private static final long serialVersionUID = 3950379651562103708L;

		public Component getListCellRendererComponent(
			JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list,value,index,
				isSelected,cellHasFocus);
			EditAction action = (EditAction)value;
			setText(action.getLabel());
			return this;
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == commandCombo)
			{
				CommandoCommand command = (CommandoCommand)commandCombo
					.getSelectedItem();
				load(command);
			}
			else if(evt.getSource() == ok)
				ok();
			else if(evt.getSource() == cancel)
				cancel();
		}
	} //}}}

	//{{{ ChangeHandler class
	class ChangeHandler implements ChangeListener
	{
		public void stateChanged(ChangeEvent evt)
		{
			if(tabs.getSelectedIndex() == 1)
			{
				updateNameSpace();
				updateTextArea();
			}
		}
	} //}}}

	//{{{ SettingsPane class
	public static class SettingsPane extends JPanel
	{
		//{{{ SettingsPane constructor
		SettingsPane()
		{
			setLayout(gridBag = new GridBagLayout());
		} //}}}

		//{{{ addComponent() method
		public void addComponent(String left, Component right)
		{
			JLabel label = new JLabel(left + ":");
			label.setBorder(new EmptyBorder(0,0,0,12));
			addComponent(label,right);
		} //}}}

		//{{{ addComponent() method
		public void addComponent(Component left, Component right)
		{
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridy = y++;
			cons.gridheight = 1;
			cons.gridwidth = 1;
			cons.weightx = 0.0f;
			cons.fill = GridBagConstraints.BOTH;
			gridBag.setConstraints(left,cons);
			add(left);

			cons.gridx = 1;
			cons.weightx = 1.0f;
			gridBag.setConstraints(right,cons);
			add(right);
		} //}}}

		//{{{ addComponent() method
		public void addComponent(Component comp)
		{
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridy = y++;
			cons.gridheight = 1;
			cons.gridwidth = cons.REMAINDER;
			cons.fill = GridBagConstraints.BOTH;
			cons.anchor = GridBagConstraints.WEST;
			cons.weightx = 1.0f;

			gridBag.setConstraints(comp,cons);
			add(comp);
		} //}}}

		private GridBagLayout gridBag;
		private int y;
	} //}}}

	//{{{ TextAreaPane class
	static class TextAreaPane extends JPanel
	{
		//{{{ TextAreaPane constructor
		TextAreaPane()
		{
			super(new BorderLayout());

			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(new EmptyBorder(6,0,6,0));
			panel.add(BorderLayout.WEST,copy = new JButton(
				jEdit.getProperty("commando.copy")));
			copy.addActionListener(new ActionHandler());

			add(BorderLayout.NORTH,panel);

			add(BorderLayout.CENTER,new JScrollPane(
				textArea = new JTextArea(4,30)));
			textArea.setEditable(false);
			textArea.setLineWrap(true);
		} //}}}

		//{{{ setText() method
		void setText(String text)
		{
			textArea.setText(text);
		} //}}}

		private JButton copy;
		private JTextArea textArea;

		//{{{ ActionHandler class
		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.copy();
			}
		} //}}}
	} //}}}

	//}}}
}
