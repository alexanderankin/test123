/*
 * ConsoleBeanShell.java - Executes commands in jEdit's BeanShell interpreter
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

import bsh.EvalError;
import bsh.NameSpace;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

class ConsoleBeanShell extends Shell
{
	public ConsoleBeanShell()
	{
		super("BeanShell");
	}

	public void printInfoMessage(Output output)
	{
		output.print(null,jEdit.getProperty("console.beanshell.info"));
	}

	public void execute(Console console, Output output, String command)
	{
		View view = console.getView();

		NameSpace ns = org.gjt.sp.jedit.BeanShell.getNameSpace();
		try
		{
			ns.setVariable("console",console);
			ns.setVariable("output",output);
			Object retVal = org.gjt.sp.jedit.BeanShell.eval(view,command,false);
			ns.setVariable("console",null);
			ns.setVariable("output",null);

			if(retVal != null)
				output.print(null,retVal.toString());
		}
		catch(EvalError e)
		{
			// thrown if set/unset fails...
			// can't do anything about it.
			Log.log(Log.ERROR,this,e);
		}

		output.commandDone();
	}

	public void stop(Console console)
	{
	}

	public boolean waitFor()
	{
		return true;
	}
}
