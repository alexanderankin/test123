package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParser;

/**
 * A Method declaration.
 *
 * @author Matthieu Casanova
 */
public final class MethodDeclaration extends Statement implements OutlineableWithChildren {
  private MethodHeader methodHeader;

  private Statement[] statements;

  private int bodyLineStart;
  private int bodyColumnStart;
  private int bodyLineEnd;
  private int bodyColumnEnd;

  /** Tell if the method is a class constructor. */
  private boolean isConstructor;

  /** The parent object. */
  private transient OutlineableWithChildren parent;
  /** The outlineable children (those will be in the node array too. */
  private final List children = new ArrayList();

  public MethodDeclaration(OutlineableWithChildren parent, MethodHeader methodHeader) {
    sourceStart = methodHeader.getSourceStart();
    beginLine = methodHeader.getBeginLine();
    beginColumn = methodHeader.getBeginColumn();
    this.parent = parent;
    this.methodHeader = methodHeader;
  }

  /**
   * Return method into String, with a number of tabs
   *
   * @param tab the number of tabs
   *
   * @return the String containing the method
   */
  public String toString(int tab) {
    StringBuffer buff = new StringBuffer(200);
    buff.append(methodHeader.toString(tab));
    buff.append(toStringStatements(tab + 1));
    return buff.toString();
  }

  /**
   * Return the statements of the method into Strings
   *
   * @param tab the number of tabs
   *
   * @return the String containing the statements
   */
  private String toStringStatements(int tab) {
    StringBuffer buff = new StringBuffer(" {");
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        buff.append('\n').append(statements[i].toString(tab));
        if (!(statements[i] instanceof Block)) {
          buff.append(';');
        }
      }
    }
    buff.append('\n').append(tabString(tab == 0 ? 0 : tab - 1)).append('}');
    return buff.toString();
  }

  public void setParent(OutlineableWithChildren parent) {
    this.parent = parent;
  }

  public OutlineableWithChildren getParent() {
    return parent;
  }

  public boolean add(Outlineable o) {
    return children.add(o);
  }

  public Outlineable get(int index) {
    return (Outlineable) children.get(index);
  }

  public int size() {
    return children.size();
  }

  public String toString() {
    return methodHeader.toString();
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(List list) {
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(List list) {
  }

  /**
   * This method will analyze the code.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(List list) {
  }

  /** Get global variables (not parameters). */
  private void getGlobalVariable(List list) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].getOutsideVariable(list);
      }
    }
  }

  /** get the modified variables. */
  public void getAssignedVariableInCode(List list) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].getModifiedVariable(list);
      }
    }
  }

  /** Get the variables used. */
  private void getUsedVariableInCode(List list) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].getUsedVariable(list);
      }
    }
  }

  public VariableUsage getAssignedVariableInCode(String name, int line, int column) {
    List list = new ArrayList();
    getAssignedVariableInCode(list);
    VariableUsage found = null;
    for (int i = 0; i < list.size(); i++) {
      VariableUsage variableUsage = (VariableUsage) list.get(i);
      if (variableUsage.getEndLine() > line || (variableUsage.getEndLine() == line && variableUsage.getBeginColumn() > column)) {
        // We do not need variables declared after the given line
        break;
      }
      if (variableUsage.getName().equals(name) && (found == null || found.isDeclaredBefore(variableUsage))) {
        found = variableUsage;
      }
    }
    return found;
  }

  private static boolean isVariableDeclaredBefore(List list, VariableUsage var) {
    String name = var.getName();
    int pos = var.getSourceStart();
    for (int i = 0; i < list.size(); i++) {
      VariableUsage variableUsage = (VariableUsage) list.get(i);
      if (variableUsage.getName().equals(name) && variableUsage.getSourceStart() < pos) {
        return true;
      }
    }
    return false;
  }

  /** This method will analyze the code. */
  public void analyzeCode(PHPParser parser) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].analyzeCode(parser);
      }
    }

    List globalsVars = new ArrayList();
    getGlobalVariable(globalsVars);
    List modifiedVars = new ArrayList();
    getAssignedVariableInCode(modifiedVars);
    List parameters = new ArrayList(methodHeader.getArgumentsCount());
    methodHeader.getParameters(parameters);

    List declaredVars = new ArrayList(globalsVars.size() + modifiedVars.size());
    declaredVars.addAll(globalsVars);
    declaredVars.addAll(modifiedVars);
    declaredVars.addAll(parameters);

    List usedVars = new ArrayList();
    getUsedVariableInCode(usedVars);
    List readOrWriteVars = new ArrayList(modifiedVars.size() + usedVars.size());
    readOrWriteVars.addAll(modifiedVars);
    readOrWriteVars.addAll(usedVars);

    //look for used variables that were not declared before
    findUnusedParameters(parser, readOrWriteVars, parameters);
    findUnknownUsedVars(parser, usedVars, declaredVars);
  }

  /**
   * This method will add a warning on all unused parameters.
   *
   * @param vars       the used variable list
   * @param parameters the declared variable list
   */
  private static void findUnusedParameters(PHPParser parser, List vars, List parameters) {
    for (int i = 0; i < parameters.size(); i++) {
      VariableUsage param = (VariableUsage) parameters.get(i);
      if (!isVariableInList(param.getName(), vars)) {
        parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                                                         PHPParseMessageEvent.MESSAGE_UNUSED_PARAMETERS,
                                                         parser.getPath(),
                                                         "warning, the parameter " + param.getName() + " seems to be never used in your method",
                                                         param.getSourceStart(),
                                                         param.getSourceEnd(),
                                                         param.getBeginLine(),
                                                         param.getEndLine(),
                                                         param.getBeginColumn(),
                                                         param.getEndColumn()));
        /* fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                                               parser.getPath(),
                                               "You should use '<?php' instead of '<?' it will avoid some problems with XML",
                                               param.getSourceStart(),
                                               param.getSourceStart() + param.getName().length(),
                                               token.beginLine,
                                               token.endLine,
                                               token.beginColumn,
                                               token.endColumn));*/
      }
    }
  }

  /**
   * Tell if the list of VariableUsage contains a variable named by the name given.
   *
   * @param name the variable name
   * @param list the list of VariableUsage
   *
   * @return true if the variable is in the list false otherwise
   */
  private static boolean isVariableInList(String name, List list) {
    for (int i = 0; i < list.size(); i++) {
      if (((VariableUsage) list.get(i)).getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method will add a warning on all used variables in a method that aren't declared before.
   *
   * @param usedVars     the used variable list
   * @param declaredVars the declared variable list
   */
  private static void findUnknownUsedVars(PHPParser parser, List usedVars, List declaredVars) {
    Set list = new HashSet(usedVars.size());
    for (int i = 0; i < usedVars.size(); i++) {
      VariableUsage variableUsage = (VariableUsage) usedVars.get(i);
      if ("this".equals(variableUsage.getName())) continue; // this is a special variable
      if (!list.contains(variableUsage.getName()) && !isVariableDeclaredBefore(declaredVars, variableUsage)) {
        list.add(variableUsage.getName());
        parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                                                         PHPParseMessageEvent.MESSAGE_VARIABLE_MAY_BE_UNASSIGNED,
                                                         parser.getPath(),
                                                         "warning, usage of a variable that seems to be unassigned yet : " + variableUsage.getName(),
                                                         variableUsage.getSourceStart(),
                                                         variableUsage.getSourceEnd(),
                                                         variableUsage.getBeginLine(),
                                                         variableUsage.getEndLine(),
                                                         variableUsage.getBeginColumn(),
                                                         variableUsage.getEndColumn()));
      }
    }
  }

  public String getName() {
    return methodHeader.getName();
  }

  public MethodHeader getMethodHeader() {
    return methodHeader;
  }

  public void setStatements(Statement[] statements) {
    this.statements = statements;
  }

  public int getBodyLineStart() {
    return bodyLineStart;
  }

  public void setBodyLineStart(int bodyLineStart) {
    this.bodyLineStart = bodyLineStart;
  }

  public int getBodyColumnStart() {
    return bodyColumnStart;
  }

  public void setBodyColumnStart(int bodyColumnStart) {
    this.bodyColumnStart = bodyColumnStart;
  }

  public int getBodyLineEnd() {
    return bodyLineEnd;
  }

  public void setBodyLineEnd(int bodyLineEnd) {
    this.bodyLineEnd = bodyLineEnd;
  }

  public int getBodyColumnEnd() {
    return bodyColumnEnd;
  }

  public void setBodyColumnEnd(int bodyColumnEnd) {
    this.bodyColumnEnd = bodyColumnEnd;
  }
}
