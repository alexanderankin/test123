package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A Function call.
 * @author Matthieu Casanova
 */
public final class FunctionCall extends AbstractSuffixExpression {

  /** the function name. */
  private final Expression functionName;

  /** the arguments. */
  private final Expression[] args;

  public FunctionCall(final Expression functionName,
                      final Expression[] args,
                      final int sourceEnd,
                       final int endLine,
                       final int endColumn) {
    super(functionName.sourceStart, sourceEnd,functionName.getBeginLine(),endLine,functionName.getBeginColumn(),endColumn);
    this.functionName = functionName;
    this.args = args;
  }
  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    final StringBuffer buff = new StringBuffer(functionName.toStringExpression());
    buff.append('(');
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        final Expression arg = args[i];
        if (i != 0) {
          buff.append(',');
        }
        buff.append(arg.toStringExpression());
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
  public void getOutsideVariable(final List list) {}

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        args[i].getModifiedVariable(list);
      }
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    functionName.getUsedVariable(list);
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        args[i].getUsedVariable(list);
      }
    }
  }
}
