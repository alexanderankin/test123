package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * This is a cast expression.
 * @author Matthieu Casanova
 */
public final class CastExpression extends Expression {

  /** The type in which we cast the expression. */
  private final ConstantIdentifier type;

  /** The expression to be casted. */
  private final Expression expression;

  /**
   * Create a cast expression.
   * @param type the type
   * @param expression the expression
   * @param sourceStart starting offset
   * @param sourceEnd ending offset
   */
  public CastExpression(final ConstantIdentifier type,
                        final Expression expression,
                        final int sourceStart,
                        final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.type = type;
    this.expression = expression;
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    final StringBuffer buff = new StringBuffer("(");
    buff.append(type.toStringExpression());
    buff.append(") ");
    buff.append(expression.toStringExpression());
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {}

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
