package net.sourceforge.phpdt.internal.compiler.ast;


/**
 * A Statement.
 *
 * @author Matthieu Casanova
 */
public abstract class Statement extends AstNode {

  /**
   * Create a node giving starting and ending offset.
   * todo: virer ca
   * @deprecated
   * @param sourceStart starting offset
   * @param sourceEnd   ending offset
   */
  protected Statement(final int sourceStart,
                      final int sourceEnd) {
    this(sourceStart, sourceEnd,0,0,0,0);
  }

  protected Statement() {
  }

  /**
   * Create a node.
   *
   * @param sourceStart starting offset
   * @param sourceEnd ending offset
   * @param beginLine begin line
   * @param endLine ending line
   * @param beginColumn begin column
   * @param endColumn ending column
   */
  protected Statement(final int sourceStart,
                    final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart,sourceEnd,beginLine,endLine,beginColumn,endColumn);
  }

  /**
   * Tell if the block is empty.
   *
   * @return a statement is not empty by default
   */
  public boolean isEmptyBlock() {
    return false;
  }
}
