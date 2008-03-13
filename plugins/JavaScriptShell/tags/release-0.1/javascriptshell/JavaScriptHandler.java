/*
* JavaScriptHandler.java
* Copyright (c) 2007 Jakub Roztocil <jakub@webkitchen.cz>
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
*
*/


package javascriptshell;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Macros.*;


public class JavaScriptHandler extends Handler {

	public JavaScriptHandler(String name) {
		super(name);
	}

	public boolean accept(String path) {
		return path.endsWith(".js");
	}


	public Macro createMacro(String macroName, String path) {
		String name = path.substring(0, path.length() - 3);
		return new Macro(this,
						name,
						Macro.macroNameToLabel(name),
						path);
	}

	public void runMacro(View view, Macro macro) {
		Log.log(Log.DEBUG, this, "Running " + macro.getPath());
		JavaScriptShell.runScript(macro.getPath(), view);
	}

	public String getName() {
		return "JavaScriptHandler";
	}

	public String getLabel() {
		return "JavaScript script";
	}

}



/* :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false: */
