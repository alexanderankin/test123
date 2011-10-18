/*
 * ConditionalExpression.java 
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
import gatchan.phpparser.parser.PHPParseMessageEvent;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * A ConditionalExpression is like that : booleanExpression ? trueValue : falseValue;.
 *
 * @author Matthieu Casanova
 */
public class ConditionalExpression extends OperatorExpression
{
	private final Expression condition;
	private final Expression valueIfTrue;
	private final Expression valueIfFalse;

	public ConditionalExpression(Expression condition,
				     Expression valueIfTrue,
				     Expression valueIfFalse)
	{
		super(valueIfFalse.getType(), // we use the valueIfTrue type
			-1,
			condition.getSourceStart(),
			valueIfFalse.getSourceEnd(),
			condition.getBeginLine(),
			valueIfFalse.getEndLine(),
			condition.getBeginColumn(),
			valueIfFalse.getEndColumn());
		this.condition = condition;
		if (valueIfTrue == null)
			this.valueIfTrue = condition;
		else
			this.valueIfTrue = valueIfTrue;
		this.valueIfFalse = valueIfFalse;
	}

	@Override
	public String toStringExpression()
	{
		String conditionString = condition.toStringExpression();
		String valueIfTrueString = valueIfTrue.toStringExpression();
		String valueIfFalse = this.valueIfFalse.toStringExpression();
		StringBuilder buff = new StringBuilder(8 +
			conditionString.length() +
			valueIfTrueString.length() +
			valueIfFalse.length());
		buff.append('(');
		buff.append(conditionString);
		buff.append(") ? ");
		buff.append(valueIfTrueString);
		buff.append(" : ");
		buff.append(valueIfFalse);
		return buff.toString();
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
		condition.getModifiedVariable(list);
		valueIfTrue.getModifiedVariable(list);
		valueIfFalse.getModifiedVariable(list);
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		condition.getUsedVariable(list);
		valueIfTrue.getUsedVariable(list);
		valueIfFalse.getUsedVariable(list);
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (condition.isAt(line, column)) return condition;
		if (valueIfTrue.isAt(line, column)) return valueIfTrue;
		if (valueIfFalse.isAt(line, column)) return valueIfFalse;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		if (valueIfFalse.equals(valueIfTrue))
		{
			parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
				PHPParseMessageEvent.MESSAGE_CONDITIONAL_EXPRESSION_CHECK,
				parser.getPath(),
				"Conditional expression : silly expression, the result is always the same",
				sourceStart,
				sourceEnd,
				beginLine,
				endLine,
				beginColumn,
				endColumn));
			return;
		}
		Type typeFalse = valueIfFalse.getType();
		Type typeTrue = valueIfTrue.getType();
		if (typeFalse != typeTrue && !typeFalse.isEmpty() && !typeTrue.isEmpty())
		{
			parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
				PHPParseMessageEvent.MESSAGE_CONDITIONAL_EXPRESSION_CHECK,
				parser.getPath(),
				"Conditional expression : warning, the true value is type " + typeTrue + " and the false value is " + typeFalse,
				sourceStart,
				sourceEnd,
				beginLine,
				endLine,
				beginColumn,
				endColumn));
		}
	}
}
