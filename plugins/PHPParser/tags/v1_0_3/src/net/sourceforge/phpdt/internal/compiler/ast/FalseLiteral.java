package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.Token;

/**
 * @author Matthieu Casanova
 */
public final class FalseLiteral extends MagicLiteral {

  public FalseLiteral(final Token token) {
    super(token.sourceStart, token.sourceEnd);
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    return "false";//$NON-NLS-1$
  }

  public String toString() {
    return "false";//$NON-NLS-1$
  }
}