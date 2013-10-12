/*
 * OutputStreamTask.java - Thread for the process output handling.
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

// {{{ Imports
import java.awt.Color;
import java.io.InputStream;
// }}}

class OutputStreamTask extends ParsingOutputStreamTask
{
	private ConsoleProcessTask process;
	
	// {{{ constructor
	OutputStreamTask(ConsoleProcessTask process,
					 InputStream in,
					 Output output,
					 Color defaultColor,
					 Console console,
					 String currentDirectory)
	{
		super(in, output, defaultColor, console, currentDirectory);
		
		this.process = process;
	} // }}}
	
	// {{{ beforeWorking() method
	@Override
	protected void beforeWorking() throws Exception
	{
		process.streamStart(this);
	} // }}}
	
	// {{{ afterWorking() method
	@Override
	protected void afterWorking() throws Exception
	{
		super.afterWorking();
		
		process.streamFinish(this);
	} // }}}
	
	// {{{ exception_dumpToOwner() method
	@Override
	protected void exception_dumpToOwner(Exception e)
	{
		process.errorNotification(e);
	} // }}}
}

