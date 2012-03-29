/*
 *  DefaultConsole.java - Console which outputs to the jEdit log
 *
 *  Copyright (c) 1999-2001 Carlos Quiroz
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jython;

import org.gjt.sp.util.Log;

/**
 *  The Console interface defines how a Jython console looks like. The methods
 *  are called by the JythonExecutor class
 *
 * @author     Carlos Quiroz
 * @version    $Id: DefaultConsole.java,v 1.4 2004/01/07 19:20:34 tibu Exp $
 * @since      JythonInterpreter 0.6
 */
public class DefaultConsole implements Console {

	/**
	 *  Request a prompt to be printed
	 */
	public void printPrompt() {
		Log.log(Log.DEBUG, this, ">>>");
	}


	/**
	 *  Prints that the command is on process, i.e. more impout is required to
	 *  to complete the request
	 */
	public void printOnProcess() {
		Log.log(Log.DEBUG, this, "...");
	}


	/**
	 *  Request an operation result to be printer
	 *
	 * @param  msg  Result message
	 */
	public void printResult(String msg) {
		Log.log(Log.DEBUG, this, msg);
	}


	/**
	 *  Request an Exception to be printed
	 *
	 * @param  e  an Exception
	 */
	public void printError(Throwable e) {
		Log.log(Log.ERROR, this, e.getMessage());
	}


	/**
	 *  Prints an error message
	 *
	 * @param  msg  Error message
	 */
	public void printErrorMsg(String msg, String fileno, int lineno) {
		Log.log(Log.DEBUG, this, msg);
	}

	public String toString() {
		return "Default Console";
	}

}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
