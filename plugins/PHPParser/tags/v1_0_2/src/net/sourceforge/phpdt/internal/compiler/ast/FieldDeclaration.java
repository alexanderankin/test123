package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;

/**
 * A Field declaration.
 * This is a variable declaration for a php class
 * In fact it's an array of VariableUsage, since a field could contains
 * several var :
 * var $toto,$tata;
 * @author Matthieu Casanova
 */
public final class FieldDeclaration extends Statement implements Outlineable {

  /** The variables. */
  public final VariableDeclaration[] vars;

  private final Object parent;

  /**
   * Create a new field.
   * @param vars the array of variables.
   * @param sourceStart the starting offset
   * @param sourceEnd   the ending offset
   */
  public FieldDeclaration(final VariableDeclaration[] vars,
                          final int sourceStart,
                          final int sourceEnd,
                          final Object parent) {
    super(sourceStart, sourceEnd);
    this.vars = vars;
    this.parent = parent;
  }

  /**
   * Return the object into String.
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("var ");//$NON-NLS-1$
    for (int i = 0; i < vars.length; i++) {
      if (i != 0) {
        buff.append(",");//$NON-NLS-1$
      }
      buff.append(vars[i].toStringExpression());
    }
    return buff.toString();
  }


  public Object getParent() {
    return parent;
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
  public void getUsedVariable(final List list) {}
}
