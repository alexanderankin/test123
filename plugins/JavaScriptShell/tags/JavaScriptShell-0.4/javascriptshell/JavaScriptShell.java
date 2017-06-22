/*
 * JavaScriptShell.java
 *
 * Copyright (c) 2017 Zigmantas Kryzius <zigmas.kr@gmail.com>
 * Copyright (c) 2009 Robert Ledger <robert@pytrash.co.uk>
 * Copyright (c) 2007 Jakub Roztocil <jakub@webkitchen.cz>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */
package javascriptshell;

//{{{ Imports
import console.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.script.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.Macros.Handler;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.gui.TextAreaDialog;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

import java.awt.Color;
import jdk.nashorn.api.scripting.*;

//}}}

//{{{ class JavaScriptShell
/**
 * A Console Shell for executing javascript.
 */
public class JavaScriptShell extends Shell {

	/** The scripting engine instance used to evaluate scripts.  */
	private static ScriptEngine engine;

	//{{{ JavaScriptShell constructor

	/**
	 * Creates a new JavaScriptShell object.
	 *
	 * @param name  Name of the JavaScript Shell
	 */
	public JavaScriptShell(String name) {
		super(name);
	}//}}}

	//{{{ init() method

	/** Initialze the JavaScriptShell object.  */
	public static void init() {

		ScriptEngineManager scriptManager  = new ScriptEngineManager();
		//Log.log(Log.DEBUG, JavaScriptShell.class, "JavaScript: scriptManager: " + scriptManager.toString());
		scriptManager.registerEngineExtension("js", new NashornScriptEngineFactory());
		engine = scriptManager.getEngineByExtension("js");

		if (!(engine == null)) {
			Log.log(Log.DEBUG, JavaScriptShell.class, "Nashorn scripting engine: " + engine.toString());
			engine.setBindings(new SimpleBindings(), ScriptContext.ENGINE_SCOPE);
			engine.put("engine", engine);
		} else {
			Log.log(Log.ERROR, JavaScriptShell.class, "Nashorn scripting engine: null");
		}
	}//}}}

	//{{{ execute() method

	/**
	 * Execute a javascript command.<p>
	 *
	 * Called from console to execute a javascript command.<p>
	 *
	 * The output from print() is intercepted after passing
	 * through engine.context.writer (a PrintWriter) and sent
	 * to the output object provided.
	 *
	 * @param console  Console instance requesting execution.
	 * @param input    Not Used Yet.
	 * @param output   Console Output class to which output is to be sent.
	 * @param error    Not Used Yet, error output is always sent to output.
	 * @param command  The script to be executed.
	 */
	public void execute(Console console, String input, Output output, Output error,
			String command) {

		if (command == null || command.equals("")) {
			output.commandDone();
			return;
		}

		setGlobals(console.getView(), output, console);
		engine.getContext().setWriter(new PrintWriter(new ShellWriter(output)));

		try {
			Object  retVal  = engine.eval(command);
			String  result  = "";

			if (retVal != null) {
				result = retVal.toString();
			}
			output.writeAttrs(
				ConsolePane.colorAttributes(console.getPlainColor()),
				result
			);
		} catch (Exception e) {
			output.print(console.getErrorColor(), e.toString());
		}
		finally {
			output.commandDone();
		}
	}//}}}

	//{{{ printInfoMessage() method

	/**
	 * Prints an 'info' message when starting or clearing a shell.<p>
	 *
	 * The message is taken from property javascriptshell.info and is only
	 * displayed if the property javascript.info.toggle is set to true.
	 *
	 * @param output  Object to which output should be directed.
	 */
	public void printInfoMessage(Output output) {
		if (jEdit.getBooleanProperty("javascriptshell.info.toggle", true)) {
			output.print(null, jEdit.getProperty("javascriptshell.info"));
		}
	}//}}}

	//{{{ printPrompt() method

	/**
	 * Print a javascript prompt on an interactive shell.<p>
	 *
	 * The prompt to use is provided by the javascriptshell.prompt property.
	 *
	 * @param console  The console to be used.
	 * @param output   Object to which output should be directed.
	 */
	public void printPrompt(Console console, Output output) {

		String  prompt  = "\n" + jEdit.getProperty("javascriptshell.prompt", "JavaScript");
		//output.writeAttrs(ConsolePane.colorAttributes(console.getInfoColor()), prompt);
		// blue color is to mark that this console is not a 'true' Nashorn console
		output.writeAttrs(ConsolePane.colorAttributes(Color.blue), prompt);
		output.writeAttrs(ConsolePane.colorAttributes(console.getPlainColor()), " ");
	}//}}}

	//{{{ setClobals() methods

	/**
	 * Setup the global namespace for the shell before each evaluation.
	 *
	 * @param view     The view containing the console.
	 */
	private static void setGlobals(View view) {
		setGlobals(view, null, null);
	}
	/**
	 * Setup the global namespace for the shell before each evaluation.
	 *
	 * @param view     The view containing the console.
	 * @param output   The console Output object to which output should be directed.
	 */
	private static void setGlobals(View view, Output output){
		setGlobals(view, output, null);
	}
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
	 private static void setGlobals(View view, Output output, Console console) {

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

	}//}}}

	//{{{ runScript() method

	/**
	 * Execute the contents of a file as a script.
	 *
	 * A fake console.Output object is provided, to collect output
	 * from the script, thus emualating a console.
	 *
	 * @param path  The file path of the script.
	 * @param view  A jEdit view.
	 */
	public static void runScript(String path, View view) {

		File  file  = new File(path);

		if (file.exists()) {

			try {

				BufferedReader  reader  = new BufferedReader(new FileReader(file));
				StringBuffer    code    = new StringBuffer();
				Output          output  = new StringOutput();
				String          line;

				while ((line = reader.readLine()) != null) {
					code.append(line + "\n");
				}

				//Log.log(Log.DEBUG, JavaScriptShell.class, code.toString());
				setGlobals(view, output);
				engine.getContext().setWriter(new PrintWriter(new ShellWriter(output)));
				engine.eval(code.toString());
			} catch (Exception e) {
				Log.log(Log.ERROR, JavaScriptShell.class, e.toString());
				new TextAreaDialog(view, "javascript-error", e);
			}
		}
	}//}}}

	//{{{ evaluateSelection() method
	/** Evaluate the contents of selected text in the current buffer.  */
	public static void evaluateSelection() {

		View      view          = jEdit.getActiveView();
		TextArea  textArea      = view.getTextArea();
		String    selectedText  = textArea.getSelectedText();

		if (selectedText == null) {
			view.getToolkit().beep();
		} else {

			RetVal  result  = evaluateCode(view, selectedText, true);

			if (result.error) {
				view.getToolkit().beep();
				return;
			}
			Object  retVal  = result.retVal;
			if (retVal == null) {
				retVal = "";
			} else {
				retVal = retVal.toString();
			}
			textArea.setSelectedText(result.out.toString() + retVal);
		}
	}//}}}

	//{{{ evaluateBuffer() method

	/**
	 * Evaluate the entire contents of the curent buffer.
	 */
	public static void evaluateBuffer() {
		View  view  = jEdit.getActiveView();
		evaluateBuffer(view, false);
	}


	/**
	 * Evaluate the entire contents of the curent buffer.<p>
	 *
	 * Optionally show the output in a dialog box.
	 *
	 * @param view        Description of the Parameter
	 * @param showOutput  true if output is to be shown in a dialog.
	 */
	public static void evaluateBuffer(View view, boolean showOutput) {
		String  text    = view.getTextArea().getText();
		RetVal  retval  = evaluateCode(view, text);
		if (showOutput) {

		}
	}//}}}

	//{{{ evaluateCode() method
	/**
	 * Evaluate javascript, collect output and return the result.
	 *
	 * @param view     the jEdit view from which this command was invoked.
	 * @param command  the javascript to be evaluated.
	 * @return         RetVal instance containing result, output and error information.
	 */
	public static RetVal evaluateCode(View view, CharSequence command) {
		return evaluateCode(view, command, true);
	}


	/**
	 * Evaluate javascript, collect output and return the result.
	 *
	 * @param command    the javascript to be evaluated.
	 * @param view       the jEdit view from which this command was invoked.
	 * @param showError  set to true if errors are to be shown in a dialog.
	 * @return           RetVal instance containing result, output and error information.
	 */
	public static RetVal evaluateCode(View view, CharSequence command, boolean showError) {

		StringOutput  output  = new StringOutput();
		Object        retVal  = null;

		if (command == null || command.equals("")) {
			return new RetVal("", "");
		}

		setGlobals(view, output);
		engine.getContext().setWriter(new PrintWriter(new ShellWriter(output)));

		try {
			retVal = engine.eval(command.toString());
		} catch (ScriptException e) {
			Log.log(Log.ERROR, JavaScriptShell.class, e.toString());

			if (showError) {
				new TextAreaDialog(view, "javascript-error", e);
			}

			return new RetVal(e, output.toString(), true, showError);
		}

		return new RetVal(retVal, output.toString());
	}//}}}

	//{{{ RetVal class
	/**
	 * Encapsulates the return value for the evaluateCode method.
	 */
	public static class RetVal {

		/** Flag set to true if an error dialog has been shown.*/
		public boolean errorShown;

		/** Flag set to true if an error occured */
		public boolean error;

		/** A CharSequence representing the generated output. */
		public CharSequence out;

		/** The object returned by the javascript engine after evaluation. */
		public Object retVal;


		/**
		 * Creates a new RetVal object. This class contains the result of executing a script.
		 *
		 * @param retVal  the result of evaluating the script.
		 * @param out     the output produced by the script.
		 */
		public RetVal(Object retVal, CharSequence out) {
			this(retVal, out, false, true);
		}


		/**
		 * Creates a new RetVal object. This class contains the result of executing a script.
		 *
		 * @param retVal      the result of evaluating the script.
		 * @param out         the output produced by the script.
		 * @param error       true if an error occured while executing the script.
		 * @param errorShown  true if the error
		 */
		public RetVal(Object retVal, CharSequence out, boolean error, boolean errorShown) {
			this.error = error;
			this.out = out;
			this.retVal = retVal;
			this.errorShown = errorShown;
		}
	}//}}}
}

/*
 * :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false:
 */

