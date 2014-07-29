/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2012-2014 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.kpouer.jedit.lua;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * @author Matthieu Casanova
 */
public class LuaSideKickPlugin extends EditPlugin
{
	public static void executeBuffer(Buffer buffer)
	{
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine e = mgr.getEngineByExtension(".lua");
		try
		{
			Object eval = e.eval(buffer.getText());
			System.out.println(eval);
		}
		catch (ScriptException e1)
		{
			Log.log(Log.ERROR, LuaSideKickPlugin.class, e);
		}
	}
}
