package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * A break statement.
 * @author Matthieu Casanova
 */
public final class Break extends BranchStatement {

  public Break(final Expression expression, final int sourceStart, final int sourceEnd) {
    super(expression, sourceStart, sourceEnd);
  }

  public String toString(final int tab) {
    final String s = tabString(tab);
    if (expression != null) {
      return s + "break " + expression.toString();//$NON-NLS-1$
    }
    return s + "break";//$NON-NLS-1$
	}
}
