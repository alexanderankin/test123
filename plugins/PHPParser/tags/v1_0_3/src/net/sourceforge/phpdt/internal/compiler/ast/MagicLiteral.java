package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * @author Matthieu Casanova
 */
public abstract class MagicLiteral extends Literal {

  protected MagicLiteral(final int sourceStart, final int sourceEnd) {
    super(sourceStart, sourceEnd);
  }
}
