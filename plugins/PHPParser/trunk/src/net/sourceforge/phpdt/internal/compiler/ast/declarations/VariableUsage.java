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
  private final int startOffset;

  private final int line;

  private final int column;

  /**
   * create a VariableUsage.
   * @param name the name of the variable
   * @param startOffset the offset
   */
  public VariableUsage(final String name, final int startOffset,final int line, final int column) {
    this.name = name;
    this.startOffset = startOffset;
    this.line = line;
    this.column = column;
  }

  public String toString() {
    return name + ' ' + startOffset;
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
  public int getStartOffset() {
    return startOffset;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public boolean equals(final Object object) {
    return name.equals(object);
  }

  public int hashCode() {
    return name.hashCode();
  }
}
