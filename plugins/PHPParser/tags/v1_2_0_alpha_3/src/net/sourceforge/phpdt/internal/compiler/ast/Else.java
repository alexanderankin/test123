package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * an else statement.
 * it's else
 * @author Matthieu Casanova
 */
public final class Else extends Statement {

  /** the statements. */
  private final Statement[] statements;

  /**
   * An else statement bad version ( : endif).
   * @param statements the statements
   * @param sourceStart the starting offset
   * @param sourceEnd the ending offset
   */
  public Else(final Statement[] statements,
              final int sourceStart,
              final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart, sourceEnd,beginLine,endLine,beginColumn,endColumn);
    this.statements = statements;
  }

  /**
   * An else statement good version
   * @param statement the statement (it could be a block)
   * @param sourceStart the starting offset
   * @param sourceEnd the ending offset
   */
  public Else(final Statement statement,
              final int sourceStart,
              final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart, sourceEnd,beginLine,endLine,beginColumn,endColumn);
    statements = new Statement[1];
    statements[0] = statement;
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("else \n");//$NON-NLS-1$
    Statement statement;
    for (int i = 0; i < statements.length; i++) {
      statement = statements[i];
      buff.append(statement.toString(tab + 1)).append("\n");//$NON-NLS-1$
    }
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
