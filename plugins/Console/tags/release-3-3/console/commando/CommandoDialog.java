/*
 * CommandoDialog.java - Commando dialog box
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

package console.commando;

//{{{ Imports
import bsh.*;
import com.microstar.xml.*;
import console.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

public class CommandoDialog extends EnhancedDialog
{
	//{{{ CommandoDialog method
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
			settings = pane = new SettingsPane());
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
		show();
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		jEdit.setProperty("commando.last-command",command.getName());

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
	private SettingsPane pane;
	private TextAreaPane commandLine;
	private JButton ok;
	private JButton cancel;

	private CommandoCommand command;
	private NameSpace nameSpace;
	private Vector scripts;

	private boolean init;
	//}}}

	//{{{ load() method
	private void load(CommandoCommand command)
	{
		init = true;

		this.command = command;
		settings.removeAll();
		commandLine.setText(null);

		nameSpace = new NameSpace(BeanShell.getNameSpace(),
			"commando");
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

		getRootPane().revalidate();
		pack();

		init = false;

		tabs.setSelectedIndex(0);
	} //}}}

	//{{{ updateTextArea() method
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
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ Renderer class
	class Renderer extends DefaultListCellRenderer
	{
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

	//{{{ Script class
	class Script
	{
		boolean confirm;
		boolean toBuffer;
		String mode;
		String shell;
		String code;

		Script(boolean confirm, boolean toBuffer, String mode,
			String shell, String code)
		{
			this.confirm = confirm;
			this.toBuffer = toBuffer;
			this.mode = mode;
			this.shell = shell;
			this.code = code;
		}

		Command getCommand()
		{
			Object command = BeanShell.eval(view,nameSpace,code);
			if(command == null)
				return null;
			return new Command(confirm,toBuffer,mode,
				shell,String.valueOf(command));
		}
	} //}}}

	//{{{ Command class
	// static for use by CommandoThread
	static class Command
	{
		boolean confirm;
		boolean toBuffer;
		String mode;
		String shell;
		String command;

		Command(boolean confirm, boolean toBuffer, String mode,
			String shell, String command)
		{
			this.confirm = confirm;
			this.toBuffer = toBuffer;
			this.mode = mode;
			this.shell = shell;
			this.command = command;
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
				updateTextAreas();
		}
	} //}}}

	//{{{ CommandoHandler class
	class CommandoHandler extends HandlerBase
	{
		//{{{ CommandoHandler constructor
		CommandoHandler()
		{
			stateStack = new Stack();
			options = new Vector();
		} //}}}

		//{{{ resolveEntity() method
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
		} //}}}

		//{{{ attribute() method
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
			else if(aname == "TO_BUFFER")
				toBuffer = "TRUE".equals(value);
			else if(aname == "BUFFER_MODE")
				mode = value;
			else if(aname == "SHELL")
				shell = value;
		} //}}}

		//{{{ doctypeDecl() method
		public void doctypeDecl(String name, String publicId,
			String systemId) throws Exception
		{
			if("COMMANDO".equals(name))
				return;

			Log.log(Log.ERROR,this,command.getLabel()
				+ ".xml: DOCTYPE must be COMMANDO");
		} //}}}

		//{{{ charData() method
		public void charData(char[] c, int off, int len)
		{
			String tag = peekElement();
			String text = new String(c, off, len);

			if(tag == "COMMAND")
				code = text;
		} //}}}

		//{{{ startElement() method
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
		} //}}}

		//{{{ endElement() method
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
						confirm,toBuffer,mode,
						shell,code));
					confirm = false;
					toBuffer = false;
					shell = code = null;
				}

				popElement();
			}
			else
			{
				// can't happen
				throw new InternalError();
			}
		} //}}}

		//{{{ startDocument() method
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
		} //}}}

		//{{{ Private members

		//{{{ Instance variables
		private String varName;
		private String defaultValue;
		private String eval;
		private String optionValue;
		private String choiceLabel;
		private String label;
		private boolean confirm;
		private boolean toBuffer;
		private String mode;
		private String shell;
		private String code;

		private Vector options;

		private Stack stateStack;
		//}}}

		//{{{ pushElement() method
		private void pushElement(String name)
		{
			name = (name == null) ? null : name.intern();

			stateStack.push(name);
		} //}}}

		//{{{ peekElement() method
		private String peekElement()
		{
			return (String) stateStack.peek();
		} //}}}

		//{{{ popElement() method
		private String popElement()
		{
			return (String) stateStack.pop();
		} //}}}

		//}}}
	} //}}}

	//{{{ CommandoCheckBox class
	class CommandoCheckBox extends JCheckBox
	{
		//{{{ CommandoCheckBox constructor
		CommandoCheckBox(String label, String varName, String defaultValue,
			String eval)
		{
			super(label);

			this.varName = varName;
			this.property = command.getPropertyPrefix() + varName;

			setSelected("TRUE".equalsIgnoreCase(defaultValue));

			if(eval != null)
			{
				Object obj = BeanShell.eval(view,nameSpace,eval);
				if(Boolean.TRUE.equals(obj))
					setSelected(true);
				else
					setSelected(false);
			}
			else
			{
				if(jEdit.getProperty(property) != null)
				{
					if(jEdit.getBooleanProperty(property))
						setSelected(true);
					else
						setSelected(false);
				}
				else
					; // use default value
			}

			addActionListener(new ActionHandler());
			valueChanged();
		} //}}}

		private String varName;
		private String property;

		//{{{ valueChanged() method
		private void valueChanged()
		{
			jEdit.setTemporaryProperty(property,isSelected() ? "true" : "false");

			try
			{
				nameSpace.setVariable(varName,new Primitive(
					isSelected()));
			}
			catch(EvalError e)
			{
				// can't do much...
			}
		} //}}}

		//{{{ ActionHandler class
		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				valueChanged();
			}
		} //}}}
	} //}}}

	//{{{ CommandoTextField class
	class CommandoTextField extends JTextField
	{
		//{{{{ CommandoTextField constructor
		CommandoTextField(String varName, String defaultValue, String eval)
		{
			super("commando." + varName);

			setText(defaultValue);

			this.varName = varName;
			this.property = CommandoDialog.this.command.getPropertyPrefix() + varName;

			if(eval != null)
			{
				Object value = BeanShell.eval(view,nameSpace,eval);
				if(value != null)
					setText(value.toString());
			}
			else
			{
				String value = jEdit.getProperty(property);
				if(value != null)
					setText(value);
			}

			Dimension size = CommandoTextField.this.getPreferredSize();
			size.width = 200;
			setPreferredSize(size);

			addActionListener(new ActionHandler());
			CommandoTextField.this.addFocusListener(new FocusHandler());
			valueChanged();
		} //}}}

		private String varName;
		private String property;

		//{{{ valueChanged() method
		private void valueChanged()
		{
			String text = getText();
			if(text == null)
				text = "";

			jEdit.setTemporaryProperty(property,text);

			try
			{
				nameSpace.setVariable(varName,text);
			}
			catch(EvalError e)
			{
				// can't do much...
			}
		} //}}}

		//{{{ ActionHandler class
		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				valueChanged();
			}
		} //}}}

		//{{{ FocusHandler class
		class FocusHandler implements FocusListener
		{
			public void focusGained(FocusEvent evt) {}

			public void focusLost(FocusEvent evt)
			{
				valueChanged();
			}
		} //}}}
	} //}}}

	//{{{ CommandoCheckBox class
	class CommandoComboBox extends JComboBox
	{
		//{{{ CommandoComboBox constructor
		CommandoComboBox(String varName, String defaultValue, String eval,
			Vector options)
		{
			super(options);

			this.varName = varName;
			this.property = command.getPropertyPrefix() + varName;

			if(eval != null)
			{
				defaultValue = String.valueOf(BeanShell.eval(
					view,nameSpace,eval));
			}
			else
			{
				String value = jEdit.getProperty(property);
				if(value != null)
					defaultValue = value;
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
		} //}}}

		private String varName;
		private String property;
		private String eval;

		//{{{ valueChanged() method
		private void valueChanged()
		{
			Option value = (Option)getSelectedItem();

			jEdit.setTemporaryProperty(property,value.value);

			try
			{
				nameSpace.setVariable(varName,value.value);
			}
			catch(EvalError e)
			{
				// can't do much...
			}
		} //}}}

		//{{{ ActionHandler class
		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				valueChanged();
			}
		} //}}}
	} //}}}

	//{{{ Option class
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
	} //}}}

	//{{{ SettingsPane class
	static class SettingsPane extends JPanel
	{
		//{{{ SettingsPane constructor
		SettingsPane()
		{
			setLayout(gridBag = new GridBagLayout());
		} //}}}

		//{{{ addComponent() method
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
		} //}}}

		//{{{ addComponent() method
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
