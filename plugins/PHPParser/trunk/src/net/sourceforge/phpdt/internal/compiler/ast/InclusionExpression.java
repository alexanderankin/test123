package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;

import java.util.List;

/** @author Matthieu Casanova */
public final class InclusionExpression extends Expression implements Outlineable {
  public static final int INCLUDE = 0;
  public static final int INCLUDE_ONCE = 1;
  public static final int REQUIRE = 2;
  public static final int REQUIRE_ONCE = 3;
  public boolean silent;
  /** The kind of include. */
  private final int keyword;
  private final Expression expression;

  private transient final Object parent;

  /**
   * @deprecated 
   * @param parent
   * @param keyword
   * @param expression
   * @param sourceStart
   * @param sourceEnd
   */
  public InclusionExpression(Object parent,
                             int keyword,
                             Expression expression,
                             int sourceStart,
                             int sourceEnd) {
    this(parent, keyword, expression, sourceStart,
          sourceEnd,
          expression.getBeginLine(),
          expression.getEndLine(),
          expression.getBeginColumn(),
          expression.getEndColumn());
  }

  public InclusionExpression(Object parent,
                             int keyword,
                             Expression expression,
                             int sourceStart,
                             int sourceEnd,
                             int beginLine,
                             int endLine,
                             int beginColumn,
                             int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.keyword = keyword;
    this.expression = expression;
    this.parent = parent;
  }

  private String keywordToString() {
    switch (keyword) {
    case INCLUDE:
         return "include";
    case INCLUDE_ONCE:
         return "include_once";
    case REQUIRE:
         return "require";
    case REQUIRE_ONCE:
         return "require_once";
    }
    return "unknown keyword";
  }

  public String toStringExpression() {
    return toString();
  }

  public String toString() {
    final String keyword = keywordToString();
    final String expressionString = expression.toStringExpression();
    final StringBuffer buffer = new StringBuffer(keyword.length() +
                                                 expressionString.length() + 2);
    if (silent) {
      buffer.append('@');
    }
    buffer.append(keyword);
    buffer.append(' ');
    buffer.append(expressionString);
    return buffer.toString();
  }

  public Object getParent() {
    return parent;
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(List list) {
    expression.getOutsideVariable(list);
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(List list) {
    expression.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(List list) {
    expression.getUsedVariable(list);
  }

  public String getName() {
    //todo : change this
    return null;
  }
}
