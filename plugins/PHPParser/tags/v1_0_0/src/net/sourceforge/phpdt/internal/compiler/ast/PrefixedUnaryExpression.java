package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * @author Matthieu Casanova
 */
public class PrefixedUnaryExpression extends UnaryExpression {

  public PrefixedUnaryExpression(final Expression expression,
                                 final int operator,
                                 final int sourceStart) {
    super(expression, operator, sourceStart, expression.sourceEnd);
  }

  public String toStringExpression() {
    return operatorToString() + expression.toStringExpression();
  }
}
