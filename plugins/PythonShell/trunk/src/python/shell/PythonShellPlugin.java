/*
* PythonShell is a Console shell for hosting a Python REPL.
* Copyright (c) 2012 Damien Radtke - www.damienradtke.org
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version
* 2.0 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
*
* For more information, visit http://www.gnu.org/copyleft
*/

package python.shell;

//{{{ Imports
import console.Shell;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.EditPlugin;
//}}}

public class PythonShellPlugin extends EditPlugin {
	/**
	 * Start the plugin.
	 */
	public void start() {

	}

	/**
	 * Stop the plugin.
	 */
	public void stop() {
		try {
			PythonShell shell = (PythonShell) Shell.getShell("Python");
			shell.stop();
		}
		catch (Exception e) {
			Log.log(Log.ERROR, this, "Can't stop plugin:", e);
		}
	}
}
