package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A Block.
 * {
 * statements
 * }.
 * @author Matthieu Casanova
 */
public final class Block extends Statement {

  /** An array of statements inside the block. */
  public final Statement[] statements;

  /**
   * Create a block.
   * @param statements the statements
   * @param sourceStart starting offset
   * @param sourceEnd ending offset
   */
  public Block(final Statement[] statements,
               final int sourceStart,
               final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.statements = statements;
  }

  /**
   * tell if the block is empty.
   * @return the block is empty if there are no statements in it
   */
  public boolean isEmptyBlock() {
    return statements == null;
  }

  /**
   * Return the block as String.
   * @param tab how many tabs
   * @return the string representation of the block
   */
  public String toString(final int tab) {
    final String s = AstNode.tabString(tab);
    final StringBuffer buff = new StringBuffer(s);
    buff.append("{\n"); //$NON-NLS-1$
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        buff.append(statements[i].toString(tab + 1)).append(";\n");//$NON-NLS-1$
      }
    }
    buff.append("}\n"); //$NON-NLS-1$
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    for (int i = 0; i < statements.length; i++) {
      statements[i].getOutsideVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
    for (int i = 0; i < statements.length; i++) {
      statements[i].getModifiedVariable(list);
    }
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    for (int i = 0; i < statements.length; i++) {
      statements[i].getUsedVariable(list);
    }
  }
}
