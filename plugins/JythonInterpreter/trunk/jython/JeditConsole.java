/*
 *  JythonExecutor.java - JythonInterpreter Shell
 *  Copyright (C) 10 June 2001 Carlos Quiroz
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

import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import org.python.core.*;
import org.python.util.*;

public class JeditConsole extends InteractiveInterpreter {
	private boolean onProcess = false;
	private DefaultConsole defConsole = new DefaultConsole();
	private String FILENAME="<console>";
	/**
	 *  Output and error streams
	 */
	protected ByteArrayOutputStream out, err;

	public JeditConsole(ByteArrayOutputStream out, ByteArrayOutputStream err) {
		super();
		this.out = out;
		this.err = err;
		setOut(out);
		setErr(err);
	}

	public JeditConsole(PyObject dict, PySystemState systemState, ByteArrayOutputStream out, ByteArrayOutputStream err) {
		super(dict);
		this.out = out;
		this.err = err;
		setOut(out);
		setErr(err);
	}

	public boolean pushLine(String line) {
		if (buffer.length() > 0)
				buffer.append("\n");
		buffer.append(line);
		onProcess = runsource(buffer.toString(), FILENAME);
		if (!onProcess)
				resetbuffer();
		return onProcess;
	}

	public boolean isOnProcess() {
		return onProcess;
	}

	public void doIO(String command, Console console) {
		doIO(command, console, true);
	}

	public void doIO(String command, Console console, boolean addPrompt) {
		if (console == null) {
			console = defConsole;
		}
		if (out.size() > 1) {
			String resultText  = out.toString();
			console.printResult(resultText.substring(0, resultText.length() -
					((command.length() == 0) ? 0 : 1)));
		}
		out.reset();
		if (err.size() > 1) {
			String errorText  = err.toString();
			console.printErrorMsg(errorText.substring(0, errorText.length() -
					((command.length() == 0) ? 0 : 1)), FILENAME, 0);
		}
		err.reset();
		if (addPrompt) {
			console.printPrompt();
		}
	}

	public void doException(Console console, Throwable e) {
		if (console == null) {
			console = defConsole;
		}
		try {
			doIO(" ", console, false);
			if (console != null) {
				console.printError(e);
				console.printPrompt();
			}
		} catch(Exception ex) {
			// ignore
		}
	}
}

/**
 * JythonCommand objects are commands to be executed by the interpreter. The
 * commands are accumulted in the interpreter queue
 */
interface JythonCommand {
	/**
	 * Execute the current command in the interpreter
	 */
	public void execute(JeditConsole interpreter) throws Exception;
}

abstract class AbstractCommand implements JythonCommand {
	protected View view;
	protected Console console;
	protected Buffer buffer;

	AbstractCommand(Buffer buffer, View view, Console console) {
		this.buffer = buffer;
		this.view = view;
		this.console = console;
	}

	public void execute(JeditConsole interpreter) {
		if (!interpreter.isOnProcess()) {
			EditPane editPane  = view.getEditPane();
			Buffer buffer      = editPane.getBuffer();
			interpreter.set("buffer", buffer);
			interpreter.set("editPane", editPane);
			interpreter.set("textArea", editPane.getTextArea());
			interpreter.set("view", view);
		}
		try {
			doExecute(interpreter);
		} catch (PyException e) {
			interpreter.doException(console, e);
		} catch (Throwable e) {
			interpreter.doException(console, e);
		}
	}

	protected abstract void doExecute(JeditConsole interpreter) throws Exception;
}

class ExecuteCommand extends AbstractCommand {
	ExecuteCommand(Buffer buffer, View view, Console console) {
		super(buffer, view, console);
	}

	public void doExecute(JeditConsole interpreter) throws Exception {
		PyStringMap locals = (PyStringMap)interpreter.getLocals();
		locals.__setitem__("__name__", new PyString("__main__"));
		interpreter.setLocals(locals);
		interpreter.execfile(buffer.getPath());
		interpreter.doIO(" ", console);
	}
}

class ImportCommand extends AbstractCommand {
	ImportCommand(Buffer buffer, View view, Console console) {
		super(buffer, view, console);
	}

	public void doExecute(JeditConsole interpreter) throws Exception {
		String module = buffer.getName().substring(0, buffer.getName().length()-3);

		PyStringMap locals = (PyStringMap)interpreter.getLocals();
		locals.__setitem__("__name__", new PyString(module));
		interpreter.setLocals(locals);
		interpreter.execfile(buffer.getPath());
		interpreter.doIO(" ", console);
	}
}

class LineCommand extends AbstractCommand {
	private String command;

	LineCommand(String command, View view, Console console) {
		super(null, view, console);
		this.command = command;
	}

	public void doExecute(JeditConsole interpreter) throws Exception {
		if (!interpreter.pushLine(command)) {
			interpreter.doIO(command, console);
		} else {
			if (console != null) {
				console.printOnProcess();
			}
		}
	}

	public String toString() {
		return command;
	}
}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
