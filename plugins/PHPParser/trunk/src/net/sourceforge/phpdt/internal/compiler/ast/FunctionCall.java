/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003-2010 Matthieu Casanova
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

import gatchan.phpparser.PHPParserPlugin;
import gatchan.phpparser.methodlist.Argument;
import gatchan.phpparser.methodlist.Function;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.WarningMessageClass;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * A Function call.
 *
 * @author Matthieu Casanova
 */
public class FunctionCall extends AbstractSuffixExpression
{
	/**
	 * the function name.
	 */
	private final Expression functionName;

	/**
	 * the arguments.
	 */
	private final ArgumentList args;

	private Function definition;

	public FunctionCall(Expression functionName, ArgumentList args)
	{
		super(Type.UNKNOWN, functionName.getSourceStart(), args.getSourceEnd(), functionName.getBeginLine(),
		      args.getEndLine(), functionName.getBeginColumn(), args.getEndColumn());
		this.functionName = functionName;
		this.args = args;
		if (functionName instanceof ConstantIdentifier)
		{
			definition = PHPParserPlugin.phpMethodList.getFunction(getFunctionName().toString());
			if (definition != null)
				setType(definition.getReturnType());
		}
	}

	public Expression getFunctionName()
	{
		return functionName;
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		StringBuilder buff = new StringBuilder(functionName.toStringExpression());
		buff.append(args.toString());
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
		if (definition == null)
			args.getModifiedVariable(list);
		else
		{
			Expression[] arguments = args.getArgs();
			if (arguments == null)
				return;
			for (int i = 0; i < arguments.length; i++)
			{
				Expression argument = arguments[i];
				Argument argDef = definition.getArgument(i);
				if (argDef != null && argDef.isReference())
				{
					argument.getUsedVariable(list);
				}
				else
				{
					argument.getModifiedVariable(list);
				}
			}
		}
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		functionName.getUsedVariable(list);
		if (definition == null)
			args.getUsedVariable(list);
		else
		{
			Expression[] arguments = args.getArgs();
			if (arguments == null)
				return;
			for (int i = 0; i < arguments.length; i++)
			{
				Expression argument = arguments[i];
				Argument argDef = definition.getArgument(i);
				if (argDef == null || !argDef.isReference())
				{
					argument.getUsedVariable(list);
				}
			}
		}
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (functionName.isAt(line, column))
			return functionName;
		return args.expressionAt(line, column);
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		if (definition == null)
			return;
		Expression[] arguments = args.getArgs();
		for (int i = 0; i < arguments.length; i++)
		{
			Expression argument = arguments[i];
			Argument defArgument = definition.getArgument(i);
			if (defArgument == null)
			{
				if (!definition.isVarargs())
				{
					parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
											 WarningMessageClass.tooManyArguments,
											 parser.getPath(),
											 "Too many arguments",
											 argument.getSourceStart(),
											 argument.getSourceEnd(),
											 argument.getBeginLine(),
											 argument.getEndLine(),
											 argument.getBeginColumn(),
											 argument.getEndColumn()));
				}
				break;
			}
			else
			{
				if (!defArgument.getType().isCompatibleWith(argument.getType()))
				{
					parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
											 WarningMessageClass.wrongArgumentType,
											 parser.getPath(),
											 "wrong argument type, expected "
											 + defArgument.getType()
											 + " got " + argument.getType(),
											 argument.getSourceStart(),
											 argument.getSourceEnd(),
											 argument.getBeginLine(),
											 argument.getEndLine(),
											 argument.getBeginColumn(),
											 argument.getEndColumn()));
				}
			}
		}
	}
}
