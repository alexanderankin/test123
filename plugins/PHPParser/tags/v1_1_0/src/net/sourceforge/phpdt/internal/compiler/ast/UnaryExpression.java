package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public abstract class UnaryExpression extends OperatorExpression {

  public final Expression expression;

  protected UnaryExpression(final Expression expression,
                            final int operator,
                            final int sourceStart,
                            final int sourceEnd,
                            final int beginLine,
                            final int endLine,
                            final int beginColumn,
                            final int endColumn) {
    super(operator, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.expression = expression;
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public final void getOutsideVariable(final List list) {
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public final void getModifiedVariable(final List list) {
    expression.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public final void getUsedVariable(final List list) {
    expression.getUsedVariable(list);
  }
}
