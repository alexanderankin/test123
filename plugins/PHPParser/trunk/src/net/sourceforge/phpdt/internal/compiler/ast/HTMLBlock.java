/*
* HTMLBlock.java
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2003, 2010 Matthieu Casanova
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
 * @author Matthieu Casanova
 */
public class HTMLBlock extends Statement
{
	private final AstNode[] nodes;

	//{{{ HTMLBlock constructor
	public HTMLBlock(AstNode[] nodes)
	{
		super(nodes[0].getSourceStart(),
			nodes[nodes.length > 0 ? nodes.length - 1 : 0].getSourceEnd(),
			nodes[0].getBeginLine(),
			nodes[nodes.length > 0 ? nodes.length - 1 : 0].getEndLine(),
			nodes[0].getBeginColumn(),
			nodes[nodes.length > 0 ? nodes.length - 1 : 0].getEndColumn());
		this.nodes = nodes;
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
		StringBuilder buff = new StringBuilder(tabString(tab));
		buff.append("?>");
		for (int i = 0; i < nodes.length; i++)
		{
			buff.append(nodes[i].toString(tab + 1));
		}
		buff.append("<?php\n");
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
		for (AstNode node : nodes)
			node.getUsedVariable(list);
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
