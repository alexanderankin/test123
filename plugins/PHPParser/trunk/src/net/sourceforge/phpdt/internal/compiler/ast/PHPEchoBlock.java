package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * a php echo block.
 * <?= someexpression ?>
 * @author Matthieu Casanova
 */
public final class PHPEchoBlock extends AstNode {

  /** the expression. */
  private final Expression expr;

  /**
   * @param sourceStart starting offset
   * @param sourceEnd ending offset
   * @param beginLine begin line
   * @param endLine ending line
   * @param beginColumn begin column
   * @param endColumn ending column
   */
  public PHPEchoBlock(final Expression expr,
                      final int sourceStart,
                    final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart,sourceEnd,beginLine,endLine,beginColumn,endColumn);
    this.expr = expr;
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final String tabs = tabString(tab);
    final String expression = expr.toStringExpression();
    final StringBuffer buff = new StringBuffer(tabs.length() +
                                               expression.length() +
                                               5);
    buff.append(tabs);
    buff.append("<?=");//$NON-NLS-1$
    buff.append(expression);
    buff.append("?>");//$NON-NLS-1$
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
  public void getModifiedVariable(final List list) {}

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    expr.getUsedVariable(list);
  }
}
