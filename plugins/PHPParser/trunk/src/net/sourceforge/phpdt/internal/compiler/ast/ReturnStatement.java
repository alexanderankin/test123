package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A return statement.
 * @author Matthieu Casanova
 */
public final class ReturnStatement extends Statement {
  private final Statement expression;

  /**
   * @deprecated
   * @param expression
   * @param sourceStart
   * @param sourceEnd
   */
  public ReturnStatement(final Statement expression, final int sourceStart, final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.expression = expression;
  }

  public ReturnStatement(Statement expression,
                         final int sourceStart,
                         final int sourceEnd,
                         final int beginLine,
                         final int endLine,
                         final int beginColumn,
                         final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.expression = expression;
  }

  public String toString(final int tab) {
    final String s = tabString(tab);
    if (expression == null) {
      return s + "return";//$NON-NLS-1$
    }
    return s + "return " + expression.toString();//$NON-NLS-1$
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) { }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
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
