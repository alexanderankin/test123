package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * Any class access.
 * @author Matthieu Casanova
 */
public final class ClassAccess extends AbstractVariable {

  /** a static class access : "::". */
  public static final int STATIC = 0;

  /** a normal class access : "->". */
  public static final int NORMAL = 1;

  private final Expression prefix;

  /** the suffix. */
  private final Expression suffix;

  /** the type of access. */
  private final int type;

  /**
   * Create a new class access.
   * @param prefix
   * @param suffix
   * @param type the type of access {@link #STATIC} or {@link #NORMAL}
   */
  public ClassAccess(final Expression prefix,
                     final Expression suffix,
                     final int type) {
    super(prefix.sourceStart, suffix.sourceEnd);
    this.prefix = prefix;
    this.suffix = suffix;
    this.type = type;
  }

  private String toStringOperator() {
    switch (type) {
      case STATIC : return "::"; //$NON-NLS-1$
      case NORMAL : return "->"; //$NON-NLS-1$
    }
    return "unknown operator"; //$NON-NLS-1$
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    final String prefixString = prefix.toStringExpression();
    final String operatorString = toStringOperator();
    final String suffixString = suffix.toStringExpression();
    final StringBuffer buff = new StringBuffer(prefixString.length() +
                                               operatorString.length() +
                                               suffixString.length());
    buff.append(prefixString);
    buff.append(operatorString);
    buff.append(suffixString);
    return buff.toString();
  }

  /**
   * todo: find a better way to handle this
   * @return the name of the variable
   */
  public String getName() {
    if (prefix instanceof AbstractVariable) {
      return ((AbstractVariable)prefix).getName();
    }
    return prefix.toStringExpression();
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
  public void getUsedVariable(final List list) {
    prefix.getUsedVariable(list);
    suffix.getUsedVariable(list);
  }
}
