package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
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
	private final Expression[] args;

	private static final long serialVersionUID = 1510966294246368800L;


	public FunctionCall(Expression functionName,
			    Expression[] args,
			    int sourceEnd,
			    int endLine,
			    int endColumn)
	{
		super(Type.UNKNOWN,
			functionName.getSourceStart(),
			sourceEnd,
			functionName.getBeginLine(),
			endLine,
			functionName.getBeginColumn(),
			endColumn);
		this.functionName = functionName;
		this.args = args;
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
		buff.append('(');
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
			{
				Expression arg = args[i];
				if (i != 0)
				{
					buff.append(',');
				}
				buff.append(arg.toStringExpression());
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
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
			{
				args[i].getModifiedVariable(list);
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
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
			{
				args[i].getUsedVariable(list);
			}
		}
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		if (functionName.isAt(line, column)) return functionName;
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
			{
				Expression arg = args[i];
				if (arg.isAt(line, column)) return arg;
			}
		}
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}
}
