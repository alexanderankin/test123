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
import bsh.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
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
		output.print(null,jEdit.getProperty("console.beanshell.info"));
	} //}}}

	//{{{ execute() method
	public void execute(Console console, String input, Output output,
		Output error, String command)
	{
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

			ns.setVariable("console",null);
			ns.setVariable("output",null);
		}
		catch(Exception e)
		{
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			error.print(console.getErrorColor(),s.toString());
		}

		output.commandDone();
		error.commandDone();
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
