package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;
import java.util.ArrayList;

/** @author Matthieu Casanova */
public class MethodHeader extends Statement {

  private String name;

  private boolean reference;
  private ArrayList arguments;

  public MethodHeader(String name,
                      boolean reference,
                      final ArrayList arguments,
                      final int sourceStart,
                      final int sourceEnd,
                      final int beginLine,
                      final int endLine,
                      final int beginColumn,
                      final int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.name = name;
    this.reference = reference;
    this.arguments = arguments;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    StringBuffer buff = new StringBuffer(100);
    if (reference) buff.append('&');
    buff.append(name);
    buff.append('(');
    if (arguments != null) {
      for (int i = 0; i < arguments.size(); i++) {
        final VariableDeclaration o = (VariableDeclaration) arguments.get(i);
        buff.append(o.toStringExpression());
        if (i != (arguments.size() - 1)) {
          buff.append(", ");
        }
      }
    }
    buff.append(')');
    return buff.toString();
  }

  public String toString(int tab) {
    return tabString(tab) + toString();
  }

  public void getOutsideVariable(List list) {
  }

  public void getModifiedVariable(List list) {
  }

  public void getUsedVariable(List list) {
  }

  public int getArgumentsCount() {
    return arguments.size();
  }

  public void getParameters(final List list) {
    if (arguments != null) {
      for (int i = 0; i < arguments.size(); i++) {
        final VariableDeclaration variable = (VariableDeclaration) arguments.get(i);
        final VariableUsage variableUsage = new VariableUsage(variable.name(),
                                                              variable.getSourceStart(),
                                                              variable.getSourceEnd(),
                                                              variable.getBeginLine(),
                                                              variable.getEndLine(),
                                                              variable.getBeginColumn(),
                                                              variable.getEndColumn());
        list.add(variableUsage);
      }
    }
  }
}
