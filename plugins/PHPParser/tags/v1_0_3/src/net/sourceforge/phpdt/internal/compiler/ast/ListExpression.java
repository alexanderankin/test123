package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A list expression.
 * it could be list($v1,$v2), list(,$v2) ...
 * @author Matthieu Casanova
 */
public final class ListExpression extends Expression {

  private final Expression[] vars;
  private Expression expression;

  public ListExpression(final Expression[] vars,
                        final Expression expression,
                        final int sourceStart,
                        final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.vars = vars;
    this.expression = expression;
  }

  public ListExpression(final Expression[] vars,
                        final int sourceStart,
                        final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.vars = vars;
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    final StringBuffer buff = new StringBuffer("list(");
    for (int i = 0; i < vars.length; i++) {
      if (i != 0) {
        buff.append(", ");
      }
      if (vars[i] != null) {
        buff.append(vars[i].toStringExpression());
      }
    }
    if (expression != null) {
      buff.append(" = ");
      buff.append(expression.toStringExpression());
    }
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
    for (int i = 0; i < vars.length; i++) {
      if (vars[i] != null) {
        vars[i].getUsedVariable(list);
      }
    }
    if (expression != null) {
      expression.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    if (expression != null) {
      expression.getUsedVariable(list);
    }
  }
}
