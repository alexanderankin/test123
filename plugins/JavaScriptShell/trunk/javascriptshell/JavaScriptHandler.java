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

//{{{ imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Macros.*;
//}}}

public class JavaScriptHandler extends Handler {

	//{{{ JavaScriptHandler constructors
	public JavaScriptHandler(String name) {
		super(name);
	}//}}}

	//{{ accept method
	public boolean accept(String path) {
		return path.endsWith(".js");
	}//}}}

	//{{{ createMacro method
	public Macro createMacro(String macroName, String path) {
		// Remove '.js'
		String name = macroName.substring(0, macroName.length() - 3);
		return new Macro(this,
						name,
						Macro.macroNameToLabel(name),
						path);
	}//}}}

	//{{{ runMacro method
	public void runMacro(View view, Macro macro) {
		Log.log(Log.DEBUG, this, "runMacro " + macro.getPath());
		JavaScriptShell.runScript(macro.getPath(), view);
	}//}}}

	//{{{ evaluateCode method
	public JavaScriptShell.RetVal evaluateCode(View view, CharSequence command) {
		Log.log(Log.DEBUG, this, "evaluateCode");
		return JavaScriptShell.evaluateCode(view, command);
	}//}}}

	//{{{ getName method
	public String getName() {
		return "JavaScriptHandler";
	}//}}}

	//{{{ getLabel method
	public String getLabel() {
		return "JavaScript script";
	}//}}}

}
/* :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false: */