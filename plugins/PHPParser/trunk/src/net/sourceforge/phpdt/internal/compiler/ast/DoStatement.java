package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A do statement.
 * 
 * @author Matthieu Casanova
 */
public final class DoStatement extends Statement {


  /** The condition expression. */
  private final Expression condition;
  /** The action of the while. (it could be a block) */
  private final Statement action;

  public DoStatement(final Expression condition,
                     final Statement action,
                     final int sourceStart,
                     final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.condition = condition;
    this.action = action;
  }

  /**
   * Return the object into String.
   * 
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final String conditionString = condition.toStringExpression();
    final StringBuffer buff;
    if (action == null) {
      buff = new StringBuffer(17 + tab + conditionString.length());
      buff.append("do ");//$NON-NLS-1$
      buff.append(" {} ;"); //$NON-NLS-1$
    } else {
      final String actionString = action.toString(tab + 1);
      buff = new StringBuffer(13 + conditionString.length() + actionString.length());
      buff.append("do ");//$NON-NLS-1$
      buff.append("\n");//$NON-NLS-1$
      buff.append(actionString);
    }
    buff.append(tabString(tab));
    buff.append(" while (");//$NON-NLS-1$
    buff.append(conditionString);
    buff.append(")");//$NON-NLS-1$
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   * 
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    condition.getOutsideVariable(list); // todo: check if unuseful
    action.getOutsideVariable(list);
  }

  /**
   * get the modified variables.
   * 
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    condition.getModifiedVariable(list);
    action.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   * 
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    condition.getUsedVariable(list);
    action.getUsedVariable(list);
  }
}
