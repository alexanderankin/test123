/*
 * BeanShell.java - Executes commands in jEdit's BeanShell interpreter
 * Copyright (C) 2000 Slava Pestov
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

import bsh.Interpreter;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

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
		Interpreter interp = org.gjt.sp.jedit.BeanShell.getInterpreter();
		interp.setVariable("console",console);
		Object retVal = org.gjt.sp.jedit.BeanShell.eval(view,command,false);
		interp.setVariable("console",null);
		if(retVal != null)
			console.printPlain(retVal.toString());
	}

	public boolean waitFor()
	{
		return true;
	}
}
