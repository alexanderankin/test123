package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * An empty statement.
 * @author Matthieu Casanova
 */
public final class EmptyStatement extends Statement {

  public EmptyStatement(final int sourceStart, final int sourceEnd) {
    super(sourceStart, sourceEnd);
  }

  public String toString(final int tab) {
    return tabString(tab) + ';'; //$NON-NLS-1$
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {}

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {}

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {}
}
