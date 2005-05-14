package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * an array initializer. array('a','b','c') or array('a' => 2,'b' = '3');
 *
 * @author Matthieu Casanova
 */
public final class ArrayInitializer extends Expression {
  /** the key and values. */
  private final ArrayVariableDeclaration[] vars;

  /**
   * Create a new array initializer.
   *
   * @param vars        the keys and values of the array
   * @param sourceStart the starting offset
   * @param sourceEnd   the ending offset
   */
  public ArrayInitializer(ArrayVariableDeclaration[] vars,
                          int sourceStart,
                          int sourceEnd,
                          int beginLine,
                          int endLine,
                          int beginColumn,
                          int endColumn) {
    super(Type.ARRAY, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.vars = vars;
  }

  /**
   * Return the expression as String.
   *
   * @return the expression
   */
  public String toStringExpression() {
    StringBuffer buff = new StringBuffer("array(");
    for (int i = 0; i < vars.length; i++) {
      if (i != 0) {
        buff.append(',');
      }
      if (vars[i] != null) {
        buff.append(vars[i].toStringExpression());
      }
    }
    buff.append(')');
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(List list) {
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(List list) {
    for (int i = 0; i < vars.length; i++) {
      if (vars[i] != null) {
        vars[i].getModifiedVariable(list);
      }
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(List list) {
    for (int i = 0; i < vars.length; i++) {
      if (vars[i] != null) {
        vars[i].getUsedVariable(list);
      }
    }
  }

  public Expression expressionAt(int line, int column) {
    for (int i = 0; i < vars.length; i++) {
      ArrayVariableDeclaration var = vars[i];
      if (var.isAt(line, column)) return var;
    }
    return null;
  }
}
