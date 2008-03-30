/*
* JavaScriptShell.java
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

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import org.gjt.sp.jedit.*;
import console.*;
import javax.script.*;
import org.gjt.sp.jedit.Macros.Handler;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.gui.TextAreaDialog;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;


public class JavaScriptShell extends Shell {

	private static ScriptEngine engine;

	public static void init() {
		if (engine == null) {
			ScriptEngineManager manager = new ScriptEngineManager();
			engine = manager.getEngineByName("JavaScript");
			engine.setBindings(new SimpleBindings(), ScriptContext.ENGINE_SCOPE);
		}
	}

	public JavaScriptShell(String name) {
		super(name);
	}

	public void execute(Console console, String input, Output output, Output error, String command) {
		if (command == null || command.equals("")) {
			output.commandDone();
			return;
		}
		setGlobals(console.getView());
		try {
			Object retVal = engine.eval(command);
			String out = "";
			if (retVal != null) {
				out = retVal.toString();
			}
			output.print(console.getPlainColor(), out);
			output.commandDone();
		} catch (ScriptException e) {
			output.print(console.getErrorColor(), e.getMessage());
			output.commandDone();
		}
	}

	public void printInfoMessage(Output output) {

	}

	public void printPrompt(Console console, Output output) {
		output.writeAttrs(ConsolePane.colorAttributes(console.getInfoColor()), "JavaScript> ");
	}

	private static void setGlobals(View view) {
		engine.put("view", view);
		engine.put("textArea", view == null ? null : view.getTextArea());
		engine.put("buffer", view == null ? null : view.getTextArea().getBuffer());
		engine.put("engine", engine);
	}


	public static void runScript(String path, View view) {
		File file = new File(path);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuffer code = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				code.append(line+"\n");
			}
			evaluateCode(view, code.toString());
		} catch (Exception e) {
			Log.log(Log.ERROR, JavaScriptShell.class, e.toString());
			new TextAreaDialog(view, "javascript-error", e);
		}
	}

	public static void evaluateSelection() {
		View view = jEdit.getActiveView();
		TextArea textArea = view.getTextArea();
		String selectedText = textArea.getSelectedText();
		if (selectedText == null) {
			view.getToolkit().beep();
		} else {
			evaluateCode(view, selectedText);
		}
	}

	public static void evaluateBuffer() {
		View view = jEdit.getActiveView();
		JEditBuffer buffer = view.getBuffer();
		evaluateCode(view, buffer.getText(0, buffer.getLength()));
	}

	public static void evaluateCode(View view, String code) {
			try {
				//Log.log(Log.DEBUG, JavaScriptShell.class, code.toString());
				setGlobals(view);
				engine.eval(code);
			} catch (Exception e) {
				Log.log(Log.ERROR, JavaScriptShell.class, e.toString());
				new TextAreaDialog(view, "javascript-error", e);
			}
	}



}



/* :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false: */
