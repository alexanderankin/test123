package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * a class instantiation.
 *
 * @author Matthieu Casanova
 */
public final class ClassInstantiation extends PrefixedUnaryExpression {

  private final boolean reference;

  public ClassInstantiation(final Expression expression,
                            final boolean reference,
                            final int sourceStart,
                            final int beginLine,
                            final int beginColumn) {
    super(expression, OperatorIds.NEW, sourceStart,beginLine,beginColumn);
    this.reference = reference;
  }
  

  public String toStringExpression() {
    if (!reference) {
      return super.toStringExpression();
    }
    return '&' + super.toStringExpression();
  }
}
