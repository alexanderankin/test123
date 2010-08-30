package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * An empty statement.
 *
 * @author Matthieu Casanova
 */
public class EmptyStatement extends Statement
{

	public EmptyStatement(int sourceStart, int sourceEnd, int beginLine, int endLine, int beginColumn, int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	@Override
	public String toString(int tab)
	{
		return tabString(tab) + ';';
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
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}
}
