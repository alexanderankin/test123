package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * A continue statement.
 * @author Matthieu Casanova
 */
public final class Continue extends BranchStatement {

  public Continue(final Expression expression, final int sourceStart, final int sourceEnd) {
    super(expression, sourceStart, sourceEnd);
  }

  public String toString(final int tab) {
    final String s = tabString(tab);
    if (expression == null) {
      return s + "continue";//$NON-NLS-1$
    }
    return s + "continue " + expression.toString();//$NON-NLS-1$
	}
}
