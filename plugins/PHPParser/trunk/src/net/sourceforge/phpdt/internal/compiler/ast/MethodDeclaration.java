package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParser;

/**
 * A Method declaration.
 *
 * @author Matthieu Casanova
 */
public final class MethodDeclaration extends Statement implements OutlineableWithChildren {

  /**
   * The name of the method.
   */
  public final String name;
  private final ArrayList arguments;


  public Statement[] statements;
  private final int bodyStart;
  private int bodyEnd = -1;
  /**
   * Tell if the method is a class constructor.
   */
  public boolean isConstructor;

  /**
   * The parent object.
   */
  private Object parent;
  /**
   * The outlineable children (those will be in the node array too.
   */
  private final ArrayList children = new ArrayList();

  /**
   * Tell if the method returns a reference.
   */
  private final boolean reference;

  public MethodDeclaration(final Object parent,
                           final String name,
                           final ArrayList arguments,
                           final boolean reference,
                           final int sourceStart,
                           final int sourceEnd,
                           final int bodyStart,
                           final int bodyEnd) {
    super(sourceStart, sourceEnd);
    this.name = name;
    this.arguments = arguments;
    this.parent = parent;
    this.reference = reference;
    this.bodyStart = bodyStart;
    this.bodyEnd = bodyEnd;
  }

  /**
   * Return method into String, with a number of tabs
   *
   * @param tab the number of tabs
   * @return the String containing the method
   */
  public String toString(final int tab) {
    final StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append(toStringHeader());
    buff.append(toStringStatements(tab + 1));
    return buff.toString();
  }

  private String toStringHeader() {
    return "function " + toString();
  }

  /**
   * Return the statements of the method into Strings
   *
   * @param tab the number of tabs
   * @return the String containing the statements
   */
  private String toStringStatements(final int tab) {
    final StringBuffer buff = new StringBuffer(" {"); //$NON-NLS-1$
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        buff.append("\n").append(statements[i].toString(tab)); //$NON-NLS-1$
        if (!(statements[i] instanceof Block)) {
          buff.append(";"); //$NON-NLS-1$
        }
      }
    }
    buff.append("\n").append(tabString(tab == 0 ? 0 : tab - 1)).append("}"); //$NON-NLS-2$ //$NON-NLS-1$
    return buff.toString();
  }

  public void setParent(final Object parent) {
    this.parent = parent;
  }

  public Object getParent() {
    return parent;
  }

  public boolean add(final Outlineable o) {
    return children.add(o);
  }

  public Outlineable get(final int index) {
    return (Outlineable) children.get(index);
  }

  public int size() {
    return children.size();
  }

  public String toString() {
    final StringBuffer buff = new StringBuffer();
    if (reference) {
      buff.append("&");//$NON-NLS-1$
    }
    buff.append(name).append("(");//$NON-NLS-1$

    if (arguments != null) {
      for (int i = 0; i < arguments.size(); i++) {
        final VariableDeclaration o = (VariableDeclaration) arguments.get(i);
        buff.append(o.toStringExpression());
        if (i != (arguments.size() - 1)) {
          buff.append(", "); //$NON-NLS-1$
        }
      }
    }
    buff.append(")"); //$NON-NLS-1$
    return buff.toString();
  }

  public List getList() {
    return children;
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
   * This method will analyze the code.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
  }

  /**
   * Get global variables (not parameters).
   */
  private void getGlobalVariable(final List list) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].getOutsideVariable(list);
      }
    }
  }

  private void getParameters(final List list) {
    if (arguments != null) {
      for (int i = 0; i < arguments.size(); i++) {
        final VariableDeclaration variable = (VariableDeclaration) arguments.get(i);
        final VariableUsage variableUsage = new VariableUsage(variable.name(), variable.sourceStart,variable.getBeginLine(),variable.getBeginColumn());
        list.add(variableUsage);
      }
    }
  }

  /**
   * get the modified variables.
   */
  private void getAssignedVariableInCode(final List list) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].getModifiedVariable(list);
      }
    }
  }

  /**
   * Get the variables used.
   */
  private void getUsedVariableInCode(final List list) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].getUsedVariable(list);
      }
    }
  }

  private static boolean isVariableDeclaredBefore(final List list, final VariableUsage var) {
    final String name = var.getName();
    final int pos = var.getStartOffset();
    for (int i = 0; i < list.size(); i++) {
      final VariableUsage variableUsage = (VariableUsage) list.get(i);
      if (variableUsage.getName().equals(name) && variableUsage.getStartOffset() < pos) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method will analyze the code.
   */
  public void analyzeCode(PHPParser parser) {
    if (statements != null) {
      for (int i = 0; i < statements.length; i++) {
        statements[i].analyzeCode(parser);

      }
    }

    final List globalsVars = new ArrayList();
    getGlobalVariable(globalsVars);
    final List modifiedVars = new ArrayList();
    getAssignedVariableInCode(modifiedVars);
    final List parameters = new ArrayList(arguments.size());
    getParameters(parameters);

    final List declaredVars = new ArrayList(globalsVars.size() + modifiedVars.size());
    declaredVars.addAll(globalsVars);
    declaredVars.addAll(modifiedVars);
    declaredVars.addAll(parameters);

    final List usedVars = new ArrayList();
    getUsedVariableInCode(usedVars);
    final List readOrWriteVars = new ArrayList(modifiedVars.size() + usedVars.size());
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
  private static void findUnusedParameters(PHPParser parser, final List vars, final List parameters) {
    for (int i = 0; i < parameters.size(); i++) {
      final VariableUsage param = (VariableUsage) parameters.get(i);
      if (!isVariableInList(param.getName(), vars)) {
        parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                                                         parser.getPath(),
                                                         "warning, the parameter " + param.getName() + " seems to be never used in your method",
                                                         param.getStartOffset(),
                                                         param.getStartOffset() + param.getName().length(),
                                                         param.getLine(),
                                                         param.getLine(),
                                                         param.getColumn(),
                                                         param.getColumn()+ param.getName().length()));
        /* fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                                               parser.getPath(),
                                               "You should use '<?php' instead of '<?' it will avoid some problems with XML",
                                               param.getStartOffset(),
                                               param.getStartOffset() + param.getName().length(),
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
   * @return true if the variable is in the list false otherwise
   */
  private static boolean isVariableInList(final String name, final List list) {
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
  private static void findUnknownUsedVars(PHPParser parser, final List usedVars, final List declaredVars) {
    final HashSet list = new HashSet(usedVars.size());
    for (int i = 0; i < usedVars.size(); i++) {
      final VariableUsage variableUsage = (VariableUsage) usedVars.get(i);
      if ("this".equals(variableUsage.getName())) continue; // this is a special variable
      if (!list.contains(variableUsage.getName()) && !isVariableDeclaredBefore(declaredVars, variableUsage)) {
        list.add(variableUsage.getName());
        parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
                                                         parser.getPath(),
                                                         "warning, usage of a variable that seems to be unassigned yet : " + variableUsage.getName(),
                                                         variableUsage.getStartOffset(),
                                                         variableUsage.getStartOffset() + variableUsage.getName().length(),
                                                         variableUsage.getLine(),
                                                         variableUsage.getLine(),
                                                         variableUsage.getColumn(),
                                                         variableUsage.getColumn() + variableUsage.getName().length()));
      }
    }
  }
}
