package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.Token;

/**
 * @author Matthieu Casanova
 */
public final class ConstantIdentifier extends Expression {

  private final String name;

  public ConstantIdentifier(final String name,
                            final int sourceStart,
                            final int sourceEnd,
                       final int beginLine,
                       final int endLine,
                       final int beginColumn,
                       final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.name = name;
  }

  public ConstantIdentifier(final Token token) {
    super(token.sourceStart, token.sourceEnd, token.beginLine,token.endLine,token.beginColumn,token.endColumn);
    name = token.image;
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    return name;
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {}

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {}

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {}
}
