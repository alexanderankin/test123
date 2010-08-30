/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2009 Matthieu Casanova
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

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

import java.util.List;

import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParserConstants;
import gatchan.phpparser.parser.PHPParser;
import sidekick.IAsset;

import javax.swing.text.Position;
import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public class InclusionExpression extends Expression implements Outlineable, IAsset
{
	private boolean silent;
	/**
	 * The kind of include.
	 */
	private final int keyword;
	private final Expression expression;

	private final transient OutlineableWithChildren parent;

	private transient Position start;
	private transient Position end;
	private static transient Icon icon;
	private String cachedToString;

	//{{{ InclusionExpression constructor

	public InclusionExpression(OutlineableWithChildren parent,
				   int keyword,
				   Expression expression,
				   int sourceStart,
				   int sourceEnd,
				   int beginLine,
				   int endLine,
				   int beginColumn,
				   int endColumn)
	{
		super(Type.INTEGER, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.keyword = keyword;
		this.expression = expression;
		this.parent = parent;
	} //}}}

	//{{{ keywordToString() method

	private String keywordToString()
	{
		return PHPParserConstants.tokenImage[keyword];
	} //}}}

	//{{{ toStringExpression() method

	@Override
	public String toStringExpression()
	{
		return toString();
	} //}}}

	//{{{ toString() method

	public String toString()
	{
		if (cachedToString == null)
		{
			String keyword = keywordToString();
			keyword = keyword.substring(1, keyword.length() - 1);
			String expressionString = expression.toStringExpression();
			StringBuilder buffer = new StringBuilder(keyword.length() +
				expressionString.length() + 2);
			if (silent)
			{
				buffer.append('@');
			}
			buffer.append(keyword);
			buffer.append(' ');
			buffer.append(expressionString);
			cachedToString = buffer.toString();
		}
		return cachedToString;
	} //}}}

	//{{{ getParent() method

	public OutlineableWithChildren getParent()
	{
		return parent;
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
		expression.getOutsideVariable(list);
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
		expression.getModifiedVariable(list);
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
		expression.getUsedVariable(list);
	} //}}}

	//{{{ getName() method

	public String getName()
	{
		//todo : change this
		return null;
	} //}}}

	//{{{ getItemType() method

	public int getItemType()
	{
		return PHPItem.INCLUDE;
	} //}}}

	//{{{ getEnd() method

	public Position getEnd()
	{
		return end;
	} //}}}

	//{{{ setEnd() method

	public void setEnd(Position end)
	{
		this.end = end;
	} //}}}

	//{{{ getStart() method

	public Position getStart()
	{
		return start;
	} //}}}

	//{{{ setStart() method

	public void setStart(Position start)
	{
		this.start = start;
	} //}}}

	//{{{ getIcon() method

	public Icon getIcon()
	{
		if (icon == null)
		{
			icon = new ImageIcon(InclusionExpression.class.getResource("/gatchan/phpparser/icons/require.png"));
		}
		return icon;
	} //}}}

	//{{{ getShortString() method

	public String getShortString()
	{
		return toString();
	} //}}}

	//{{{ getLongString() method

	public String getLongString()
	{
		return toString();
	} //}}}

	//{{{ setName() method

	public void setName(String name)
	{
	} //}}}

	//{{{ expressionAt() method

	@Override
	public Expression expressionAt(int line, int column)
	{
		return expression.isAt(line, column) ? expression : null;
	} //}}}

	//{{{ analyzeCode() method

	@Override
	public void analyzeCode(PHPParser parser)
	{
		expression.analyzeCode(parser);
	} //}}}
}
