package net.sourceforge.phpdt.internal.compiler.ast.declarations;

/**
 * A variable usage.
 * This describe a variable declaration in a php document and his starting offset
 * @author Matthieu Casanova
 */
public final class VariableUsage {

  /** the variable name. */
  private final String name;

  /** where the variable is declared. */
  private final int sourceStart;
  private final int sourceEnd;

  private final int beginLine;
  private final int endLine;
  private final int beginColumn;
  private final int endColumn;


  /**
   * create a VariableUsage.
   * @param name the name of the variable
   * @param startOffset the offset
   */
  public VariableUsage(final String name, 
                       final int startOffset,
                       final int sourceEnd,
                       final int beginLine,
                       final int endLine,
                       final int beginColumn,
                       final int endColumn) {
    this.name = name;
    this.sourceStart = startOffset;
    this.sourceEnd = sourceEnd;
    this.beginLine = beginLine;
    this.endLine = endLine;
    this.beginColumn = beginColumn;
    this.endColumn = endColumn;
  }

  public String toString() {
    return name + ' ' + sourceStart;
  }

  /**
   * Get the name of the variable.
   * @return the name if the variable
   */
  public String getName() {
    return name;
  }

  /**
   * Get the starting offset.
   * @return the starting offset
   */
  public int getSourceStart() {
    return sourceStart;
  }

  public int getBeginLine() {
    return beginLine;
  }

  public int getBeginColumn() {
    return beginColumn;
  }

  public int getSourceEnd() {
    return sourceEnd;
  }

  public int getEndLine() {
    return endLine;
  }

  public int getEndColumn() {
    return endColumn;
  }

  public boolean equals(final Object object) {
    return name.equals(object);
  }

  public int hashCode() {
    return name.hashCode();
  }
}
