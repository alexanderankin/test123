package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.PHPParseMessageEvent;

/**
 * A GlobalStatement statement in php.
 * @author Matthieu Casanova
 */
public final class GlobalStatement extends Statement implements Outlineable {

  /** An array of the variables called by this global statement. */
  private final AbstractVariable[] variables;

  private final Object parent;


  public GlobalStatement(final Object parent,
                         final AbstractVariable[] variables,
                         final int sourceStart,
                         final int sourceEnd,
                    final int beginLine,
                    final int endLine,
                    final int beginColumn,
                    final int endColumn) {
    super(sourceStart, sourceEnd,beginLine,endLine,beginColumn,endColumn);
    this.variables = variables;
    this.parent = parent;
  }
  
  public String toString() {
    final StringBuffer buff = new StringBuffer("global ");//$NON-NLS-1$
    for (int i = 0; i < variables.length; i++) {
      if (i != 0) {
        buff.append(", ");//$NON-NLS-1$
      }
      buff.append(variables[i].toStringExpression());
    }
    return buff.toString();
  }

  public String toString(final int tab) {
    return tabString(tab) + toString();
  }

  public Object getParent() {
    return parent;
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
    for (int i = 0; i < variables.length; i++) {
      variables[i].getUsedVariable(list);
    }
  }

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

  /**
   * We will analyse the code.
   * if we have in globals a special variable it will be reported as a warning.
   * @see Variable#SPECIAL_VARS
   */
  public void analyzeCode(PHPParser parser) {
    for (int i = 0; i < variables.length; i++) {
      if (arrayContains(Variable.SPECIAL_VARS, variables[i].getName())) {
                parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                        parser.getPath(),
                        "warning, you shouldn't request " + variables[i].getName() + " as global",
                        variables[i].sourceStart,
                        variables[i].sourceEnd,
                        0,
                        0,
                        0,
                        0));
      }
    }
  }

  public String getName() {
    //todo : change this
    return null;
  }
}
