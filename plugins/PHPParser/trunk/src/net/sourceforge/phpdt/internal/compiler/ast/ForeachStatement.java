package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A Foreach statement.
 *
 * @author Matthieu Casanova
 */
public final class ForeachStatement extends Statement {

  private final Expression expression;
  private final Expression variable;
  private final Statement statement;

  /**
   * Create a new Foreach statement.
   *
   * @param expression  the Array that will be read. It could be null if there was a parse error
   * @param variable    the value (it could be a value or a key => value, or null if there was a parse error)
   * @param statement   the statement that will be executed
   * @param sourceStart the start of the foreach
   * @param sourceEnd   the end of the foreach
   */
  public ForeachStatement(final Expression expression,
                          final Expression variable,
                          final Statement statement,
                          final int sourceStart,
                          final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.expression = expression;
    this.variable = variable;
    this.statement = statement;
  }

  /**
   * Return the object into String.
   * 
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final String expressionString;
    if (expression == null) {
      expressionString = "__EXPRESSION__";
    } else {
      expressionString = expression.toStringExpression();
    }
    final String variableString;
    if (variable == null) {
      variableString = "__VARIABLE__";
    } else {
      variableString = variable.toStringExpression();
    }

    final String statementString;
    if (statement== null) {
      statementString = "__STATEMENT__";
    } else {
      statementString = statement.toString(tab + 1);
    }
    
    final StringBuffer buff = new StringBuffer(tab +
                                               expressionString.length() +
                                               variableString.length() +
                                               statementString.length() + 18);
    buff.append(AstNode.tabString(tab));
    buff.append("foreach (");
    buff.append(expressionString);
    buff.append(" as ");
    buff.append(variableString);
    buff.append(" {\n");
    buff.append(statementString);
    buff.append("\n}");
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...).
   * 
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    if (expression != null) expression.getOutsideVariable(list);
    if (variable != null) variable.getOutsideVariable(list);
    if (statement!= null) statement.getOutsideVariable(list);
  }

  /**
   * get the modified variables.
   * 
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    if (expression != null) expression.getModifiedVariable(list);
    if (variable != null) variable.getUsedVariable(list);
    if (statement!= null) statement.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   * 
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    if (expression != null) expression.getUsedVariable(list);
    if (statement!= null) statement.getUsedVariable(list);
  }
}
