package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * Variable suffix.
 * class access or [something]
 * Should it be an expression ?
 * @author Matthieu Casanova
 */
public abstract class AbstractSuffixExpression extends Expression {

  protected AbstractSuffixExpression(final int sourceStart, final int sourceEnd, final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
  }
}
