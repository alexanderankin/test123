package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * @author Matthieu Casanova
 */
public final class PostfixedUnaryExpression extends UnaryExpression {


  public PostfixedUnaryExpression(final Expression expression,
                                  final int operator,
                                  final int sourceEnd,
                                  final int endLine,
                                  final int endColumn) {
    super(expression, operator, expression.sourceStart, sourceEnd,expression.getBeginLine(),endLine,expression.getBeginColumn(),endColumn);
  }

  public String toStringExpression() {
    return expression.toStringExpression() + operatorToString();
  }
}
