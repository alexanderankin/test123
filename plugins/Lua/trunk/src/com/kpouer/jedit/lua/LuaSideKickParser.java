/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2013 Matthieu Casanova
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
package com.kpouer.jedit.lua;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;
import org.luaj.vm2.parser.TokenMgrError;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

/**
 * @author Matthieu Casanova
 */
public class LuaSideKickParser extends SideKickParser
{
	private Collection<String> functionList = new HashSet<String>();
	public LuaSideKickParser()
	{
		super("Lua");
		BufferedReader reader = null;
		try
		{
			InputStream functionlist = LuaSideKickParser.class.getResourceAsStream("/functionlist");
			reader = new BufferedReader(new InputStreamReader(functionlist));
			loadFunctionList(reader);
		}
		finally
		{
			IOUtilities.closeQuietly((Closeable) reader);
		}
	}

	private void loadFunctionList(BufferedReader reader)
	{
		try
		{
			String line = reader.readLine();
			while (line != null)
			{
				if (!line.isEmpty())
				{
					functionList.add(line);
				}
				line = reader.readLine();
			}
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	@Override
	public SideKickParsedData parse(final Buffer buffer, final DefaultErrorSource errorSource)
	{
		LuaValue.valueOf(true);
		LuaParser parser = new MyLuaParser(new StringReader(buffer.getText()));
		final Set<String> localFunctionList = new HashSet<String>();
		try
		{
			Chunk chunk = parser.Chunk();
			SideKickParsedData sideKickParsedData = new SideKickParsedData(buffer.getName());
			final DefaultMutableTreeNode root = sideKickParsedData.root;

			chunk.accept( new Visitor()
			{
				@Override
				public void visit(Stat.FuncDef funcDef)
				{
					try
					{
						Position startPosition = createPosition(buffer, funcDef.beginLine-1, funcDef.beginColumn-1);
						Position endPosition = createPosition(buffer, funcDef.endLine-1, funcDef.endColumn-1);
						ParList parlist = funcDef.body.parlist;

						StringBuilder buider = new StringBuilder(funcDef.name.name.name);
						for (Object dot : funcDef.name.dots)
						{
							buider.append('.').append(dot);
						}
						String functionName = buider.toString();
						FunctionAsset asset = new FunctionAsset(functionName, startPosition, endPosition, parlist);
						root.add(new DefaultMutableTreeNode(asset));
						localFunctionList.add(functionName);
					}
					catch (Exception e)
					{
						Log.log(Log.ERROR,  this, e);
					}
					super.visit(funcDef);
				}

				@Override
				public void visit(Stat.LocalFuncDef localFuncDef)
				{
					try
					{
						Position startPosition = createPosition(buffer, localFuncDef.beginLine-1, localFuncDef.beginColumn-1);
						Position endPosition = createPosition(buffer, localFuncDef.endLine-1, localFuncDef.endColumn-1);
						ParList parlist = localFuncDef.body.parlist;

						FunctionAsset asset = new FunctionAsset(localFuncDef.name.name + " (local)", startPosition, endPosition, parlist);
						localFunctionList.add(localFuncDef.name.name);
						root.add(new DefaultMutableTreeNode(asset));
					}
					catch (Exception e)
					{
						Log.log(Log.ERROR,  this, e);
					}
					super.visit(localFuncDef);
				}

				private Position createPosition(JEditBuffer buffer, int line, int column)
				{
					int lineStartOffset = buffer.getLineStartOffset(line);
					return buffer.createPosition(lineStartOffset + column);
				}

				@Override
				public void visit(Exp.FuncCall funcCall)
				{
					Log.log(Log.DEBUG, this, "funcCall = " + funcCall);
					if (funcCall.lhs instanceof Exp.FieldExp)
					{
						Exp.FieldExp field = (Exp.FieldExp) funcCall.lhs;
						Exp.NameExp prefixExpr = (Exp.NameExp) field.lhs;
						String prefix = prefixExpr.name.name;
						String suffix = field.name.name;

						String functionName = prefix + '.' + suffix;
						if (!functionList.contains(functionName) && !localFunctionList.contains(functionName))
						{
							errorSource.addError(ErrorSource.ERROR, buffer.getPath(),
												 prefixExpr.beginLine-1,
												 prefixExpr.beginColumn-1,
												 field.endColumn,
												 "Error this function " + functionName +" doesn't exist");
						}
					}
					super.visit(funcCall);
				}
			});

			return sideKickParsedData;
		}
		catch (ParseException e)
		{
			errorSource.addError(ErrorSource.ERROR, buffer.getPath(), e.currentToken.next.beginLine - 1,
								 e.currentToken.next.beginColumn - 1, e.currentToken.next.endColumn,
								 e.getLocalizedMessage());
		}
		catch (TokenMgrError e)
		{
			e.printStackTrace();
			errorSource.addError(ErrorSource.ERROR, buffer.getPath(), 0, 0, 0, e.getLocalizedMessage());
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		return new SideKickParsedData(buffer.getName());
	}

	private static class MyLuaParser extends LuaParser
	{
		private MyLuaParser(Reader reader)
		{
			super(reader);
		}

		@Override
		public ParseException generateParseException()
		{
			ParseException parseException = super.generateParseException();
			parseException.currentToken = token;
			return parseException;
		}
	}
}
