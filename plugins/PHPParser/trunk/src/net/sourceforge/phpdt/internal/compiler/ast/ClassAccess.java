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

  /** the accessType of access. */
  private final int accessType;

  /**
   * Instantiate a class access.
   *
   * @param prefix    usualy the class name
   * @param suffix    the field or method called (it can be null in case of parse error)
   * @param type      the accessType of access
   * @param sourceEnd the end offset
   * @param endLine   the end line
   * @param endColumn the end column
   */
  public ClassAccess(Expression prefix,
                     Expression suffix,
                     int type,
                     int sourceEnd,
                     int endLine,
                     int endColumn) {
    super(Type.UNKNOWN,prefix.getSourceStart(), sourceEnd, prefix.getBeginLine(), endLine, prefix.getBeginColumn(), endColumn);
    this.prefix = prefix;
    this.suffix = suffix;
    accessType = type;
  }

  private String toStringOperator() {
    switch (accessType) {
      case STATIC:
        return "::";
      case NORMAL:
        return "->";
    }
    return "unknown operator";
  }

  /**
   * Return the expression as String.
   *
   * @return the expression
   */
  public String toStringExpression() {
    String prefixString = prefix.toStringExpression();
    String operatorString = toStringOperator();
    StringBuffer buff = new StringBuffer(prefixString.length() + operatorString.length() + 100);
    buff.append(prefixString);
    buff.append(operatorString);
    if (suffix != null) {
      String suffixString = suffix.toStringExpression();
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
  public void getOutsideVariable(List list) {
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(List list) {
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(List list) {
    prefix.getUsedVariable(list);
    if (suffix != null) {
      suffix.getUsedVariable(list);
    }
  }
}
