package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * @author Matthieu Casanova
 */
public final class PostfixedUnaryExpression extends UnaryExpression {

  public PostfixedUnaryExpression(final Expression expression,
                                  final int operator,
                                  final int sourceEnd) {
    super(expression, operator, expression.sourceStart, sourceEnd);
  }

  public String toStringExpression() {
    return expression.toStringExpression() + operatorToString();
  }
}
