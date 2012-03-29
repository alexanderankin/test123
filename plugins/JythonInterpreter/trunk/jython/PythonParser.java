/*
 *  JythonPlugin.java - Jython plugin main class
 *  Copyright (C) 2001 Carlos Quiroz
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

import org.gjt.sp.jedit.Buffer;
import sidekick.SideKickParser;
import sidekick.SideKickParsedData;
import errorlist.DefaultErrorSource;
import org.python.core.PyObject;

/**
 * Java wrapper over JythonPlugin to make loading faster
 *
 * @author     Carlos Quiorz
 * @version    $Id: PythonParser.java,v 1.2 2006/04/22 22:18:40 ezust Exp $
 */
public class PythonParser extends SideKickParser {

	public PythonParser() {
		super("Jython");
	}

	public SideKickParsedData parse(Buffer buffer,
		DefaultErrorSource errorSource) {
			PyObject parsed = JythonExecutor.execPlugin("jython.JythonPlugin",
				"jython", "PyParser", "_parse", new Object[] {buffer, errorSource});
			if (parsed != null) {
				return (SideKickParsedData)parsed.__tojava__(SideKickParsedData.class);
			} else {
				return null;
			}
	}
}
