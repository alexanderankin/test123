/*
 * JCompilerShell.java - ConsolePlugin shell for JCompiler
 * Copyright (C) 2000 Dirk Moebius
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
 */

package jcompiler;

// from Java:
import java.io.*;

// from jEdit:
import gnu.regexp.RE;
import gnu.regexp.RESyntax;
import gnu.regexp.REException;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

// from EditBus plugin:
import org.gjt.sp.jedit.DefaultErrorSource;
import org.gjt.sp.jedit.ErrorSource;

// from Console plugin:
import console.Shell;
import console.Console;

// from JCompiler plugin:
import JCompilerPlugin;


/**
 * a JCompiler shell for the Console plugin.
 */
public class JCompilerShell extends Shell implements EBComponent
{

	public JCompilerShell() {
		super("Java Compiler");
		errorSource = new DefaultErrorSource("JCompiler");
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST, errorSource);
		EditBus.addToBus(errorSource);
		EditBus.addToBus(this);
		propertiesChanged();
	}


	public void handleMessage(EBMessage msg) {
		if (msg instanceof PropertiesChanged) {
			propertiesChanged();
		}
	}


	/**
	 * print an information message to the Console.
	 *
	 * @param  console  the Console
	 */
	public void printInfoMessage(Console _console) {
		if (_console != null) {
			_console.printInfo(jEdit.getProperty("jcompiler.msg.info"));
		}
	}


	/**
	 * execute a command.
	 *
	 * @param  view	 the view that was open when the command was invoked.
	 * @param  command  the command.
	 * @param  console  the Console where output should go to.
	 */
	public void execute(View view, String command, Console console) {
		this.console = console; // remember console instance

		stop(); // stop last command
		errorSource.clear();

		String cmd = command.trim();
		if ("compile".equals(cmd)) {
			jthread = new CompilerThread(view, false, false);
		}
		else if ("compilepkg".equals(cmd)) {
			jthread = new CompilerThread(view, true, false);
		}
		else if ("rebuildpkg".equals(cmd)) {
			jthread = new CompilerThread(view, true, true);
		}
		else if ("help".equals(cmd)) {
			printInfoMessage(console);
		}
		else {
			if (console != null) {
				console.printError("Unknown JCompiler command '" + cmd + "'");
			}
		}
	}


	public void stop() {
		if (jthread != null) {
			if (jthread.isAlive()) {
				jthread.stop();
				if (console != null) {
					console.printError("JCompiler thread killed.");
				}
			}
			jthread = null;
		}
	}


	public boolean waitFor() {
		if (jthread != null) {
			try {
				jthread.wait();
				jthread = null;
			}
			catch (InterruptedException ie) {
				return false;
			}
		}
		return true;
	}


	private void propertiesChanged() {
		parseAccentChar = jEdit.getBooleanProperty("jcompiler.parseaccentchar", true);
		String sErrorRE = jEdit.getProperty("jcompiler.regexp");
		String sWarningRE = jEdit.getProperty("jcompiler.regexp.warning");
		rfilenamepos = jEdit.getProperty("jcompiler.regexp.filename");
		rlinenopos = jEdit.getProperty("jcompiler.regexp.lineno");
		rmessagepos = jEdit.getProperty("jcompiler.regexp.message");
		try {
			errorRE = new RE(sErrorRE, RE.REG_ICASE, RESyntax.RE_SYNTAX_PERL5);
		}
		catch (REException rex) {
			String errorMsg = "The regular expression " + sErrorRE
				+ " for compiler errors is invalid. Message is: " + rex.getMessage()
				+ " at position " + rex.getPosition();
			Log.log(Log.ERROR, this, errorMsg);
			printLine(errorMsg + '\n');
		}
		try {
			warningRE = new RE(sWarningRE, RE.REG_ICASE, RESyntax.RE_SYNTAX_PERL5);
		}
		catch (REException rex) {
			String errorMsg = "The regular expression " + sWarningRE
				+ " for compiler warnings is invalid. Message is: " + rex.getMessage()
				+ " at position " + rex.getPosition();
			Log.log(Log.ERROR, this, errorMsg);
			printLine(errorMsg + '\n');
		}
	}


	/**
	 * parse the line for errors and send them to ErrorList and Console.
	 */
	private void printLine(String line) {
		int type = -1;
		if (errorRE != null && errorRE.isMatch(line)) {
			if (warningRE != null && warningRE.isMatch(line)) {
				type = ErrorSource.WARNING;
			} else {
				type = ErrorSource.ERROR;
			}
			String filename = errorRE.substitute(line, rfilenamepos);
			String lineno = errorRE.substitute(line, rlinenopos);
			String message = errorRE.substitute(line, rmessagepos);
			pendingError = new PendingError(type, filename,
				Integer.parseInt(lineno) - 1, 0, 0, message);
			if (!parseAccentChar) {
				// don't wait for a line with '^', add error immediately
				pendingError.addToErrorSource();
				pendingError = null;
			}
		}

		if (parseAccentChar && pendingError != null && line.trim().equals("^")) {
			// a line with a single '^' in it: this determines the column
			// position of the last compiler error
			pendingError.setStartPos(line.indexOf('^'));
			pendingError.addToErrorSource();
			pendingError = null;
		}

		if (console != null) {
			switch (type) {
				case ErrorSource.WARNING:
					console.printWarning(line);
					break;
				case ErrorSource.ERROR:
					console.printError(line);
					break;
				default:
					console.printPlain(line);
					break;
			}
		}
	}


	private DefaultErrorSource errorSource = null;
	private PendingError pendingError = null;
	private CompilerThread jthread = null;
	private Console console = null;
	private RE errorRE = null;
	private RE warningRE = null;
	private String rfilenamepos;
	private String rlinenopos;
	private String rmessagepos;
	private boolean parseAccentChar;


	/**
	 * Wraps the JCompiler run in a thread.
	 */
	class CompilerThread extends Thread
	{

		CompilerThread(View view, boolean pkgCompile, boolean rebuild) {
			super();
			this.view = view;
			this.pkgCompile = pkgCompile;
			this.rebuild = rebuild;
			this.setPriority(Thread.MIN_PRIORITY);
			this.start();
		}


		public void run() {
			JCompiler jcompiler = new JCompiler();
			Thread outputThread = new OutputThread(jcompiler.getOutputPipe());
			jcompiler.compile(view, view.getBuffer(), pkgCompile, rebuild);
			Log.log(Log.DEBUG, this, "compile thread complete");
			jcompiler = null;
			view = null;
			outputThread = null;
		}


		private View view;
		private boolean pkgCompile;
		private boolean rebuild;

	} // inner class CompilerThread


	/**
	 * This class monitors output created by the CompilerThread.
	 */
	class OutputThread extends Thread
	{
		OutputThread(PipedOutputStream outpipe) {
			try {
				PipedInputStream inpipe = new PipedInputStream(outpipe);
				InputStreamReader in = new InputStreamReader(inpipe);
				buf = new BufferedReader(in);
				this.start();
			}
			catch (IOException ioex) {
				// if there's an exception, the thread will not be started.
			}
		}


		public void run() {
			if (buf == null) return;
			try {
				String line;
				while ((line = buf.readLine()) != null) {
					printLine(line);
				}
				Log.log(Log.DEBUG, this, "ends");
			}
			catch (IOException ioex) {
				// ignore
			}
			finally {
				if (pendingError != null) {
					pendingError.addToErrorSource();
					pendingError = null;
				}
			}
		}


		private BufferedReader buf = null;

	} // inner class OutputThread



	/**
	 * Holds data of an error.
	 */
	class PendingError
	{
		public PendingError(int type, String filename, int lineno,
		int startpos, int endpos, String error) {
			this.type = type;
			this.filename = filename;
			this.lineno = lineno;
			this.startpos = startpos;
			this.endpos = endpos;
			this.error = error;
		}


		public void setStartPos(int startpos) {
			this.startpos = startpos;
		}


		public void setEndPos(int endpos) {
			this.endpos = endpos;
		}


		public void addToErrorSource() {
			errorSource.addError(type, filename, lineno, startpos, endpos, error);
		}


		private int type;
		private String filename;
		private int lineno;
		private int startpos;
		private int endpos;
		private String error;
	}

}
