/*
* PHPEchoBlock.java
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2003, 2009 Matthieu Casanova
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
package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * a php echo block. <?= someexpression ?>
 *
 * @author Matthieu Casanova
 */
public class PHPEchoBlock extends Statement
{
	/**
	 * the expression.
	 */
	private final Expression expr;

	//{{{ PHPEchoBlock constructor

	/**
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 * @param beginLine   begin line
	 * @param endLine     ending line
	 * @param beginColumn begin column
	 * @param endColumn   ending column
	 */
	public PHPEchoBlock(Expression expr,
			    int sourceStart,
			    int sourceEnd,
			    int beginLine,
			    int endLine,
			    int beginColumn,
			    int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.expr = expr;
	} //}}}

	//{{{ toString() method

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
		String expression = expr.toStringExpression();
		StringBuilder buff = new StringBuilder(tabs.length() +
			expression.length() +
			5);
		buff.append(tabs);
		buff.append("<?=");
		buff.append(expression);
		buff.append("?>");
		return buff.toString();
	} //}}}

	//{{{ getOutsideVariable() method

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	} //}}}

	//{{{ getModifiedVariable() method

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	} //}}}

	//{{{ getUsedVariable() method

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		expr.getUsedVariable(list);
	} //}}}

	//{{{ expressionAt() method

	@Override
	public Expression expressionAt(int line, int column)
	{
		return null;
	} //}}}

	//{{{ analyzeCode() method

	@Override
	public void analyzeCode(PHPParser parser)
	{
	} //}}}
}
