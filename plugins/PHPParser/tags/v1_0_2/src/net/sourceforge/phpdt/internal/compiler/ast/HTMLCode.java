package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;


/**
 * It's html code.
 * It will contains some html, javascript, css ...
 * @author Matthieu Casanova
 */
public final class HTMLCode extends AstNode {

  /** The html Code. */
  private final String htmlCode;

  /**
   * Create an html Block.
   * @param htmlCode the html inside the block
   * @param sourceStart the starting offset
   * @param sourceEnd the ending offset
   */
  public HTMLCode(final String htmlCode,
                  final int sourceStart,
                  final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.htmlCode = htmlCode;
  }

  /**
   * I don't process tabs, it will only return the html inside.
   * @return the text of the block
   */
  public String toString() {
    return htmlCode;
  }

  /**
   * I don't process tabs, it will only return the html inside.
   * @param tab how many tabs before this html
   * @return the text of the block
   */
  public String toString(final int tab) {
    return htmlCode + ' ';
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
