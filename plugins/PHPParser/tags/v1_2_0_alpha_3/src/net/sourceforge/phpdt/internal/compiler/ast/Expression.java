package net.sourceforge.phpdt.internal.compiler.ast;


/**
 * An expression.
 *
 * @author Matthieu Casanova
 */
public abstract class Expression extends Statement {

  /**
   * Create an expression giving starting and ending offset
   *
   * @param sourceStart starting offset
   * @param sourceEnd   ending offset
   */
  protected Expression(final int sourceStart,
                       final int sourceEnd,
                       final int beginLine,
                       final int endLine,
                       final int beginColumn,
                       final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
  }

  protected Expression() {
  }

  /**
   * Return the expression with a number of spaces before.
   *
   * @param tab how many spaces before the expression
   * @return a string representing the expression
   */
  public final String toString(final int tab) {
    return tabString(tab) + toStringExpression();
  }

  /**
   * Return the expression as String.
   *
   * @return the expression
   */
  public abstract String toStringExpression();
}
