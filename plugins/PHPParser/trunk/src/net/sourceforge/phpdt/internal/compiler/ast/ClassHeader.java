package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/** @author Matthieu Casanova */
public class ClassHeader extends AstNode {

  private String className;
  private String superClassName;

  public ClassHeader(String className, String superClassName, final int sourceStart, final int sourceEnd, final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.className = className;
    this.superClassName = superClassName;
  }

  public ClassHeader(String className, final int sourceStart, final int sourceEnd, final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
    this(className, null, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
  }

  public String toString(int tab) {
    return tabString(tab) + toString();
  }

  public String toString() {
    final StringBuffer buff = new StringBuffer(200);
    buff.append("class ").append(className);
    if (superClassName != null) {
      buff.append(" extends ");
      buff.append(superClassName);
    }
    return buff.toString();
  }

  public void getOutsideVariable(List list) {
  }

  public void getModifiedVariable(List list) {
  }

  public void getUsedVariable(List list) {
  }

  public String getClassName() {
    return className;
  }
}
