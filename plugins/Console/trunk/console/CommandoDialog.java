/*
 * CommandoDialog.java - Commando dialog box
 * Copyright (C) 2001 Slava Pestov
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

import bsh.*;
import com.microstar.xml.*;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class CommandoDialog extends EnhancedDialog
{
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

		CommandoCommand[] commands = ConsolePlugin.getCommandoCommands();

		ActionHandler actionListener = new ActionHandler();

		commandCombo = new JComboBox(commands);
		commandCombo.addActionListener(actionListener);
		top.add(BorderLayout.CENTER,commandCombo);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(jEdit.getProperty("commando.settings"),
			settings = pane = new SettingsPane());
		tabs.addTab(jEdit.getProperty("commando.commands"),
			commandLine = new TextAreaPane());
		tabs.addTab(jEdit.getProperty("commando.properties"),
			properties = new TextAreaPane());

		if(command == null)
			command = jEdit.getProperty("commando.last-command");

		for(int i = 0; i < commands.length; i++)
		{
			if(commands[i].name.equals(command))
			{
				commandCombo.setSelectedIndex(i);
				break;
			}
		}

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
		show();
	}

	public void ok()
	{
		jEdit.setProperty("commando.last-command",command.name);

		Buffer buffer = view.getBuffer();

		Enumeration keys = propertyValues.keys();
		while(keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			buffer.putProperty(key,propertyValues.get(key));
		}

		Vector commands = new Vector();

		for(int i = 0; i < scripts.size(); i++)
		{
			Script script = (Script)scripts.elementAt(i);
			Command command = script.getCommand();
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
			(Console)wm.getDockableWindow("console"),
			commands);
		thread.start();

		dispose();
	}

	public void cancel()
	{
		jEdit.setProperty("commando.last-command",command.name);
		dispose();
	}

	// private members
	private View view;

	private JComboBox commandCombo;
	private SettingsPane settings;
	private SettingsPane pane;
	private TextAreaPane commandLine;
	private TextAreaPane properties;
	private JButton ok;
	private JButton cancel;

	private CommandoCommand command;
	private NameSpace nameSpace;
	private Hashtable propertyValues;
	private Vector scripts;

	private boolean init;

	private void load(CommandoCommand command)
	{
		init = true;

		this.command = command;
		settings.removeAll();
		commandLine.setText(null);
		properties.setText(null);

		nameSpace = new NameSpace(BeanShell.getNameSpace(),
			"commando");
		propertyValues = new Hashtable();
		scripts = new Vector();

		XmlParser parser = new XmlParser();
		CommandoHandler handler = new CommandoHandler();
		parser.setHandler(handler);
		try
		{
			parser.parse(null, null, command.openStream());
		}
		catch(XmlException xe)
		{
			Log.log(Log.ERROR,this,xe);

			int line = xe.getLine();
			String message = xe.getMessage();

			Object[] pp = { command.name + ".xml", new Integer(line),
				message };
			GUIUtilities.error(null,"commando.xml-error",pp);
		}
		catch(IOException io)
		{
			Log.log(Log.ERROR,this,io);

			Object[] pp = { command.name + ".xml", io.toString() };
			GUIUtilities.error(null,"read-error",pp);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
		}

		getRootPane().revalidate();
		pack();

		init = false;

		updateTextAreas();
	}

	private void updateTextAreas()
	{
		if(init)
			return;

		StringBuffer buf = new StringBuffer();

		for(int i = 0; i < scripts.size(); i++)
		{
			Script script = (Script)scripts.elementAt(i);
			Command command = script.getCommand();
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

		buf = new StringBuffer();

		Enumeration enum = propertyValues.keys();
		while(enum.hasMoreElements())
		{
			buf.append(':');
			Object key = enum.nextElement();
			buf.append(key);
			buf.append('=');
			buf.append(propertyValues.get(key));
			buf.append(":\n");
		}

		properties.setText(buf.toString());
	}

	class Script
	{
		boolean confirm;
		String shell;
		String code;

		Script(boolean confirm, String shell, String code)
		{
			this.confirm = confirm;
			this.shell = shell;
			this.code = code;
		}

		Command getCommand()
		{
			Object command = BeanShell.eval(view,nameSpace,
				code,false);
			if(command == null)
				return null;
			return new Command(confirm,shell,String.valueOf(command));
		}
	}

	// static for use by CommandoThread
	static class Command
	{
		boolean confirm;
		String shell;
		String command;

		Command(boolean confirm, String shell, String command)
		{
			this.confirm = confirm;
			this.shell = shell;
			this.command = command;
		}
	}

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
	}

	class CommandoHandler extends HandlerBase
	{
		CommandoHandler()
		{
			stateStack = new Stack();
			options = new Vector();
		}

		public Object resolveEntity(String publicId, String systemId)
		{
			if("commando.dtd".equals(systemId))
			{
				try
				{
					return new BufferedReader(new InputStreamReader(
						CommandoHandler.class.getResourceAsStream(
						"/console/commando/commando.dtd")));
				}
				catch(Exception e)
				{
					Log.log(Log.ERROR,this,"Error while opening"
						+ " commando.dtd:");
					Log.log(Log.ERROR,this,e);
				}
			}

			return null;
		}

		public void attribute(String aname, String value, boolean isSpecified)
		{
			aname = (aname == null) ? null : aname.intern();
			value = (value == null) ? null : value.intern();

			if(aname == "LABEL")
				label = value;
			else if(aname == "VARNAME")
				varName = value;
			else if(aname == "DEFAULT")
				defaultValue = value;
			else if(aname == "EVAL")
				eval = value;
			else if(aname == "VALUE")
				optionValue = value;
			else if(aname == "CONFIRM")
				confirm = "TRUE".equals(value);
			else if(aname == "SHELL")
				shell = value;
		}

		public void doctypeDecl(String name, String publicId,
			String systemId) throws Exception
		{
			if("COMMANDO".equals(name))
				return;

			Log.log(Log.ERROR,this,command.name + ".xml: DOCTYPE must be COMMANDO");
		}

		public void charData(char[] c, int off, int len)
		{
			String tag = peekElement();
			String text = new String(c, off, len);

			if(tag == "COMMAND")
				code = text;
		}

		public void startElement(String name)
		{
			pushElement(name);

			String tag = peekElement();
			if(tag == "CAPTION")
			{
				pane = new SettingsPane();
				pane.setBorder(new TitledBorder(label));
				settings.addComponent(pane);
				label = null;
			}
			else if(tag == "CHOICE")
			{
				choiceLabel = label;
			}
		}

		public void endElement(String name)
		{
			if(name == null)
				return;

			String tag = peekElement();

			if(name.equals(tag))
			{
				if(tag == "TOGGLE")
				{
					pane.addComponent(
						new CommandoCheckBox(
						label,varName,defaultValue,eval));
					label = varName = eval = null;
				}
				else if(tag == "ENTRY")
				{
					JLabel left = new JLabel(label);
					left.setBorder(new EmptyBorder(0,0,0,12));
					pane.addComponent(left,
						new CommandoTextField(
						varName,defaultValue,eval));
					label = varName = eval = null;
				}
				else if(tag == "TOGGLE_ENTRY")
				{
					// XXX
					label = varName = eval = null;
				}
				else if(tag == "CHOICE")
				{
					JLabel left = new JLabel(choiceLabel);
					left.setBorder(new EmptyBorder(0,0,0,12));
					pane.addComponent(left,
						new CommandoComboBox(
						varName,defaultValue,eval,options));
					options = new Vector();
					choiceLabel = varName = eval = null;
				}
				else if(tag == "OPTION")
				{
					options.addElement(new Option(label,optionValue));
					label = optionValue = null;
				}
				else if(tag == "CAPTION")
				{
					pane = settings;
				}
				else if(tag == "COMMAND")
				{
					scripts.addElement(new Script(
						confirm,shell,code));
				}

				popElement();
			}
			else
			{
				// can't happen
				throw new InternalError();
			}
		}

		public void startDocument()
		{
			try
			{
				pushElement(null);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// end HandlerBase implementation

		// private members
		private String varName;
		private String defaultValue;
		private String eval;
		private String optionValue;
		private String choiceLabel;
		private String label;
		private boolean confirm;
		private String shell;
		private String code;

		private Vector options;

		private Stack stateStack;

		private void pushElement(String name)
		{
			name = (name == null) ? null : name.intern();

			stateStack.push(name);
		}

		private String peekElement()
		{
			return (String) stateStack.peek();
		}

		private String popElement()
		{
			return (String) stateStack.pop();
		}
	}

	class CommandoCheckBox extends JCheckBox
	{
		CommandoCheckBox(String label, String varName, String defaultValue,
			String eval)
		{
			super(label);

			this.varName = varName;
			this.property = command.propertyPrefix + varName;

			setSelected("TRUE".equalsIgnoreCase(defaultValue));

			if(eval != null)
			{
				Object obj = BeanShell.eval(view,eval,false);
				if(Boolean.TRUE.equals(obj))
					setSelected(true);
				else
					setSelected(false);
			}
			else
			{
				Buffer buffer = view.getBuffer();
				if(buffer.getProperty(property) != null)
				{
					if(buffer.getBooleanProperty(property))
						setSelected(true);
					else
						setSelected(false);
				}
				else
					; // use default value
			}

			addActionListener(new ActionHandler());
			valueChanged();
		}

		// private members
		private String varName;
		private String property;

		private void valueChanged()
		{
			propertyValues.put(property,new Boolean(isSelected()));

			try
			{
				nameSpace.setVariable(varName,new Primitive(
					isSelected()));
			}
			catch(EvalError e)
			{
				// can't do much...
			}

			updateTextAreas();
		}

		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				valueChanged();
			}
		}
	}

	class CommandoTextField extends HistoryTextField
	{
		CommandoTextField(String varName, String defaultValue, String eval)
		{
			super("commando." + varName);

			setText(defaultValue);

			this.varName = varName;
			this.property = command.propertyPrefix + varName;

			if(eval != null)
			{
				Object value = BeanShell.eval(view,eval,false);
				if(value != null)
					setText(value.toString());
			}
			else
			{
				Buffer buffer = view.getBuffer();
				Object value = buffer.getProperty("commando."
					+ command.name + "." + varName);
				if(value != null)
					setText(value.toString());
			}

			Dimension size = CommandoTextField.this.getPreferredSize();
			size.width = 200;
			setPreferredSize(size);

			addActionListener(new ActionHandler());
			CommandoTextField.this.addFocusListener(new FocusHandler());
			valueChanged();
		}

		// private members
		private String varName;
		private String property;

		private void valueChanged()
		{
			String text = getText();
			if(text == null)
				text = "";

			propertyValues.put(property,text);

			try
			{
				nameSpace.setVariable(varName,text);
			}
			catch(EvalError e)
			{
				// can't do much...
			}

			updateTextAreas();
		}

		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				valueChanged();
			}
		}

		class FocusHandler implements FocusListener
		{
			public void focusGained(FocusEvent evt) {}

			public void focusLost(FocusEvent evt)
			{
				valueChanged();
			}
		}
	}

	class CommandoComboBox extends JComboBox
	{
		CommandoComboBox(String varName, String defaultValue, String eval,
			Vector options)
		{
			super(options);

			this.varName = varName;
			this.property = command.propertyPrefix + varName;

			if(eval != null)
			{
				defaultValue = String.valueOf(BeanShell.eval(
					view,eval,false));
			}
			else
			{
				Buffer buffer = view.getBuffer();
				Object value = buffer.getProperty("commando."
					+ command.name + "." + varName);
				if(value != null)
					defaultValue = String.valueOf(value);
			}

			if(defaultValue != null)
			{
				for(int i = 0; i < options.size(); i++)
				{
					Option opt = (Option)options.elementAt(i);
					if(defaultValue.equals(opt.value))
					{
						setSelectedIndex(i);
						break;
					}
				}
			}

			addActionListener(new ActionHandler());
			valueChanged();
		}

		// private members
		private String varName;
		private String property;
		private String eval;

		private void valueChanged()
		{
			Option value = (Option)getSelectedItem();

			propertyValues.put(property,value.value);

			try
			{
				nameSpace.setVariable(varName,value.value);
			}
			catch(EvalError e)
			{
				// can't do much...
			}

			updateTextAreas();
		}

		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				valueChanged();
			}
		}
	}

	static class Option
	{
		String label;
		String value;

		Option(String label, String value)
		{
			this.label = label;
			this.value = value;
		}

		public String toString()
		{
			return label;
		}
	}

	static class SettingsPane extends JPanel
	{
		SettingsPane()
		{
			setLayout(gridBag = new GridBagLayout());
		}

		void addComponent(Component left, Component right)
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
		}

		void addComponent(Component comp)
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
		}

		// private members
		private GridBagLayout gridBag;
		private int y;
	}

	static class TextAreaPane extends JPanel
	{
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
		}

		void setText(String text)
		{
			textArea.setText(text);
		}

		// private emembers
		private JButton copy;
		private JTextArea textArea;

		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.copy();
			}
		}
	}
}
