package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * @author Matthieu Casanova
 */
public class PostfixedUnaryExpression extends UnaryExpression
{


	public PostfixedUnaryExpression(Expression expression,
					int operator,
					int sourceEnd,
					int endLine,
					int endColumn)
	{
		super(expression, operator, expression.getSourceStart(), sourceEnd, expression.getBeginLine(), endLine, expression.getBeginColumn(), endColumn);
	}

	@Override
	public String toStringExpression()
	{
		return expression.toStringExpression() + operatorToString();
	}
}
