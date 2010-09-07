package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * Here is the Superclass of the Literal expressions.
 *
 * @author Matthieu Casanova
 */
public abstract class Literal extends Expression
{

	protected Literal(Type type, int sourceStart, int sourceEnd, int beginLine, int endLine, int beginColumn, int endColumn)
	{
		super(type, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public final void getOutsideVariable(List<VariableUsage> list)
	{
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public final void getModifiedVariable(List<VariableUsage> list)
	{
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
		return isAt(line, column) ? this : null;
	}
}
