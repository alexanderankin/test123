package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

/**
 * A variable.
 * It could be a simple variable, or contains another variable.
 * @author Matthieu Casanova
 */
public final class Variable extends AbstractVariable {

  /** The name of the variable. */
  private String name;

  /** A variable inside ($$varname). */
  private AbstractVariable variable;

  /** the variable is defined like this ${expression} */
  private Expression expression;

  private static final String _GET = "_GET";
  private static final String _POST = "_POST";
  private static final String _REQUEST = "_REQUEST";
  private static final String _SERVER = "_SERVER";
  private static final String _SESSION = "_SESSION";
  private static final String _this = "this";
  private static final String GLOBALS = "GLOBALS";
  private static final String _COOKIE = "_COOKIE";
  private static final String _FILES = "_FILES";
  private static final String _ENV = "_ENV";

  /** Here is an array of all superglobals variables and the special "this". */
  public static final String[] SPECIAL_VARS = {_GET,
                                               _POST,
                                               _REQUEST,
                                               _SERVER,
                                               _SESSION,
                                               _this,
                                               GLOBALS,
                                               _COOKIE,
                                               _FILES,
                                               _ENV};

  /**
   * Create a new simple variable.
   * @param name the name
   * @param sourceStart the starting position
   * @param sourceEnd the ending position
   */
  public Variable(final String name,
                  final int sourceStart,
                  final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.name = name;
  }

  /**
   * Create a special variable ($$toto for example).
   * @param variable the variable contained
   * @param sourceStart the starting position
   * @param sourceEnd the ending position
   */
  public Variable(final AbstractVariable variable,
                  final int sourceStart,
                  final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.variable = variable;
  }

  /**
   * Create a special variable ($$toto for example).
   * @param expression the variable contained
   * @param sourceStart the starting position
   * @param sourceEnd the ending position
   */
  public Variable(final Expression expression,
                  final int sourceStart,
                  final int sourceEnd) {
    super(sourceStart, sourceEnd);
    this.expression = expression;
  }

  /**
   * Return the expression as String.
   * @return the expression
   */
  public String toStringExpression() {
    return '$' + getName();
  }

  public String getName() {
    if (name != null) {
      return name;
    }
    if (variable != null) {
      return variable.toStringExpression();
    }
    return '{' + expression.toStringExpression() + '}';
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   */
  public void getOutsideVariable(final List list) {
  }

  /**
   * get the modified variables.
   */
  public void getModifiedVariable(final List list) {
  }

  /**
   * Get the variables used.
   */
  public void getUsedVariable(final List list) {
    final String varName;
    if (name != null) {
      varName = name;
    } else if (variable != null) {
      varName = variable.getName();
    } else {
      varName = expression.toStringExpression();//todo : do a better thing like evaluate this ??
    }
    if (!arrayContains(SPECIAL_VARS, name)) {
      list.add(new VariableUsage(varName, sourceStart));
    }
  }
}
