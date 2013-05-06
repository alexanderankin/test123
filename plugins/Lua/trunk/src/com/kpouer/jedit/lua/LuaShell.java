/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2013 jEdit contributors
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

import java.io.PrintWriter;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import console.Console;
import console.ConsolePane;
import console.ConsolePlugin;
import console.Output;
import console.Shell;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * @author Matthieu Casanova
 */
public class LuaShell extends Shell
{
	private ScriptEngine engine;

	public LuaShell()
	{
		super("lua");
		ScriptEngineManager mgr = new ScriptEngineManager();
		engine = mgr.getEngineByExtension(".lua");

	}
	//{{{ printInfoMessage() method
	@Override
	public void printInfoMessage(Output output)
	{
		if (jEdit.getBooleanProperty("console.shell.info.toggle"))
			output.print(null,jEdit.getProperty("console.lua.info"));
	} //}}}

	//{{{ printPrompt() method
	/**
	 * Prints a prompt to the specified console.
	 * @param output The output
	 */
	@Override
	public void printPrompt(Console console, Output output)
	{
		output.writeAttrs(
			ConsolePane.colorAttributes(console.getInfoColor()),
			jEdit.getProperty("console.lua.prompt"));
		output.writeAttrs(null," ");
	} //}}}

	/**
	 * Setup the global namespace for the shell before each evaluation.
	 *
	 * If console is not provided it will be derived from view<.
	 *
	 * I output is not provided it will be derived from console.
	 *
	 * @param view     The view containing the console.
	 * @param output   The console Output object to which output should be directed.
	 * @param console  The console from which the command was invoked!
	 */
	private void setGlobals(View view,  Output output, Console console) {

		Buffer buffer = null;

		if (view != null) {
			buffer = view.getBuffer();
			if (console == null) {
				console = ConsolePlugin.getConsole(view);
			}

			if ( output == null && console != null) {
				output = console.getOutput();
			}
		}

		engine.put("view", view);
		engine.put("editPane", view == null ? null : view.getEditPane());
		engine.put("textArea", view == null ? null : view.getTextArea());
		engine.put("buffer", buffer);
		engine.put("wm", view == null ? null : view.getDockableWindowManager());
		engine.put("scriptPath", buffer == null ? null : buffer.getPath());

		engine.put("console", console);
		engine.put("output", output);

	}

	@Override
	public void execute(Console console, String input, Output output, Output error, String command)
	{
		try
		{
			setGlobals(console.getView(), output, console);
			engine.getContext().setWriter(new PrintWriter(new ShellWriter(output)));
			Object eval = engine.eval(command);
//			output.print(console.getPlainColor(), eval.toString());
		}
		catch (ScriptException e)
		{
			output.print(console.getErrorColor(), e.getLocalizedMessage());
		}
	}
}
