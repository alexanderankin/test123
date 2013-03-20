/*
 * InputStreamTask.java - Thread for the process input handling.
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999-2012 Slava Pestov, Marcelo Vanzin, Artem Bryantsev 
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
import java.io.OutputStream;
import java.io.PipedOutputStream;
//}}}

/**
 * Thread for feeding input to a running subprocess.
 */
class InputStreamTask extends SimpleInputStreamTask
{
	private ConsoleProcessTask process;

	// {{{ constructor
	InputStreamTask(ConsoleProcessTask process, OutputStream processOutput, PipedOutputStream userInput)
	{
		super(processOutput, userInput);
		
		this.process = process;
	} // }}}

	// {{{ beforeWorking() method
	/**
	 * Run BEFORE main working loop starts
	 * (under "try" section) 
	 */
	@Override
	protected void beforeWorking() throws Exception
	{
		process.streamStart(this);
	} // }}}
	
	// {{{ afterWorking() method
	/**
	 * Run AFTER:
	 * - main working loop ends
	 * - "finalOutputing()" method
	 *
	 * (under "finally" section) 
	 */
	@Override
	protected void afterWorking()
	{
		process.streamFinish(this);
	} // }}}
	
	// {{{ exception_dumpToOwner() method
	/**
	 * By default do nothing AFTER "exception_dumpToLog" method
	 * (under "catch" section)
	 */
	@Override
	protected void exception_dumpToOwner(Exception e)
	{
		process.errorNotification(e);
	} // }}}
}
