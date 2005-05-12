package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

import java.util.List;

import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.sidekick.PHPAsset;
import gatchan.phpparser.sidekick.IncludeAsset;
import sidekick.Asset;

import javax.swing.text.Position;

/** @author Matthieu Casanova */
public final class InclusionExpression extends Expression implements Outlineable {
  public static final int INCLUDE = 0;
  public static final int INCLUDE_ONCE = 1;
  public static final int REQUIRE = 2;
  public static final int REQUIRE_ONCE = 3;
  private boolean silent;
  /** The kind of include. */
  private final int keyword;
  private final Expression expression;

  private final transient OutlineableWithChildren parent;

  public InclusionExpression(OutlineableWithChildren parent,
                             int keyword,
                             Expression expression,
                             int sourceStart,
                             int sourceEnd,
                             int beginLine,
                             int endLine,
                             int beginColumn,
                             int endColumn) {
    super(Type.INTEGER, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
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
      default:
        return "unknown keyword";
    }
  }

  public String toStringExpression() {
    return toString();
  }

  public String toString() {
    String keyword = keywordToString();
    String expressionString = expression.toStringExpression();
    StringBuffer buffer = new StringBuffer(keyword.length() +
                                           expressionString.length() + 2);
    if (silent) {
      buffer.append('@');
    }
    buffer.append(keyword);
    buffer.append(' ');
    buffer.append(expressionString);
    return buffer.toString();
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

  public int getItemType() {
    return PHPItem.INCLUDE;
  }

  public Asset getAsset(Position start, Position end) {
    return new IncludeAsset(toString(),start, end);
  }
}
