package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A GlobalStatement statement in php.
 * @author Matthieu Casanova
 */
public final class StaticStatement extends Statement {

  /** An array of the variables called by this global statement. */
  private final VariableDeclaration[] variables;

  public StaticStatement(final VariableDeclaration[] variables, final int sourceStart, final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart, sourceEnd,beginLine,endLine,beginColumn,endColumn);
    this.variables = variables;
  }

  public String toString() {
    final StringBuffer buff = new StringBuffer("static ");
    for (int i = 0; i < variables.length; i++) {
      if (i != 0) {
        buff.append(", ");
      }
      buff.append(variables[i]);
    }
    return buff.toString();
  }

  public String toString(final int tab) {
    return tabString(tab) + toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    for (int i = 0; i < variables.length; i++) {
      variables[i].getModifiedVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
  }
}
