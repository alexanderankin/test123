package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * a variable declaration in an array().
 * it could take Expression as key.
 * 
 * @author Matthieu Casanova
 */
public final class ArrayVariableDeclaration extends Expression {

  /** the array key. */
  private final Expression key;

  /** the array value. */
  private Expression value;

  /**
   * Create a new array variable declaration.
   * 
   * @param key   the key
   * @param value the value
   */
  public ArrayVariableDeclaration(final Expression key, final Expression value) {
    super(key.sourceStart, value.sourceEnd);
    this.key = key;
    this.value = value;
  }

  /**
   * Create a new array variable declaration.
   * 
   * @param key       the key
   * @param sourceEnd the end position
   */
  public ArrayVariableDeclaration(final Expression key, final int sourceEnd) {
    super(key.sourceStart, sourceEnd);
    this.key = key;
  }

  /**
   * Return the expression as String.
   * 
   * @return the expression
   */
  public String toStringExpression() {
    if (value == null) {
      return key.toStringExpression();
    } else {
      final String keyString = key.toStringExpression();
      final String valueString = value.toStringExpression();
      final StringBuffer buff = new StringBuffer(keyString.length() + valueString.length() + 3);
      buff.append(keyString);
      buff.append(" => ");
      buff.append(valueString);
      return buff.toString();
    }
  }


  /**
   * Get the variables from outside (parameters, globals ...)
   * 
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
  }

  /**
   * get the modified variables.
   * 
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    key.getModifiedVariable(list);
    if (value != null) {
      value.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   * 
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    key.getUsedVariable(list);
    if (value != null) {
      value.getUsedVariable(list);
    }
  }
}
