package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * @author Matthieu Casanova
 */
public class PrefixedUnaryExpression extends UnaryExpression {


  public PrefixedUnaryExpression(final Expression expression,
                                 final int operator,
                                 final int sourceStart,
                                 final int beginLine,
                                 final int beginColumn) {
    super(expression, operator, sourceStart, expression.sourceEnd,beginLine,expression.getEndLine(),beginColumn,expression.getEndColumn());
  }

  public String toStringExpression() {
    return operatorToString() + expression.toStringExpression();
  }
}
