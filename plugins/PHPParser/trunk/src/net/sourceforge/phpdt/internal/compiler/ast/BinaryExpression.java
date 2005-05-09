package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * a binary expression is a combination of two expressions with an operator.
 * 
 * @author Matthieu Casanova
 */
public final class BinaryExpression extends OperatorExpression {

  /** The left expression. */
  private final Expression left;
  /** The right expression. */
  private final Expression right;

  public BinaryExpression(Expression left,
                          Expression right,
                          int operator,
                          int sourceStart,
                          int sourceEnd,
                          int beginLine,
                          int endLine,
                          int beginColumn,
                          int endColumn) {
    super(Type.UNKNOWN, operator, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.left = left;
    this.right = right;
  }

  public String toStringExpression() {
    String leftString = left.toStringExpression();
    String operatorString = operatorToString();
    String rightString = right.toStringExpression();
    StringBuffer buff = new StringBuffer(leftString.length() + operatorString.length() + rightString.length());
    buff.append(leftString);
    buff.append(operatorString);
    buff.append(rightString);
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   * 
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(List list) {}

  /**
   * get the modified variables.
   * 
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(List list) {
    left.getModifiedVariable(list);
    if(right != null) {
      right.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   * 
   * @param list the list where we will put variables
   */
  public void getUsedVariable(List list) {
    left.getUsedVariable(list);
    if (right != null) {
    right.getUsedVariable(list);
    }
  }

}
