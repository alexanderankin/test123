package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public final class HTMLBlock extends Statement {

  private final AstNode[] nodes;

  public HTMLBlock(final AstNode[] nodes) {
    super(nodes[0].sourceStart,
            nodes[(nodes.length > 0) ? nodes.length - 1 : 0].sourceEnd,
            nodes[0].getBeginLine(),
            nodes[(nodes.length > 0) ? nodes.length - 1 : 0].getEndLine(),
            nodes[0].getBeginColumn(),
            nodes[(nodes.length > 0) ? nodes.length - 1 : 0].getEndColumn());
    this.nodes = nodes;
  }

  /**
   * Return the object into String.
   *
   * @param tab how many tabs (not used here
   * @return a String
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("?>");
    for (int i = 0; i < nodes.length; i++) {
      buff.append(nodes[i].toString(tab + 1));
    }
    buff.append("<?php\n");
    return buff.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(final List list) {
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(final List list) {
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
  }
}
