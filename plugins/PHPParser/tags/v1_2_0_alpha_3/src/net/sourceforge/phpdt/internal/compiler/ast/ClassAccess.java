package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * Any class access.
 *
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
   * Instantiate a class access.
   *
   * @param prefix    usualy the class name
   * @param suffix    the field or method called (it can be null in case of parse error)
   * @param type      the type of access
   * @param sourceEnd the end offset
   * @param endLine   the end line
   * @param endColumn the end column
   */
  public ClassAccess(final Expression prefix,
                     final Expression suffix,
                     final int type,
                     final int sourceEnd,
                     final int endLine,
                     final int endColumn) {
    super(prefix.getSourceStart(), sourceEnd, prefix.getBeginLine(), endLine, prefix.getBeginColumn(), endColumn);
    this.prefix = prefix;
    this.suffix = suffix;
    this.type = type;
  }

  private String toStringOperator() {
    switch (type) {
      case STATIC:
        return "::"; //$NON-NLS-1$
      case NORMAL:
        return "->"; //$NON-NLS-1$
    }
    return "unknown operator"; //$NON-NLS-1$
  }

  /**
   * Return the expression as String.
   *
   * @return the expression
   */
  public String toStringExpression() {
    final String prefixString = prefix.toStringExpression();
    final String operatorString = toStringOperator();
    final StringBuffer buff = new StringBuffer(prefixString.length() + operatorString.length() + 100);
    buff.append(prefixString);
    buff.append(operatorString);
    if (suffix != null) {
      final String suffixString = suffix.toStringExpression();
      buff.append(suffixString);
    }
    return buff.toString();
  }

  /**
   * Returns the name of the class. todo: find a better way to handle this
   *
   * @return the name of the variable
   */
  public String getName() {
    if (prefix instanceof AbstractVariable) {
      return ((AbstractVariable) prefix).getName();
    }
    return prefix.toStringExpression();
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
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    prefix.getUsedVariable(list);
    if (suffix != null) {
      suffix.getUsedVariable(list);
    }
  }
}
