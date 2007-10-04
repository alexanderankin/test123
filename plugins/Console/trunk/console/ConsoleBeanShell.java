/*
 * ConsoleBeanShell.java - Executes commands in jEdit's BeanShell interpreter
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
import org.gjt.sp.jedit.bsh.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
//}}}

public class ConsoleBeanShell extends Shell
{
	//{{{ ConsoleBeanShell constructor
	public ConsoleBeanShell()
	{
		super("BeanShell");
	} //}}}

	//{{{ printInfoMessage() method
	public void printInfoMessage(Output output)
	{
		if (jEdit.getBooleanProperty("console.shell.info.toggle"))
			output.print(null,jEdit.getProperty("console.beanshell.info"));
	} //}}}

	//{{{ printPrompt() method
	/**
	 * Prints a prompt to the specified console.
	 * @param output The output
	 */
	public void printPrompt(Console console, Output output)
	{
		output.writeAttrs(
			ConsolePane.colorAttributes(console.getInfoColor()),
			jEdit.getProperty("console.beanshell.prompt"));
		output.writeAttrs(null," ");
	} //}}}
	

	//{{{ execute() method
	public void execute(Console console, String input, Output output,
		Output error, String command)
	{
		if (error == null) error = output;
		View view = console.getView();

		NameSpace ns = org.gjt.sp.jedit.BeanShell.getNameSpace();
		try
		{
			ns.setVariable("console",console);
			ns.setVariable("output",output);
			Object retVal = BeanShell._eval(view,
				ns,command);

			if(retVal != null)
			{
				ns.setVariable("retVal",retVal);
				BeanShell._eval(view,ns,
					"print(retVal);");
				ns.setVariable("retVal",null);
			}
		}
		catch(Exception e)
		{
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			error.print(console.getErrorColor(),s.toString());
		}
		finally
		{
			try
			{
				ns.setVariable("console",null);
				ns.setVariable("output",null);
			}
			catch(UtilEvalError e)
			{
			}
		}
		if (error != output) error.commandDone();
		output.commandDone();

	} //}}}

	//{{{ stop() method
	public void stop(Console console)
	{
	} //}}}

	//{{{ waitFor() method
	public boolean waitFor(Console console)
	{
		return true;
	} //}}}

}
