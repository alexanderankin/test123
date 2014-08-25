package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public abstract class UnaryExpression extends OperatorExpression
{

	protected final Expression expression;

	protected UnaryExpression(Expression expression,
				  int operator,
				  int sourceStart,
				  int sourceEnd,
				  int beginLine,
				  int endLine,
				  int beginColumn,
				  int endColumn)
	{
		super(expression.getType(), operator, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.expression = expression;
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
		expression.getModifiedVariable(list);
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public final void getUsedVariable(List<VariableUsage> list)
	{
		expression.getUsedVariable(list);
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		return expression.isAt(line, column) ? expression : this;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		expression.analyzeCode(parser);
	}
}
