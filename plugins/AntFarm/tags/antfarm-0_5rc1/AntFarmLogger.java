/*
 *  AntFarmLogger.java - Ant build utility plugin for jEdit
 *  Copyright (C) 2000 Chris Scott
 *  Other contributors: Rick Gibbs, Todd Papaioannou
 *
 *  The Apache Software License, Version 1.1
 *
 *  Copyright (c) 1999, 2000 The Apache Software Foundation.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution, if
 *  any, must include the following acknowlegement:
 *  "This product includes software developed by the
 *  Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowlegement may appear in the software itself,
 *  if and wherever such third-party acknowlegements normally appear.
 *
 *  4. The names "The Jakarta Project", "Ant", and "Apache Software
 *  Foundation" must not be used to endorse or promote products derived
 *  from this software without prior written permission. For written
 *  permission, please contact apache@apache.org.
 *
 *  5. Products derived from this software may not be called "Apache"
 *  nor may "Apache" appear in their names without prior written
 *  permission of the Apache Group.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of the Apache Software Foundation.  For more
 *  information on the Apache Software Foundation, please see
 *  <http://www.apache.org/>.
 */

import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;

/**
 *  Description of the Class
 *
 *@author     steinbeck
 *@created    27. August 2001
 */
public class AntFarmLogger implements BuildLogger
{
	/**
	 *  Description of the Field
	 */
	protected int msgOutputLevel = Project.MSG_INFO;

	/**
	 *  Description of the Field
	 */
	protected boolean emacsMode = false;

	private PrintStream output;
	private PrintStream err;
	private long startTime = System.currentTimeMillis();
	private BuildMessage currentBuildMessage = new BuildMessage();
	private AntFarmPlugin farm;
	private AntFarm window;

	/**
	 *  Description of the Field
	 */
	protected static String lSep = System.getProperty("line.separator");


	/**
	 *  Constructor for the AntFarmLogger object
	 *
	 *@param  farm    Description of Parameter
	 *@param  window  Description of Parameter
	 */
	public AntFarmLogger(AntFarmPlugin farm, AntFarm window)
	{
		this.farm = farm;
		this.window = window;
	}


	/**
	 *  Set the msgOutputLevel this logger is to respond to. Only messages with a
	 *  message level lower than or equal to the given level are output to the log.
	 *
	 *@param  level  the logging level for the logger.
	 */
	public void setMessageOutputLevel(int level)
	{
		this.msgOutputLevel = level;
	}


	/**
	 *  Sets the OutputPrintStream attribute of the AntFarmLogger object
	 *
	 *@param  output  The new OutputPrintStream value
	 */
	public void setOutputPrintStream(PrintStream output)
	{
		this.output = output;
	}


	/**
	 *  Sets the ErrorPrintStream attribute of the AntFarmLogger object
	 *
	 *@param  err  The new ErrorPrintStream value
	 */
	public void setErrorPrintStream(PrintStream err)
	{
		this.err = err;
	}


	/**
	 *  Sets the EmacsMode attribute of the AntFarmLogger object
	 *
	 *@param  emacsMode  The new EmacsMode value
	 */
	public void setEmacsMode(boolean emacsMode) { }


	/**
	 *  Gets the OutputPrintStream attribute of the AntFarmLogger object
	 *
	 *@return    The OutputPrintStream value
	 */
	public PrintStream getOutputPrintStream()
	{
		return this.output;
	}


	/**
	 *  Gets the ErrorPrintStream attribute of the AntFarmLogger object
	 *
	 *@return    The ErrorPrintStream value
	 */
	public PrintStream getErrorPrintStream()
	{
		return this.err;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of Parameter
	 */
	public void buildStarted(BuildEvent event)
	{
		startTime = System.currentTimeMillis();
	}


	/**
	 *  Prints whether the build succeeded or failed, and any errors the occured
	 *  during the build.
	 *
	 *@param  event  Description of Parameter
	 */

	// FIXME: For some reason this is not being called.  No biggie, its just
	//     annoying not to know why
	public void buildFinished(BuildEvent event)
	{
		System.out.println("FINISHED");
		Throwable error = event.getException();

		if (error == null)
		{
			output.println(lSep + "BUILD SUCCESSFUL");
		}
		else
		{
			err.println(lSep + "BUILD FAILED" + lSep);

			if (error instanceof BuildException)
			{
				err.println(error.toString());

				Throwable nested = ((BuildException) error).getException();

				if (nested != null)
				{
					System.out.println("h1");
					nested.printStackTrace(err);
				}
			}
			else
			{
				System.out.println("h2");
				error.printStackTrace(err);
			}
		}

		//output.println(lSep + "Total time: " + formatTime(System.currentTimeMillis() - startTime));
	}


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of Parameter
	 */
	public void targetStarted(BuildEvent event)
	{
		if (msgOutputLevel <= Project.MSG_INFO)
		{
			farm.handleBuildMessage(window, new BuildMessage(lSep + event.getTarget().getName() + ":"));
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of Parameter
	 */
	public void targetFinished(BuildEvent event) { }


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of Parameter
	 */
	public void taskStarted(BuildEvent event) { }


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of Parameter
	 */
	public void taskFinished(BuildEvent event) { }


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of Parameter
	 */
	public void messageLogged(BuildEvent event)
	{
		// A flag we'll use to denote which compiler we are using
		boolean usingJikes = false;

		if (event.getPriority() <= msgOutputLevel)
		{
			// Get the name of the task
			String name = event.getTask().getTaskName();

			// Make sure we are in the javac task
			if (name.equals("javac"))
			{
				// Find out which compiler we are using
				String compiler =
						event.getTask().getProject().getProperty("build.compiler");

				if (compiler != null && compiler.equals("jikes"))
				{
					usingJikes = true;
				}

				// Retrieve the message
				String line = event.getMessage();

				// Make sure it's not a blank line
				if (!line.trim().equals(""))
				{
					// check to see if this is the first line of the error message
					if (isMessageLine(line, usingJikes))
					{
						if (usingJikes)
						{
							currentBuildMessage = parseJikesMessage(line);
						}
						else
						{
							currentBuildMessage = parseJavacMessage(line);
						}
					}
					else
					{
						if (!usingJikes)
						{
							// check for the column pos of the error
							int column = getLocationPos(line);

							if ((column >= 0) && (currentBuildMessage != null))
							{
								currentBuildMessage.setColumn(column);
								farm.handleBuildMessage(window, currentBuildMessage);
								currentBuildMessage = null;
							}
						}
						else
						{
							if (line.startsWith("Compiling "))
							{
								farm.handleBuildMessage(window, new BuildMessage(line));
							}
							// If the line starts like this it's the message line
							else if (line.startsWith("***"))
							{
								currentBuildMessage.setMessage(getJikesBuildErrorMessage(line));
								farm.handleBuildMessage(window, currentBuildMessage);
							}
							// Ok, the next line will give us the line, column and the message
							else if (line.lastIndexOf("-") >= 0)
							{
								currentBuildMessage.setColumn(line.length());
							}
							else if (line.indexOf(".") >= 0)
							{
								if (line.indexOf(". . .") == -1)
								{
									int lineNumber = getJikesLineNumber(line);
									currentBuildMessage.setLine(lineNumber);
								}
							}

						}
					}
					// end of not a message line
				}
			}
			else
			{
				farm.handleBuildMessage(window, new BuildMessage(event.getMessage()));
			}
		}
	}


	/**
	 *  Get the error message from a jikes message
	 *
	 *@param  line  Description of Parameter
	 *@return       The JikesBuildErrorMessage value
	 */
	private String getJikesBuildErrorMessage(String line)
	{

		// Get rid of the *'s
		int newStart = line.lastIndexOf("*");

		String message = line.substring(newStart + 1);
		return message;
	}
	// end of getJikesBuildErrorMessage


	/**
	 *  Get the line number from a jikes message
	 *
	 *@param  message  Description of Parameter
	 *@return          The JikesLineNumber value
	 */
	private int getJikesLineNumber(String message)
	{
		// What we will return
		int Result;

		// Ok, let's see if there is the usual period
		int dot = message.indexOf(".");

		// Now, trim down the message
		String number = message.substring(0, dot);
		number = number.trim();

		// Ok, what does that give us?
		Result = Integer.parseInt(number);

		return Result;
	}


	/**
	 *  Gets the MessageLine attribute of the AntFarmLogger object
	 *
	 *@param  line   Description of Parameter
	 *@param  jikes  Description of Parameter
	 *@return        The MessageLine value
	 */
	private boolean isMessageLine(String line, boolean jikes)
	{
		if (!jikes)
		{
			// Get the position of
			int p1 = line.indexOf(".java:");

			if (p1 >= 0)
			{
				p1 += 5;
			}

			int p2 = line.indexOf(":", p1 + 1);

			if (p1 >= 0 && p2 >= 0)
			{
				try
				{
					Integer.parseInt(line.substring(p1 + 1, p2).trim());
					return true;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			}

			//check for last line of the format "LineNr String" like "2 warnings"
			int x = line.indexOf(' ');

			try
			{
				if (x > 0)
				{
					Integer.parseInt(line.substring(0, x));
				}
				//line starts with a number??

				String s = line.substring(x + 1).toLowerCase();

				if (s.startsWith("error") || s.startsWith("warning"))
				{
					return true;
				}
			}
			catch (NumberFormatException e)
			{
				// do nothing
			}

			return false;
		}
		else
		{
			// Trim the line
			line.trim();

			// Does it start with Compiling?
			if (line.startsWith("Compiling"))
			{
				return false;
			}

			// Does it start with "Found" (the string that jikes uses to signify an
			// error.
			if (line.startsWith("Found"))
			{
				return true;
			}
		}

		return false;
	}
	// end of isMessageLine


	/**
	 *  Gets the LocationPos attribute of the AntFarmLogger object
	 *
	 *@param  line  Description of Parameter
	 *@return       The LocationPos value
	 */
	private int getLocationPos(String line)
	{
		return line.indexOf('^');
	}


	/**
	 *  Parse the first line that jikes returns to see if it is a compile message
	 *
	 *@param  line  Description of Parameter
	 *@return       Description of the Returned Value
	 */
	private BuildMessage parseJikesMessage(String line)
	{
		// First of all, let's work out what the file name is
		int firstQuote = line.indexOf("\"");
		int endOfName = line.indexOf(".java");

		// Ok, this is ugly, but we need to adjust our numbers.
		String filename = line.substring(firstQuote + 1, endOfName + 5);

		BuildMessage theMessage = new BuildMessage();
		theMessage.setFileName(filename);

		// Now work out if this is an error
		int i = line.indexOf("error");
		if (i != -1)
		{
			theMessage.setError(true);
		}

		// Or a warning
		i = line.indexOf("warning");
		if (i != -1)
		{
			theMessage.setWarning(true);
		}

		return theMessage;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  line  Description of Parameter
	 *@return       Description of the Returned Value
	 */
	private BuildMessage parseJavacMessage(String line)
	{
		int p1 = line.indexOf(".java:");

		if (p1 >= 0)
		{
			p1 += 5;
		}

		int p2 = line.indexOf(":", p1 + 1);

		if (p1 >= 0 && p2 >= 0)
		{
			int p0 = line.indexOf("[javac]");

			if (p0 >= 0)
			{
				p0 += 7;
			}

			//only if ".java:" and a second ":" are found
			//the line includes a correct message to display

			//extract the filename
			String f = line.substring(p0 >= 0 ? p0 : 0, p1).trim();

			//get the line number
			String l = line.substring(p1 + 1, p2).trim();

			//and finally get the message itself
			String m = line.substring(p2 + 1).trim();

			// return a new error message for the error list:
			return new BuildMessage(f, Integer.parseInt(l), "error: " + m);
		}

		return null;
	}

}

