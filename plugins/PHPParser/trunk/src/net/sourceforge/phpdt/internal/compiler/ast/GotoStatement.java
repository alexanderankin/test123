/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
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

package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.WarningMessageClass;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

/**
 * an goto statement.
 * goto labelidentifier;
 *
 * @author Matthieu Casanova
 */
public class GotoStatement extends Statement
{
	private final ConstantIdentifier label;

	public GotoStatement(Token gotoToken, ConstantIdentifier label, int sourceEnd, int endLine, int endColumn)
	{
		super(gotoToken.sourceStart, sourceEnd, gotoToken.beginLine, endLine, gotoToken.beginColumn, endColumn);
		this.label = label;
	}

	public ConstantIdentifier getName()
	{
		return label;
	}

	public String toString()
	{
		StringBuilder buff = new StringBuilder("goto ");
		buff.append(label).append(';');
		return buff.toString();
	}

	/**
	 * Return the object into String.
	 *
	 * @param tab how many tabs (not used here
	 * @return a String
	 */
	@Override
	public String toString(int tab)
	{
		String tabs = tabString(tab);
		String str = toString();
		return tabs + str;
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (label != null && label.isAt(line, column))
			return label;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		parser.fireParseMessage(
			new PHPParseMessageEvent(PHPParser.WARNING, WarningMessageClass.gotostatement,
						 parser.getPath(),
						 "use of a goto statement",
						 getSourceStart(), label.getSourceEnd(), getBeginLine(),
						 label.getEndLine(), getBeginColumn(), label.getEndColumn()));
	}
}
