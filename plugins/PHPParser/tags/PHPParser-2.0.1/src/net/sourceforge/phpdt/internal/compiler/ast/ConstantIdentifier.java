/*
* ConstantIdentifier.java
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2003, 2012 Matthieu Casanova
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

//{{{ Imports
import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;
//}}}

/**
 * @author Matthieu Casanova
 */
public class ConstantIdentifier extends Expression
{
	private final String name;

	//{{{ ConstantIdentifier constructor
	public ConstantIdentifier(String name,
				  int sourceStart,
				  int sourceEnd,
				  int beginLine,
				  int endLine,
				  int beginColumn,
				  int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.name = name;
	}

	public ConstantIdentifier(Token token)
	{
		super(Type.UNKNOWN, token.sourceStart, token.sourceEnd, token.beginLine, token.endLine, token.beginColumn, token.endColumn);
		name = token.image;
	} //}}}

	//{{{ toStringExpression()
	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		return name;
	} //}}}

	//{{{ toString()
	public String toString()
	{
		return name;
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
	} //}}}

	//{{{ subNodeAt() method
	@Override
	public AstNode subNodeAt(int line, int column)
	{
		return null;
	} //}}}

	//{{{ analyzeCode() method
	@Override
	public void analyzeCode(PHPParser parser)
	{
	} //}}}
}
