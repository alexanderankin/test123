package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.Token;

/**
 * @author Matthieu Casanova
 */
public final class NullLiteral extends MagicLiteral {

  public NullLiteral(final Token token) {
    super(token.sourceStart, token.sourceEnd, token.beginLine,token.endLine,token.beginColumn,token.endColumn);
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    return "null";
  }
}
