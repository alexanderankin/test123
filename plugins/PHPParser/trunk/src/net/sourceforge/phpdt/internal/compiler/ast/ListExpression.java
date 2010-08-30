package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * A list expression. it could be list($v1,$v2), list(,$v2) ...
 *
 * @author Matthieu Casanova
 */
public class ListExpression extends Expression
{
	private final Expression[] vars;

	public ListExpression(Expression[] vars,
			      int sourceStart,
			      int sourceEnd,
			      int beginLine,
			      int endLine,
			      int beginColumn,
			      int endColumn)
	{
		super(Type.ARRAY, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.vars = vars;
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		StringBuilder buff = new StringBuilder("list(");
		for (int i = 0; i < vars.length; i++)
		{
			if (i != 0)
			{
				buff.append(", ");
			}
			if (vars[i] != null)
			{
				buff.append(vars[i].toStringExpression());
			}
		}
		buff.append(')');
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
		for (int i = 0; i < vars.length; i++)
		{
			if (vars[i] != null)
			{
				vars[i].getUsedVariable(list);
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
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		for (int i = 0; i < vars.length; i++)
		{
			Expression var = vars[i];
			if (var != null && var.isAt(line, column)) return var;
		}
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		for (int i = 0; i < vars.length; i++)
		{
			vars[i].analyzeCode(parser);
		}
	}
}
