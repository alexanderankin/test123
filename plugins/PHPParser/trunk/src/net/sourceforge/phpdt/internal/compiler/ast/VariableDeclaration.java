package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

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

  private final AbstractVariable variable;

  /** The value for variable initialization. */
  private Expression initialization;

  private transient OutlineableWithChildren parent;
  private boolean reference;


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
  public VariableDeclaration(OutlineableWithChildren parent,
                             AbstractVariable variable,
                             Expression initialization,
                             String operator,
                             int sourceStart,
                             int sourceEnd,
                             int beginLine,
                             int endLine,
                             int beginColumn,
                             int endColumn) {
    super(initialization == null ? Type.UNKNOWN : initialization.getType(),
          sourceStart,
          sourceEnd,
          beginLine,
          endLine,
          beginColumn,
          endColumn);
    this.initialization = initialization;
    this.variable = variable;
    variable.setType(type);
    this.operator = operator;
    this.parent = parent;
  }

  /**
   * Create a variable.
   *
   * @param variable    a variable (in case of $$variablename)
   * @param sourceStart the start point
   */
  public VariableDeclaration(OutlineableWithChildren parent,
                             AbstractVariable variable,
                             int sourceStart,
                             int sourceEnd,
                             int beginLine,
                             int endLine,
                             int beginColumn,
                             int endColumn) {
    super(Type.NULL, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.variable = variable;
    this.parent = parent;
  }

  public void setReference(boolean reference, int sourceStart, int beginLine, int beginColumn) {
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
    String variableString = variable.toStringExpression();
    if (initialization == null) {
      if (reference) return '&' + variableString;
      else return variableString;
    } else {
      //  final String operatorString = operatorToString();
      String initString = initialization.toStringExpression();
      StringBuffer buff = new StringBuffer(variableString.length() +
                                           operator.length() +
                                           initString.length() +
                                           1);
      buff.append(variableString);
      buff.append(operator); //$NON-NLS-1$
      buff.append(initString);
      return buff.toString();
    }
  }

  public OutlineableWithChildren getParent() {
    return parent;
  }

  public String toString() {
    return toStringExpression();
  }


  /** Get the variables from outside (parameters, globals ...) */
  public void getOutsideVariable(List list) {
  }

  /** get the modified variables. */
  public void getModifiedVariable(List list) {
    variable.getUsedVariable(list);
    if (initialization != null) {
      initialization.getModifiedVariable(list);
    }
  }

  /** Get the variables used. */
  public void getUsedVariable(List list) {
    if (initialization != null) {
      initialization.getUsedVariable(list);
    }
  }

  public String getName() {
    return variable.getName();
  }

  public Expression getInitialization() {
    return initialization;
  }
}
