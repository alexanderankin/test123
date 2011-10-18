/*
 * PHPParseMessageEvent.java - The PHP Parser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
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
package gatchan.phpparser.parser;

/**
 * The PHPParseErrorEvent.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class PHPParseMessageEvent
{

	public static final int MESSAGE_SHORT_OPEN_TAG = 1;
	public static final int MESSAGE_SWITCH_ENDSWITCH_TAG = 2;
	public static final int MESSAGE_IF_ENDIF_TAG = 3;
	public static final int MESSAGE_FOR_ENDFOR_TAG = 4;
	public static final int MESSAGE_WHILE_ENDWHILE_TAG = 5;
	public static final int MESSAGE_FOREACH_ENDFOREACH_TAG = 6;
	public static final int MESSAGE_UNUSED_PARAMETERS = 7;
	public static final int MESSAGE_VARIABLE_MAY_BE_UNASSIGNED = 8;
	public static final int MESSAGE_UNNECESSARY_GLOBAL = 9;

	/**
	 * message for syntax case '' ;.
	 */
	public static final int MESSAGE_CASE_SEMICOLON = 10;
	public static final int MESSAGE_DEPRECATED_VAR_TOKEN = 11;
	public static final int MESSAGE_CONDITIONAL_EXPRESSION_CHECK = 12;
	public static final int MESSAGE_METHOD_FIELD_WITH_SAME_NAME = 13;
	public static final int MESSAGE_PHP_CLOSING_MISSING = 14;
	public static final int MESSAGE_DOUBLE_DOLLAR = 15;

	private int messageClass;
	private int level;

	private final String path;
	private int beginLine;
	private int beginColumn;
	private int endLine;
	private int endColumn;

	private int sourceStart, sourceEnd;

	private String message;

	public PHPParseMessageEvent(int level,
				    int messageClass,
				    String path,
				    String message,
				    int sourceStart,
				    int sourceEnd,
				    int beginLine,
				    int endLine,
				    int beginColumn,
				    int endColumn)
	{
		this.level = level;
		this.messageClass = messageClass;
		this.path = path;
		this.beginLine = beginLine;
		this.message = message;
		this.beginColumn = beginColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
	}

	/**
	 * Create a parse message with a Token.
	 *
	 * @param level	the message level
	 * @param messageClass the class of message
	 * @param path	 the path of the source file
	 * @param message      the message
	 * @param token	the token
	 */
	public PHPParseMessageEvent(int level,
				    int messageClass,
				    String path,
				    String message,
				    Token token)
	{
		this(level,
			messageClass,
			path,
			message,
			token.sourceStart,
			token.sourceEnd,
			token.beginLine,
			token.endLine,
			token.beginColumn,
			token.endColumn);
	}

	public int getLevel()
	{
		return level;
	}

	public int getBeginLine()
	{
		return beginLine;
	}

	public int getBeginColumn()
	{
		return beginColumn;
	}

	public int getEndLine()
	{
		return endLine;
	}

	public int getEndColumn()
	{
		return endColumn;
	}

	public int getSourceStart()
	{
		return sourceStart;
	}

	public int getSourceEnd()
	{
		return sourceEnd;
	}

	public String getMessage()
	{
		return message;
	}

	public String getPath()
	{
		return path;
	}

	public int getMessageClass()
	{
		return messageClass;
	}
}
