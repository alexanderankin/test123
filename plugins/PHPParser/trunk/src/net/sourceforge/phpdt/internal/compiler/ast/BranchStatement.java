package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * Here is a branchstatement : break or continue.
 * @author Matthieu Casanova
 */
public abstract class BranchStatement extends Statement {

  /** The label (if there is one). */
  protected final Expression expression;

  protected BranchStatement(final Expression expression, final int sourceStart, final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart, sourceEnd,beginLine,endLine,beginColumn,endColumn);
    this.expression = expression;
  }


  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public final void getOutsideVariable(final List list) {
    if (expression != null) {
      expression.getOutsideVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public final void getModifiedVariable(final List list) {
    if (expression != null) {
    expression.getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public final void getUsedVariable(final List list) {
    if (expression != null) {
      expression.getUsedVariable(list);
    }
  }
}
