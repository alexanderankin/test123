package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A While statement.
 *
 * @author Matthieu Casanova
 */
public final class WhileStatement extends Statement {

  /**
   * The condition expression.
   */
  private final Expression condition;
  /**
   * The action of the while. (it could be a block)
   */
  private final Statement action;

  /**
   * Create a While statement.
   *
   * @param condition   the condition
   * @param action      the action
   * @param sourceStart the starting offset
   * @param sourceEnd   the ending offset
   */
  public WhileStatement(final Expression condition,
                        final Statement action,
                        final int sourceStart,
                        final int sourceEnd,
                        final int beginLine,
                        final int endLine,
                        final int beginColumn,
                        final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
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
    final String s = tabString(tab);
    final StringBuffer buff = new StringBuffer(s).append("while ("); //$NON-NLS-1$
    buff.append(condition.toStringExpression()).append(")"); 	//$NON-NLS-1$
    if (action == null) {
      buff.append(" {} ;"); //$NON-NLS-1$
    } else {
      buff.append("\n").append(action.toString(tab + 1)); //$NON-NLS-1$
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
    if (action != null) {
      action.getOutsideVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    condition.getModifiedVariable(list);
    if (action != null) {
      action.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    condition.getUsedVariable(list);
    if (action != null) {
      action.getUsedVariable(list);
    }
  }
}
