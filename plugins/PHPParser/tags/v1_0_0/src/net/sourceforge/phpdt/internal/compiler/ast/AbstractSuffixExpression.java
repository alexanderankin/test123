package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * Variable suffix.
 * class access or [something]
 * Should it be an expression ?
 * @author Matthieu Casanova
 */
public abstract class AbstractSuffixExpression extends Expression {

  protected AbstractSuffixExpression(final int sourceStart, final int sourceEnd) {
    super(sourceStart, sourceEnd);
  }
}
