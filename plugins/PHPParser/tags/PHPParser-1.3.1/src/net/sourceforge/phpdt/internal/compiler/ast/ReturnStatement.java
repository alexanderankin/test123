package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * A return statement.
 *
 * @author Matthieu Casanova
 */
public class ReturnStatement extends Statement
{
	private final Statement expression;

	public ReturnStatement(Statement expression,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.expression = expression;
	}

	@Override
	public String toString(int tab)
	{
		String s = tabString(tab);
		if (expression == null)
		{
			return s + "return";//$NON-NLS-1$
		}
		return s + "return " + expression.toString();//$NON-NLS-1$
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
		if (expression != null)
		{
			expression.getModifiedVariable(list);
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
		if (expression != null)
		{
			expression.getUsedVariable(list);
		}
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		if (expression.isAt(line, column)) return expression.expressionAt(line, column);
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		if (expression != null)
		{
			expression.analyzeCode(parser);
		}
	}
}
