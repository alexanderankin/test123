package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;

import java.util.List;

/**
 * A variable declaration.
 *
 * @author Matthieu Casanova
 */
public class VariableDeclaration extends Expression implements Outlineable {

  public static final int EQUAL = 0;
  public static final int PLUS_EQUAL = 1;
  public static final int MINUS_EQUAL = 2;
  public static final int STAR_EQUAL = 3;
  public static final int SLASH_EQUAL = 4;
  public static final int AND_EQUAL = 5;
  public static final int OR_EQUAL = 6;
  public static final int XOR_EQUAL = 7;
  public static final int DOT_EQUAL = 8;
  public static final int REM_EQUAL = 9;
  public static final int TILDE_EQUAL = 10;
  public static final int LSHIFT_EQUAL = 11;
  public static final int RSIGNEDSHIFT_EQUAL = 12;

  protected final AbstractVariable variable;

  /**
   * The value for variable initialization.
   */
  public Expression initialization;

  private Object parent;
  protected boolean reference;


  private String operator;

  /**
   * Create a variable.
   *
   * @param variable       the name of the variable
   * @param initialization the initialization (it could be null when you have a parse error)
   * @param operator       the assign operator
   * @param sourceStart    the start point
   * @param sourceEnd      the end point
   */
  public VariableDeclaration(final Object parent,
                             final AbstractVariable variable,
                             final Expression initialization,
                             final String operator,
                             final int sourceStart,
                             final int sourceEnd,
                             final int beginLine,
                             final int endLine,
                             final int beginColumn,
                             final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.initialization = initialization;
    this.variable = variable;
    this.operator = operator;
    this.parent = parent;
  }

  /**
   * Create a variable.
   *
   * @param variable    a variable (in case of $$variablename)
   * @param sourceStart the start point
   */
  public VariableDeclaration(final Object parent,
                             final AbstractVariable variable,
                             final int sourceStart,
                             final int sourceEnd,
                             final int beginLine,
                             final int endLine,
                             final int beginColumn,
                             final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.variable = variable;
    this.parent = parent;
  }

  public final void setReference(final boolean reference) {
    this.reference = reference;
  }

  /**
   * Return the operator as String.
   * 
   * @return the operator
   */
/*  private String operatorToString() {
    switch (operator) {
      case EQUAL:
        return "="; //$NON-NLS-1$
      case PLUS_EQUAL:
        return "+=";   //$NON-NLS-1$
      case MINUS_EQUAL:
        return "-=";   //$NON-NLS-1$
      case STAR_EQUAL:
        return "*="; //$NON-NLS-1$
      case SLASH_EQUAL:
        return "/="; //$NON-NLS-1$
      case AND_EQUAL:
        return "<="; //$NON-NLS-1$
      case OR_EQUAL:
        return "|=";//$NON-NLS-1$
      case XOR_EQUAL:
        return "^=";//$NON-NLS-1$
      case DOT_EQUAL:
        return ".="; //$NON-NLS-1$
      case REM_EQUAL:
        return "%="; //$NON-NLS-1$
      case TILDE_EQUAL:
        return "~="; //$NON-NLS-1$
      case LSHIFT_EQUAL:
        return "<<="; //$NON-NLS-1$
      case RSIGNEDSHIFT_EQUAL:
        return ">>="; //$NON-NLS-1$
    }
    return " unknown operator ";//$NON-NLS-1$
  }   */

  /**
   * Return the variable into String.
   *
   * @return a String
   */
  public String toStringExpression() {
    final String variableString = variable.toStringExpression();
    if (initialization == null) {
      if (reference) return '&' + variableString; else return variableString;
    } else {
      //  final String operatorString = operatorToString();
      final String initString = initialization.toStringExpression();
      final StringBuffer buff = new StringBuffer(variableString.length() +
              operator.length() +
              initString.length() +
              1);
      buff.append(variableString);
      buff.append(operator); //$NON-NLS-1$
      buff.append(initString);
      return buff.toString();
    }
  }

  public final Object getParent() {
    return parent;
  }

  public final String toString() {
    return toStringExpression();
  }


  /**
   * Get the name of the field as String.
   *
   * @return the name of the String
   */
  public final String name() {
    return variable.getName();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   */
  public final void getOutsideVariable(final List list) {
  }

  /**
   * get the modified variables.
   */
  public final void getModifiedVariable(final List list) {
    variable.getUsedVariable(list);
    if (initialization != null) {
      initialization.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   */
  public final void getUsedVariable(final List list) {
    if (initialization != null) {
      initialization.getUsedVariable(list);
    }
  }
}
