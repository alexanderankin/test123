package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.Token;

/**
 * the true literal.
 * @author Matthieu Casanova
 */
public final class TrueLiteral extends MagicLiteral {

  public TrueLiteral(final Token token) {
    super(token.sourceStart, token.sourceEnd);
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    return "true";//$NON-NLS-1$
  }

  public String toString() {
    return "true";//$NON-NLS-1$
  }
}
