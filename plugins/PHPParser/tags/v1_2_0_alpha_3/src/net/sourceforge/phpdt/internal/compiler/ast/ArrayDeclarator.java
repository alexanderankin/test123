package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * An access to a key of an array.
 * @author Matthieu Casanova
 */
public final class ArrayDeclarator extends AbstractVariable {

  /** The name of the array. */
  private final AbstractVariable prefix;

  /** The key. */
  private final Expression key;

  /**
   * Create an ArrayDeclarator.
   * @param prefix the prefix, it could be a variable.
   * @param key the key
   * @param sourceEnd the end of the expression
   */
  public ArrayDeclarator(final AbstractVariable prefix,
                         final Expression key,
                         final int sourceEnd,
                         final int endLine,
                         final int endColumn) {
    super(prefix.sourceStart, sourceEnd,prefix.getBeginLine(),endLine,prefix.getBeginColumn(),endColumn);
    this.prefix = prefix;
    this.key = key;
  }
  
  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    final StringBuffer buff = new StringBuffer(prefix.toStringExpression());
    buff.append('[');
    if (key != null) {
      buff.append(key.toStringExpression());
    }
    buff.append(']');
    return buff.toString();
  }

  /**
   * Return the name of the variable.
   * @return the name of the functionName variable
   */
  public String getName() {
    return prefix.getName();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {}

  /**
   * get the modified variables.
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    prefix.getModifiedVariable(list);
    if (key != null) {
      key.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    prefix.getUsedVariable(list);
    if (key != null) {
      key.getUsedVariable(list);
    }
  }
}
