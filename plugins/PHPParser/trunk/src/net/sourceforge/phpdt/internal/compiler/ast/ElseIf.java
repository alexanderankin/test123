package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * An elseif statement.
 * @author Matthieu Casanova
 */
public final class ElseIf extends Statement {

  /** The condition. */
  private final Expression condition;

  /** The statements. */
  private final Statement[] statements;

  public ElseIf(final Expression condition, final Statement[] statements, final int sourceStart, final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.condition = condition;
    this.statements = statements;
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("elseif (");
    buff.append(condition.toStringExpression());
    buff.append(") \n");
    for (int i = 0; i < statements.length; i++) {
      final Statement statement = statements[i];
      buff.append(statement.toString(tab + 1)).append('\n');
    }
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    for (int i = 0; i < statements.length; i++) {
      statements[i].getModifiedVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    for (int i = 0; i < statements.length; i++) {
      statements[i].getModifiedVariable(list);
    }
    condition.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    for (int i = 0; i < statements.length; i++) {
      statements[i].getUsedVariable(list);
    }
    condition.getUsedVariable(list);
  }
}
