package net.sourceforge.phpdt.internal.compiler.ast;


/**
 * A Statement.
 * @author Matthieu Casanova
 */
public abstract class Statement extends AstNode {

  /**
   * Create a node giving starting and ending offset.
   * @param sourceStart starting offset
   * @param sourceEnd ending offset
   */
  protected Statement(final int sourceStart,
                   final int sourceEnd) {
    super(sourceStart, sourceEnd);
  }

  /**
   * Tell if the block is empty.
   * @return a statement is not empty by default
   */
  public boolean isEmptyBlock() {
    return false;
  }
}
