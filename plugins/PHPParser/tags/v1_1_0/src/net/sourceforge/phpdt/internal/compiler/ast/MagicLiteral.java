package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * @author Matthieu Casanova
 */
public abstract class MagicLiteral extends Literal {

  protected MagicLiteral(final int sourceStart, final int sourceEnd, final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
  }
}
