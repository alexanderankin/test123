package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * The variable superclass.
 * @author Matthieu Casanova
 */
public abstract class AbstractVariable extends Expression {
  protected AbstractVariable(final int sourceStart, final int sourceEnd) {
    super(sourceStart, sourceEnd);
  }

  /**
   * This method will return the name of the variable.
   * @return a string containing the name of the variable.
   */
  public abstract String getName();
}
