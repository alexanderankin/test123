package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A Case statement for a Switch.
 * @author Matthieu Casanova
 */
public final class Case extends AbstractCase {

  private final Expression value;

  public Case(final Expression value,
              final Statement[] statements,
              final int sourceStart,
              final int sourceEnd) {
    super(statements, sourceStart, sourceEnd);
    this.value = value;
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("case ");
    buff.append(value.toStringExpression());
    buff.append(" :\n");
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        final Statement statement = statements[i];
        buff.append(statement.toString(tab + 1));
      }
    }
    return buff.toString();
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    super.getModifiedVariable(list);
    value.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    super.getUsedVariable(list);
    value.getUsedVariable(list);
  }
}
