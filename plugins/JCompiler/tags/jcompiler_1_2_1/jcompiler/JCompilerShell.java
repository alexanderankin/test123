/*
 * JCompilerShell.java - ConsolePlugin shell for JCompiler
 * Copyright (C) 2000 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


import java.io.*;
import java.util.Vector;
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
import org.gjt.sp.jedit.DefaultErrorSource;
import org.gjt.sp.jedit.ErrorSource;
import console.Shell;
import console.Console;


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
		if (msg instanceof PropertiesChanged)
			propertiesChanged();
	}


	/**
	 * print an information message to the Console.
	 *
	 * @param  console  the Console
	 */
	public void printInfoMessage(Console console) {
		if (console != null)
			console.printInfo(jEdit.getProperty("jcompiler.msg.info"));
	}


	/**
	 * execute a command.
	 *
	 * @param  view  the view that was open when the command was invoked.
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
		else if ("javac".equals(cmd)) {
			// command "javac" invoked without arguments
			jthread = new CompilerThread(new String[] {});
		}
		else if (cmd.startsWith("javac ")) {
			jthread = new CompilerThread(parseCmdLineArguments(cmd.substring(6)));
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
			if (warningRE != null && warningRE.isMatch(line))
				type = ErrorSource.WARNING;
			else
				type = ErrorSource.ERROR;

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


	private String[] parseCmdLineArguments(String cmd) {
		// Expand any variables in the command line arguments:
		cmd = JCompiler.expandVariables(cmd);

		// The following is stolen from Slava Pestov's DefaultShell.java (Console plugin):

		// We replace \ with a non-printable char because StreamTokenizer
		// handles \ specially, which causes problems on Windows as \ is the
		// file separator there. After parsing is done, the non printable
		// char is changed to \ once again.
		cmd = cmd.replace('\\', NON_PRINTABLE);

		StreamTokenizer st = new StreamTokenizer(new StringReader(cmd));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		Vector args = new Vector();

		try {
loop:
			for(;;) {
				switch(st.nextToken()) {
					case StreamTokenizer.TT_EOF:
						break loop;
				case StreamTokenizer.TT_WORD:
				case '"':
				case '\'':
					args.addElement(st.sval.replace(NON_PRINTABLE, '\\'));
					break;
				}
			}
		}
		catch (IOException ex) {
			console.printError(jEdit.getProperty("jcompiler.msg.errorCommandLine", new Object[] { ex }));
		}

		Log.log(Log.DEBUG, this, "arguments=" + args);

		String[] array = new String[args.size()];
		args.copyInto(array);
		return array;
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

	private static final char NON_PRINTABLE = 127;


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
			this.setPriority(NORM_PRIORITY - 1);
			this.start();
		}


		CompilerThread(String[] args) {
			super();
			this.args = args;
			this.setPriority(NORM_PRIORITY + 1);
			this.start();
		}


		public void run() {
			JCompiler jcompiler = new JCompiler();
			Thread outputThread = new OutputThread(jcompiler.getOutputPipe());
			if (args == null)
				jcompiler.compile(view, view.getBuffer(), pkgCompile, rebuild);
			else
				jcompiler.compile(args);
			jcompiler = null;
			view = null;
			outputThread = null;
		}


		private View view;
		private boolean pkgCompile;
		private boolean rebuild;
		private String[] args;
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
