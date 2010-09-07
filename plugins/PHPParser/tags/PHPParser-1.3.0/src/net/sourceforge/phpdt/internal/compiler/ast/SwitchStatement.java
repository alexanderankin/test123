package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class SwitchStatement extends Statement
{

	private final Expression variable;
	private final AbstractCase[] cases;

	public SwitchStatement(Expression variable,
			       AbstractCase[] cases,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.variable = variable;
		this.cases = cases;
	}

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
		buff.append("switch (").append(variable.toStringExpression()).append(") {\n");
		for (int i = 0; i < cases.length; i++)
		{
			AbstractCase cas = cases[i];
			buff.append(cas.toString(tab + 1));
			buff.append('\n');
		}
		buff.append('}');
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
		for (int i = 0; i < cases.length; i++)
		{
			cases[i].getOutsideVariable(list);
		}
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		for (int i = 0; i < cases.length; i++)
		{
			cases[i].getModifiedVariable(list);
		}
		variable.getModifiedVariable(list);
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		for (int i = 0; i < cases.length; i++)
		{
			cases[i].getUsedVariable(list);
		}
		variable.getUsedVariable(list);
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		if (variable.isAt(line, column)) return variable;
		for (int i = 0; i < cases.length; i++)
		{
			AbstractCase cas = cases[i];
			if (cas.isAt(line, column)) return cas.expressionAt(line, column);
		}
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		variable.analyzeCode(parser);
		for (int i = 0; i < cases.length; i++)
		{
			cases[i].analyzeCode(parser);
		}
	}
}
