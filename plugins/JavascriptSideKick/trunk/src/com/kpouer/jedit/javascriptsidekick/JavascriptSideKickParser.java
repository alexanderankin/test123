/*
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2012 Matthieu Casanova
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
package com.kpouer.jedit.javascriptsidekick;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.util.Log;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

/**
 * @author Matthieu Casanova
 */
public class JavascriptSideKickParser extends SideKickParser
{
	public JavascriptSideKickParser()
	{
		super("rhinojs");
	}

	@Override
	public SideKickParsedData parse(final Buffer buffer, final DefaultErrorSource errorSource)
	{
		Parser parser = new Parser(new CompilerEnvirons(), new ErrorReporter()
		{
			@Override
			public void warning(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				errorSource.addError(ErrorSource.WARNING, buffer.getPath(),line, lineOffset, lineOffset, message);
			}

			@Override
			public void error(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				errorSource.addError(ErrorSource.ERROR, buffer.getPath(),line, lineOffset, lineOffset, message);
			}

			@Override
			public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				return new EvaluatorException(message, sourceName, line);
			}
		});
		try
		{
			parser.parse(buffer.getText(), null, 0);
		}
		catch (Exception e)
		{
		}
		SideKickParser jsparser = ServiceManager.getService(SideKickParser.class, "javascript");
		if (jsparser != null)
		{
			return jsparser.parse(buffer, errorSource);
		}
		return new SideKickParsedData(buffer.getName());
	}

	public SideKickParsedData _parse(final Buffer buffer, final DefaultErrorSource errorSource)
	{
		SideKickParsedData data = null;
		SideKickParser jsparser = ServiceManager.getService(SideKickParser.class, "javascript");
		if (jsparser != null)
		{
			data = jsparser.parse(buffer, errorSource);
		}
		Parser parser = new Parser(new CompilerEnvirons(), new ErrorReporter()
		{
			@Override
			public void warning(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				errorSource.addError(ErrorSource.WARNING, buffer.getPath(),line, lineOffset, lineOffset, message);
			}

			@Override
			public void error(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				errorSource.addError(ErrorSource.ERROR, buffer.getPath(),line, lineOffset, lineOffset, message);
			}

			@Override
			public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				return new EvaluatorException(message, sourceName, line);
			}
		});
		parser.parse(buffer.getText(), null, 0);

		return data;
	}
}
