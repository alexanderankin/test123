package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public final class PrintExpression extends Expression {

  private final Expression expression;

  public PrintExpression(final Expression expression, final int sourceStart, final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.expression = expression;
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    return "print " + expression.toStringExpression();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    expression.getOutsideVariable(list);
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    expression.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    expression.getUsedVariable(list);
  }
}
