package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public final class LabeledStatement extends Statement {

  private final String label;

  private final Statement statement;

  public LabeledStatement(final String label,
                          final Statement statement,
                          final int sourceStart,
                          final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.label = label;
    this.statement = statement;
  }

  /**
   * Return the object into String.
   * It should be overriden
   * 
   * @return a String
   */
  public String toString() {
    if (statement != null) {
      return label + statement.toString();
    }
    return label;
  }

  /**
   * Return the object into String.
   * 
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    return tabString(tab) + toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    if (statement != null) {
      statement.getOutsideVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    if (statement != null) {
      statement.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    if (statement != null) {
      statement.getUsedVariable(list);
    }
  }
}
