package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;

import java.util.List;
import java.io.Serializable;

/**
 * It will be the mother of our own ast tree for php just like the ast tree of Eclipse.
 * @author Matthieu Casanova
 */
public abstract class AstNode implements Serializable {

  /** Starting and ending position of the node in the sources. */
  public int sourceStart, sourceEnd;

  protected int beginLine,endLine;
  protected int beginColumn,endColumn;

  protected AstNode() {
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
  protected AstNode(final int sourceStart,
                    final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    this.sourceStart = sourceStart;
    this.sourceEnd = sourceEnd;
    this.beginLine = beginLine;
    this.endLine = endLine;
    this.beginColumn = beginColumn;
    this.endColumn = endColumn;
  }

  /**
   * Add some tabulations.
   * @param tab the number of tabulations
   * @return a String containing some spaces
   */
  public static String tabString(final int tab) {
    if (tab == 0) return "";
    final StringBuffer s = new StringBuffer(2 * tab);
    for (int i = tab; i > 0; i--) {
      s.append("  "); //$NON-NLS-1$
    }
    return s.toString();
  }

  /**
   * Return the object into String.
   * It should be overriden
   * @return a String
   */
  public String toString() {
    return "****" + super.toString() + "****";  //$NON-NLS-2$ //$NON-NLS-1$
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public abstract String toString(int tab);

  /**
   * Get the variables from outside (parameters, globals ...)
   * @param list the list where we will put variables
   */
  public abstract void getOutsideVariable(List list);

  /**
   * get the modified variables.
   * @param list the list where we will put variables
   */
  public abstract void getModifiedVariable(List list);

  /**
   * Get the variables used.
   * @param list the list where we will put variables
   */
  public abstract void getUsedVariable(List list);

  /**
   * This method will analyze the code.
   * by default it will do nothing
   */
  public void analyzeCode(PHPParser parser) {}

  /**
   * Check if the array array contains the object o.
   * @param array an array
   * @param o an obejct
   * @return true if the array contained the object o
   */
  public final boolean arrayContains(final Object[] array, final Object o) {
    for (int i = 0; i < array.length; i++) {
      if (array[i].equals(o)) {
        return true;
      }
    }
    return false;
  }

  public int getSourceStart() {
    return sourceStart;
  }

  public int getSourceEnd() {
    return sourceEnd;
  }

  public int getBeginLine() {
    return beginLine;
  }

  public int getEndLine() {
    return endLine;
  }

  public int getBeginColumn() {
    return beginColumn;
  }

  public int getEndColumn() {
    return endColumn;
  }
}
