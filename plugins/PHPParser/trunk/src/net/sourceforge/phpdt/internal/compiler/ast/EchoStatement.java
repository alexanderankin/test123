package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * an echo statement.
 * echo something;
 * 
 * @author Matthieu Casanova
 */
public final class EchoStatement extends Statement {

  /** An array of expressions in this echo statement. */
  private final Expression[] expressions;

  public EchoStatement(final Expression[] expressions,
                       final int sourceStart,
                       final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart, sourceEnd,beginLine,endLine,beginColumn,endColumn);
    this.expressions = expressions;
  }

  public String toString() {
    final StringBuffer buff = new StringBuffer("echo ");//$NON-NLS-1$
    for (int i = 0; i < expressions.length; i++) {
      if (i != 0) {
        buff.append(", ");//$NON-NLS-1$
      }
      buff.append(expressions[i].toStringExpression());
    }
    return buff.toString();
  }

  /**
   * Return the object into String.
   * 
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final String tabs = tabString(tab);
    final String str = toString();
    final StringBuffer buff = new StringBuffer(tabs.length() + str.length());
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    for (int i = 0; i < expressions.length; i++) {
      expressions[i].getOutsideVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    for (int i = 0; i < expressions.length; i++) {
      expressions[i].getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    for (int i = 0; i < expressions.length; i++) {
      expressions[i].getUsedVariable(list);
    }
  }
}
