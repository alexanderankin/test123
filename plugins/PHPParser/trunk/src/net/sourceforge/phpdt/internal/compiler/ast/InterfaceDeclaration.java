package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/** @author Matthieu Casanova */
public class InterfaceDeclaration extends Statement {
  public InterfaceDeclaration(int sourceStart,
                              int sourceEnd,
                              int beginLine,
                              int endLine,
                              int beginColumn,
                              int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
  }

  public String toString(int tab) {
    return null;
  }

  public void getOutsideVariable(List list) {
  }

  public void getModifiedVariable(List list) {
  }

  public void getUsedVariable(List list) {
  }
}
