package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.sidekick.PHPAsset;
import sidekick.Asset;

import javax.swing.text.Position;

/**
 * A GlobalStatement statement in php.
 *
 * @author Matthieu Casanova
 */
public final class GlobalStatement extends Statement implements Outlineable {

  /** An array of the variables called by this global statement. */
  private final AbstractVariable[] variables;

  private final transient OutlineableWithChildren parent;

  public GlobalStatement(OutlineableWithChildren parent,
                         AbstractVariable[] variables,
                         int sourceStart,
                         int sourceEnd,
                         int beginLine,
                         int endLine,
                         int beginColumn,
                         int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.variables = variables;
    this.parent = parent;
  }

  public String toString() {
    StringBuffer buff = new StringBuffer("global ");//$NON-NLS-1$
    for (int i = 0; i < variables.length; i++) {
      if (i != 0) {
        buff.append(", ");//$NON-NLS-1$
      }
      buff.append(variables[i].toStringExpression());
    }
    return buff.toString();
  }

  public String toString(int tab) {
    return tabString(tab) + toString();
  }

  public OutlineableWithChildren getParent() {
    return parent;
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(List list) {
    for (int i = 0; i < variables.length; i++) {
      variables[i].getUsedVariable(list);
    }
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(List list) {
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(List list) {
  }

  /**
   * We will analyse the code. if we have in globals a special variable it will be reported as a warning.
   *
   * @see Variable#SPECIAL_VARS
   */
  public void analyzeCode(PHPParser parser) {
    for (int i = 0; i < variables.length; i++) {
      if (arrayContains(Variable.SPECIAL_VARS, variables[i].getName())) {
        parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                                                         PHPParseMessageEvent.MESSAGE_UNNECESSARY_GLOBAL,
                                                         parser.getPath(),
                                                         "warning, you shouldn't request " + variables[i].getName() + " as global",
                                                         variables[i].sourceStart,
                                                         variables[i].sourceEnd,
                                                         variables[i].getBeginLine(),
                                                         variables[i].getEndLine(),
                                                         variables[i].getBeginColumn(),
                                                         variables[i].getEndColumn()));
      }
    }
  }

  public String getName() {
    //todo : change this
    return null;
  }

  public int getItemType() {
    return PHPItem.GLOBAL;
  }

  public Asset getAsset(Position start, Position end) {
    return new PHPAsset(toString(),start, end);
  }
}
