package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * A default case for a switch.
 * it's default : .....;
 * @author Matthieu Casanova
 */
public final class DefaultCase extends AbstractCase {

  /**
   * Create a default case.
   *
   * @param statements the statements
   * @param sourceStart the starting offset
   * @param sourceEnd the ending offset
   */
  public DefaultCase(final Statement[] statements, final int sourceStart, final int sourceEnd) {
    super(statements, sourceStart, sourceEnd);
  }

  /**
   * Return the object into String.
   *
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("default : \n"); //$NON-NLS-1$
    for (int i = 0; i < statements.length; i++) {
      final Statement statement = statements[i];
      buff.append(statement.toString(tab + 9));
    }
    return buff.toString();
  }
}
