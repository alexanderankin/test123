package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public final class SwitchStatement extends Statement {

  private final Expression variable;
  private final AbstractCase[] cases;

  public SwitchStatement(final Expression variable,
                         final AbstractCase[] cases,
                         final int sourceStart,
                         final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.variable = variable;
    this.cases = cases;
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("switch (").append(variable.toStringExpression()).append(") {\n");
    for (int i = 0; i < cases.length; i++) {
      final AbstractCase cas = cases[i];
      buff.append(cas.toString(tab + 1));
      buff.append('\n');
    }
    buff.append('}');
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    for (int i = 0; i < cases.length; i++) {
      cases[i].getOutsideVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    for (int i = 0; i < cases.length; i++) {
      cases[i].getModifiedVariable(list);
    }
    variable.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    for (int i = 0; i < cases.length; i++) {
      cases[i].getUsedVariable(list);
    }
    variable.getUsedVariable(list);
  }
}
