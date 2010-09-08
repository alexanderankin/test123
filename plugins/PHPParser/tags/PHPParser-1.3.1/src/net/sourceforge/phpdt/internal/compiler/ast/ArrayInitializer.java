package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * an array initializer. array('a','b','c') or array('a' => 2,'b' = '3');
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class ArrayInitializer extends Expression
{
	/**
	 * the key and values. The last value can be null because of <code>syntax array('bar',)</code>
	 */
	private final ArrayVariableDeclaration[] vars;

	/**
	 * Create a new array initializer.
	 *
	 * @param vars	the keys and values of the array
	 * @param sourceStart the starting offset
	 * @param sourceEnd   the ending offset
	 * @param beginLine   begin line
	 * @param endLine     ending line
	 * @param beginColumn begin column
	 * @param endColumn   ending column
	 */
	public ArrayInitializer(ArrayVariableDeclaration[] vars,
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
		StringBuilder buff = new StringBuilder("array(");
		for (int i = 0; i < vars.length; i++)
		{
			if (i != 0)
			{
				buff.append(',');
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
				vars[i].getModifiedVariable(list);
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
		for (int i = 0; i < vars.length; i++)
		{
			if (vars[i] != null)
			{
				vars[i].getUsedVariable(list);
			}
		}
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		for (int i = 0; i < vars.length; i++)
		{
			ArrayVariableDeclaration var = vars[i];
			if (var.isAt(line, column)) return var;
		}
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		for (int i = 0; i < vars.length; i++)
		{
			if (vars[i] != null)
				vars[i].analyzeCode(parser);
		}
	}
}
