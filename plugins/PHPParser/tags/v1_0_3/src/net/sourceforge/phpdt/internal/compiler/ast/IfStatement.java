package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * This is a if statement.
 * if (condition)
 *  statement
 * (elseif statement)*
 * else statement
 * @author Matthieu Casanova
 */
public final class IfStatement extends Statement {

  private final Expression condition;
  private final Statement statement;
  private final ElseIf[] elseifs;
  private final Else els;

  /**
   * Create a new If statement.
   * @param condition the condition
   * @param statement a statement or a block of statements
   * @param elseifs the elseifs
   * @param els the else (or null)
   * @param sourceStart the starting position
   * @param sourceEnd the ending offset
   */
  public IfStatement(final Expression condition,
                     final Statement statement,
                     final ElseIf[] elseifs,
                     final Else els,
                     final int sourceStart,
                     final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.condition = condition;
    this.statement = statement;
    this.elseifs = elseifs;
    this.els = els;
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("if (");//$NON-NLS-1$
    buff.append(condition.toStringExpression()).append(") ");//$NON-NLS-1$
    if (statement != null) {
      buff.append(statement.toString(tab + 1));
    }
    for (int i = 0; i < elseifs.length; i++) {
      buff.append(elseifs[i].toString(tab + 1));
      buff.append("\n");//$NON-NLS-1$
    }
    if (els != null) {
      buff.append(els.toString(tab + 1));
      buff.append("\n");//$NON-NLS-1$
    }
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    condition.getOutsideVariable(list); // todo: check if unuseful
    if (statement != null) {
      statement.getOutsideVariable(list);
    }
    for (int i = 0; i < elseifs.length; i++) {
      elseifs[i].getOutsideVariable(list);
    }
    if (els != null) {
      els.getOutsideVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    condition.getModifiedVariable(list);
    if (statement != null) {
      statement.getModifiedVariable(list);
    }
    for (int i = 0; i < elseifs.length; i++) {
      elseifs[i].getModifiedVariable(list);
    }
    if (els != null) {
      els.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    condition.getUsedVariable(list);
    if (statement != null) {
      statement.getUsedVariable(list);
    }
    for (int i = 0; i < elseifs.length; i++) {
      elseifs[i].getUsedVariable(list);
    }
    if (els != null) {
      els.getUsedVariable(list);
    }
  }
}
