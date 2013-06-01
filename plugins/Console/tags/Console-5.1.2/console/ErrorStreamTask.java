/*
 * ErrorStreamTask.java - Thread for the process error handling.
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
import org.gjt.sp.util.Log;
// }}}

class ErrorStreamTask extends SimpleOutputStreamTask
{
	private ConsoleProcessTask process;
	
	// {{{ constructor
	ErrorStreamTask(ConsoleProcessTask process, InputStream in, Output output, Color defaultColor)
	{
		super(in, output, defaultColor);
		
		this.process = process;
		
		setName("Error thread");
	} // }}}
	
	// {{{ beforeWorking() method
	@Override
	protected void beforeWorking() throws Exception
	{
		process.streamStart(this);
	} // }}}
	
	// {{{ afterWorking() method
	@Override
	protected void afterWorking()
	{
		process.streamFinish(this);
	} // }}}
	
	// {{{ exception_dumpToLog() method
	@Override
	protected void exception_dumpToLog(Exception e)
	{
		if ( process.isForeground() )
		{
			Log.log(Log.ERROR, e, e);
		}
	} // }}}
	
	// {{{ exception_dumpToOwner() method
	@Override
	protected void exception_dumpToOwner(Exception e)
	{
		process.errorNotification(e);
	} // }}}
}
