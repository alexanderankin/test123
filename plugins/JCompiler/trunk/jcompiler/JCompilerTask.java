/*
 * JCompilerTask.java
 * Copyright (C) 2001 Dirk Moebius
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


import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;
import gnu.regexp.RE;
import gnu.regexp.RESyntax;
import gnu.regexp.REException;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.DefaultErrorSource;
import org.gjt.sp.jedit.ErrorSource;
import console.Console;
import console.Output;
import console.Shell;


/**
 * Wraps the JCompiler run in a thread.
 * Note: the thread starts itself on construction.
 */
public class JCompilerTask extends Thread implements JCompilerOutput
{

	public JCompilerTask(
			boolean pkgCompile,
			boolean rebuild,
			Console console,
			Output output,
			DefaultErrorSource errorSource)
	{
		super("JCompilerTask");
		this.pkgCompile = pkgCompile;
		this.rebuild = rebuild;
		this.console = console;
		this.output = output;
		this.errorSource = errorSource;

		init(console);

		//this.setPriority(NORM_PRIORITY + 1);
		this.start();
	}


	public JCompilerTask(
			String[] args,
			Console console,
			Output output,
			DefaultErrorSource errorSource)
	{
		super("JCompilerTask");
		this.args = args;
		this.console = console;
		this.output = output;
		this.errorSource = errorSource;

		init(console);

		//this.setPriority(NORM_PRIORITY + 1);
		this.start();
	}


	public void run()
	{
		JCompiler jcompiler = new JCompiler(
			this,
			console.getView(),
			console.getView().getBuffer()
		);

		if (args == null)
			jcompiler.compile(pkgCompile, rebuild);
		else
			jcompiler.compile(args);

		cleanUp();
		output.commandDone();
		Log.log(Log.DEBUG, this, toString() + " ends.");
	}


	// BEGIN JCompilerOutput implementation

	/**
	 * Print the line to the Output instance,
	 * parse the line for errors and send any errors to ErrorList.
	 */
	public void outputText(String line)
	{
		Color color = null;

		if (errorRE != null && errorRE.isMatch(line))
		{
			// new error detected
			String filename = errorRE.substitute(line, rfilenamepos);
			String lineno = errorRE.substitute(line, rlinenopos);
			String message = errorRE.substitute(line, rmessagepos);
			int type = -1;

			if (warningRE != null && warningRE.isMatch(line))
			{
				type = ErrorSource.WARNING;
				color = console.getWarningColor();
			}
			else
			{
				type = ErrorSource.ERROR;
				color = console.getErrorColor();
			}

			if (pendingError != null)
				pendingError.send();

			pendingError = new PendingError(type, filename,
				Integer.parseInt(lineno) - 1, 0, 0, message, line);

			if (!parseAccentChar)
			{
				// don't wait for a line with '^', add error immediately
				pendingError.send();
				pendingError = null;
			}
		}

		output.print(color, line);

		if (parseAccentChar && pendingError != null && line.trim().equals("^"))
		{
			// a line with a single '^' in it: this determines the column
			// position of the last compiler error.
			pendingError.setStartPos(getStartPos(line));
			pendingError.setEndPos(getEndPos(line));
			pendingError.send();
			pendingError = null;
			prevLine = null;
			line = null;
		}

		// add any new line to the current pending error, but not the
		// line containing the error indicator "^" and the line before:
		if (pendingError != null && prevLine != null && prevLine != pendingError.getLine())
			pendingError.addExtraMessage(prevLine);

		prevLine = line;
	}


	/** print an informational message on the Console instance. */
	public void outputInfo(String line)
	{
		console.print(console.getInfoColor(), line);
	}


	/** print an error message on the Console instance. */
	public void outputError(String line)
	{
		console.print(console.getErrorColor(), line);
	}

	// END JCompilerOutput implementation


	private boolean pkgCompile;
	private boolean rebuild;
	private String[] args;
	private Console console;
	private Output output;
	private DefaultErrorSource errorSource;
	private PendingError pendingError;
	private RE errorRE;
	private RE warningRE;
	private String rfilenamepos;
	private String rlinenopos;
	private String rmessagepos;
	private boolean parseAccentChar;
	private String prevLine;


	private void init(Console console)
	{
		parseAccentChar = jEdit.getBooleanProperty("jcompiler.parseaccentchar", true);
		String sErrorRE = jEdit.getProperty("jcompiler.regexp");
		String sWarningRE = jEdit.getProperty("jcompiler.regexp.warning");
		rfilenamepos = jEdit.getProperty("jcompiler.regexp.filename");
		rlinenopos = jEdit.getProperty("jcompiler.regexp.lineno");
		rmessagepos = jEdit.getProperty("jcompiler.regexp.message");

		try
		{
			// dot needs to match newlines, because the modern compiler
			// (>= JDK1.3) outputs multiline errors:
			errorRE = new RE(sErrorRE,
				RE.REG_ICASE | RE.REG_DOT_NEWLINE,
				RESyntax.RE_SYNTAX_PERL5);
		}
		catch (REException rex)
		{
			errorRE = null;
			String errorMsg = jEdit.getProperty("jcompiler.msg.invalidErrorRE",
				new Object[] { sErrorRE, rex.getMessage() }
			);
			outputError(errorMsg);
		}

		try
		{
			warningRE = new RE(sWarningRE,
				RE.REG_ICASE | RE.REG_DOT_NEWLINE,
				RESyntax.RE_SYNTAX_PERL5);
		}
		catch (REException rex)
		{
			warningRE = null;
			String errorMsg = jEdit.getProperty("jcompiler.msg.invalidWarningRE",
				new Object[] { sWarningRE, rex.getMessage() }
			);
			outputError(errorMsg);
		}
	}


	private void cleanUp()
	{
		if (pendingError != null)
		{
			pendingError.send();
			pendingError = null;
		}
		prevLine = null;
	}


	private int getStartPos(String arrowLine)
	{
		if (prevLine != null)
		{
			// We assume that prevLine contains the source code containing
			// the error. Calculate the startPos of the position indicated by
			// the '^' in the prevLine, counting tabs as 8.
			int arrowLinePos = 0;
			int prevLinePos = 0;

			while (arrowLinePos < arrowLine.length()
				&& arrowLine.charAt(arrowLinePos) != '^'
				&& prevLinePos < prevLine.length())
			{
				if (prevLine.charAt(prevLinePos) == '\t')
					if (arrowLine.charAt(arrowLinePos) == '\t')
						++arrowLinePos;
					else
						arrowLinePos += 8; // prevLine has tabs, while arrowLine has not
				else
					++arrowLinePos;
				++prevLinePos;
			}

			return prevLinePos;
		}
		else
			return arrowLine.indexOf('^');
	}


	private int getEndPos(String arrowLine)
	{
		if (prevLine != null)
		{
			// We assume that prevLine contains the source code containing
			// the error. The endPos of the error is simple the length of
			// this line. The arrowLine is ignored.
			return prevLine.length();
		}
		else
			return 0;
	}


	/**
	 * Holds data of an error temporarily.
	 */
	class PendingError
	{

		public PendingError(
				int type, String filename, int lineno,
				int startpos, int endpos,
				String errorText, String line)
		{
			this.type = type;
			this.filename = filename;
			this.lineno = lineno;
			this.startpos = startpos;
			this.endpos = endpos;
			this.errorText = errorText;
			this.line = line;
		}


		public String getLine()
		{
			return this.line;
		}


		public void setStartPos(int startpos)
		{
			this.startpos = startpos;
		}


		public void setEndPos(int endpos)
		{
			this.endpos = endpos;
		}


		public void send()
		{
			DefaultErrorSource.DefaultError error = new DefaultErrorSource.DefaultError(
				errorSource, type, filename, lineno, startpos, endpos, errorText);

			if (extras != null)
			{
				Enumeration enum = extras.elements();
				while (enum.hasMoreElements())
					error.addExtraMessage((String)enum.nextElement());
			}

			errorSource.addError(error);
		}


		public void addExtraMessage(String line)
		{
			if (extras == null)
				extras = new Vector();
			extras.addElement(line);
		}


		private int type;
		private String filename;
		private int lineno;
		private int startpos;
		private int endpos;
		private String errorText;
		private String line;
		private Vector extras;

	}

}
