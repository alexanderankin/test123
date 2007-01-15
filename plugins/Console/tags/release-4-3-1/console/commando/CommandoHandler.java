/*
 * CommandoHandler.java - Reads commando XML file
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2005 Slava Pestov
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
import java.io.StringReader;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import bsh.NameSpace;

import com.microstar.xml.HandlerBase;
//}}}

public class CommandoHandler extends HandlerBase
{
	//{{{ CommandoHandler constructor
	CommandoHandler(View view, CommandoCommand command,
		CommandoDialog.SettingsPane settings,
		NameSpace nameSpace,
		List components,
		List scripts)
	{
		this.view = view;
		this.command = command;
		this.settings = pane = settings;
		this.nameSpace = nameSpace;
		this.components = components;
		this.scripts = scripts;

		stateStack = new Stack();
		options = new Vector();
	} //}}}

	//{{{ resolveEntity() method
	public Object resolveEntity(String publicId, String systemId)
	{
		if("commando.dtd".equals(systemId))
		{
			return new StringReader("<!-- -->");
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
			pane = new CommandoDialog.SettingsPane();
			pane.setBorder(new TitledBorder(label));
			settings.addComponent(pane);
			label = null;
		}
		else if(tag == "CHOICE")
		{
			choiceLabel = label;
			options = new Vector();
		}
	} //}}}


	public void endElement(String name)
	{
		if(name == null)
			return;

		String tag = peekElement();

		if(name.equals(tag))
		{
			if(tag == "OPTION")
			{
				options.addElement(new Option(label,optionValue));
				label = optionValue = null;
			}
			else if(tag == "CAPTION")
			{
				pane = settings;
			}
			else if(tag == "COMMANDS" || tag == "UI"
				|| tag == "COMMANDO")
			{
				// ignore these, they're syntax sugar
			}
			else if(tag == "COMMAND")
			{
				scripts.add(new Script(
					confirm,toBuffer,mode,
					shell,code));
				confirm = false;
				toBuffer = false;
				shell = code = null;
			}
			else
			{
				try
				{
					NameSpace tmp = new NameSpace(
						BeanShell.getNameSpace(),
						"commando");
					tmp.setVariable("pane",pane);
					tmp.setVariable("ns",nameSpace);
					if(tag == "CHOICE")
						tmp.setVariable("label",choiceLabel);
					else
						tmp.setVariable("label",label);
					tmp.setVariable("var",varName);
					tmp.setVariable("options",options);

					defaultValue = jEdit.getProperty(
						command.getPropertyPrefix()
						+ varName,defaultValue);

					if(eval != null)
					{
						defaultValue =
							String.valueOf(
							BeanShell.eval(
							view,tmp,eval));
					}
					if(defaultValue == null)
						nameSpace.setVariable(varName,"");
					else
						nameSpace.setVariable(varName,defaultValue);

					// this stores This instances
					// we call valueChanged() on
					// them to update namespace
					
					String script = "commando" + tag + "(view,pane,ns,label,var,options)";
					Object value = BeanShell.eval(view, tmp, script);
					components.add(value);
				}
				catch(Exception e)
				{
					Log.log(Log.ERROR,this,e);
				}

				label = varName = defaultValue = eval = null;
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
//		try
		{
			pushElement(null);
		}
/*		catch (Exception e)
		{
			e.printStackTrace();
		} */
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private CommandoCommand command;
	private CommandoDialog.SettingsPane settings;
	private CommandoDialog.SettingsPane pane;
	private NameSpace nameSpace;
	private List components;
	private List scripts;

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

	//{{{ Option class
	public static class Option
	{
		public String label;
		public String value;

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
}
