/*
 *  JythonHandler.java - Macro Handler for python macros
 *  Copyright (C) 2002 Mike Dillon
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

import org.gjt.sp.jedit.*;
import org.python.util.*;

public class JythonHandler extends Macros.Handler
{
	public JythonHandler()
	{
		super("jython");
	}

	public Macros.Macro createMacro(String macroName, String path)
	{
		String label = Macros.Macro.macroNameToLabel(macroName);

		// Remove the '.py' from the label, but not the macroName
		label = label.substring(0, label.length() - 3);

		return new Macros.Macro(this, macroName, label, path);
	}

	public void runMacro(View view, Macros.Macro macro)
	{
		JythonExecutor.execMacro(view, macro.getPath());
	}
}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
