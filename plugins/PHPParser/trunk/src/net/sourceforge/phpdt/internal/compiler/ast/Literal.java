package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * Here is the Superclass of the Literal expressions.
 * @author Matthieu Casanova
 */
public abstract class Literal extends Expression {

  protected Literal(final int sourceStart, final int sourceEnd, final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public final void getOutsideVariable(final List list) {}

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public final void getModifiedVariable(final List list) {}

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {}
}
