/*
 *  BuildMessage.java - Ant build utility plugin for jEdit
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

/**
 *  Description of the Class
 *
 *@author     steinbeck
 *@created    27. August 2001
 */
public class BuildMessage
{
	private String absoluteFilename = null;
	private String filename = null;
	private int line = -1;
	private int column = -1;
	private String message = null;
	private boolean isWarning = false;
	private boolean isError = false;


	/**
	 *  Constructor for the BuildMessage object
	 */
	public BuildMessage() { }


	/**
	 *  Constructor for the BuildMessage object
	 *
	 *@param  message  Description of Parameter
	 */
	public BuildMessage(String message)
	{
		this(" ", -1, -1, message);
	}


	/**
	 *  Constructor for the BuildMessage object
	 *
	 *@param  filename  Description of Parameter
	 *@param  line      Description of Parameter
	 *@param  message   Description of Parameter
	 */
	public BuildMessage(String filename, int line, String message)
	{
		this(filename, line, -1, message);
	}


	/**
	 *  Constructor for the BuildMessage object
	 *
	 *@param  filename  Description of Parameter
	 *@param  line      Description of Parameter
	 *@param  column    Description of Parameter
	 *@param  message   Description of Parameter
	 */
	public BuildMessage(String filename, int line, int column, String message)
	{
		this.absoluteFilename = filename;
		this.filename = filename.substring(filename.lastIndexOf(File.separatorChar) + 1);
		this.line = line;
		this.column = column;

		if (message != null)
		{
			this.message = message.trim();
		}

		if (message != null)
		{
			this.isWarning = message.toLowerCase().startsWith("warning:");
		}

		if (message != null)
		{
			this.isError = message.toLowerCase().startsWith("error:");
		}
	}


	/**
	 *  Sets the Column attribute of the BuildMessage object
	 *
	 *@param  column  The new Column value
	 */
	public void setColumn(int column)
	{
		this.column = column;
	}


	/**
	 *  Sets the Line attribute of the BuildMessage object
	 *
	 *@param  line  The new Line value
	 */
	public void setLine(int line)
	{
		this.line = line;
	}


	/**
	 *  Sets the Message attribute of the BuildMessage object
	 *
	 *@param  message  The new Message value
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}


	/**
	 *  Sets the Error attribute of the BuildMessage object
	 *
	 *@param  val  The new Error value
	 */
	public void setError(boolean val)
	{
		this.isError = val;
	}


	/**
	 *  Sets the Warning attribute of the BuildMessage object
	 *
	 *@param  val  The new Warning value
	 */
	public void setWarning(boolean val)
	{
		this.isWarning = val;
	}


	/**
	 *  Sets the FileName attribute of the BuildMessage object
	 *
	 *@param  name  The new FileName value
	 */
	public void setFileName(String name)
	{
		this.absoluteFilename = name;
		this.filename =
				name.substring(name.lastIndexOf(File.separatorChar) + 1);
	}


	/**
	 *  Gets the Complete attribute of the BuildMessage object
	 *
	 *@return    The Complete value
	 */
	public boolean isComplete()
	{
		boolean Result = true;

		if (filename == null)
		{
			Result = false;
		}
		else if (line == -1)
		{
			Result = false;
		}
		else if (column == -1)
		{
			Result = false;
		}
		else if (message == null)
		{
			Result = false;
		}

		return Result;
	}


	/**
	 *  Gets the AbsoluteFilename attribute of the BuildMessage object
	 *
	 *@return    The AbsoluteFilename value
	 */
	public String getAbsoluteFilename()
	{
		return absoluteFilename;
	}


	/**
	 *  Gets the Line attribute of the BuildMessage object
	 *
	 *@return    The Line value
	 */
	public int getLine()
	{
		return line;
	}


	/**
	 *  Gets the Column attribute of the BuildMessage object
	 *
	 *@return    The Column value
	 */
	public int getColumn()
	{
		return column;
	}


	/**
	 *  Gets the Message attribute of the BuildMessage object
	 *
	 *@return    The Message value
	 */
	public String getMessage()
	{
		return message;
	}


	/**
	 *  Gets the Warning attribute of the BuildMessage object
	 *
	 *@return    The Warning value
	 */
	public boolean isWarning()
	{
		return isWarning;
	}


	/**
	 *  Gets the Error attribute of the BuildMessage object
	 *
	 *@return    The Error value
	 */
	public boolean isError()
	{
		return isError;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString()
	{
		if (isError || isWarning)
		{
			return message + " at line " + line + ", column " + column;
		}
		else
		{
			return message;
		}

	}
}

