package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParserConstants;

import java.util.List;

/**
 * An assignment.
 *
 * @author Matthieu Casanova
 */
public class Assignment extends Expression {
  protected final Expression target;

  /** The value for variable initialization. */
  public Expression initialization;

  protected boolean reference;

  private int operator;

  /**
   * Create a variable.
   *
   * @param variable       the name of the variable
   * @param initialization the initialization (it could be null when you have a parse error)
   * @param operator       the assign operator
   * @param sourceStart    the start point
   * @param sourceEnd      the end point
   */
  public Assignment(Expression variable,
                    Expression initialization,
                    int operator,
                    int sourceStart,
                    int sourceEnd,
                    int beginLine,
                    int endLine,
                    int beginColumn,
                    int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.initialization = initialization;
    this.target = variable;
    this.operator = operator;
  }


  public final void setReference(boolean reference,
                                 int sourceStart,
                                 int beginLine,
                                 int beginColumn) {
    this.reference = reference;
    this.sourceStart = sourceStart;
    this.beginLine = beginLine;
    this.beginColumn = beginColumn;
  }

  /**
   * Return the variable into String.
   *
   * @return a String
   */
  public String toStringExpression() {
    final String variableString = target.toStringExpression();
    final String initString = initialization.toStringExpression();
    final String operatorImage = PHPParserConstants.tokenImage[operator].substring(1,PHPParserConstants.tokenImage[operator].length()-1);
    final StringBuffer buff = new StringBuffer(variableString.length() +
                                               operatorImage.length() +
                                               initString.length() +
                                               1);
    buff.append(variableString);
    buff.append(operatorImage);
    buff.append(initString);
    return buff.toString();
  }

  public final String toString() {
    return toStringExpression();
  }

  /** Get the variables from outside (parameters, globals ...) */
  public final void getOutsideVariable(final List list) {
  }

  /** get the modified variables. */
  public final void getModifiedVariable(final List list) {
    target.getModifiedVariable(list);
    initialization.getModifiedVariable(list);
  }

  /** Get the variables used. */
  public final void getUsedVariable(final List list) {
    initialization.getUsedVariable(list);
  }

}
