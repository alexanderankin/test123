/*
 * BeanShell.java - Executes commands in jEdit's BeanShell interpreter
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

class BeanShell extends Shell
{
	public BeanShell()
	{
		super("BeanShell");
	}

	public void printInfoMessage(Console console)
	{
		console.printInfo(jEdit.getProperty("console.beanshell.info"));
	}

	public void execute(View view, String command, Console console)
	{
		NameSpace ns = org.gjt.sp.jedit.BeanShell.getNameSpace();
		try
		{
			ns.setVariable("console",console);
			Object retVal = org.gjt.sp.jedit.BeanShell.eval(view,command,false);
			ns.setVariable("console",null);

			if(retVal != null)
				console.printPlain(retVal.toString());
		}
		catch(EvalError e)
		{
			// thrown if set/unset fails...
			// can't do anything about it.
			Log.log(Log.ERROR,this,e);
		}
	}

	public boolean waitFor()
	{
		return true;
	}
}
