package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

/**
 * A ConditionalExpression is like that : booleanExpression ? trueValue : falseValue;.
 * @author Matthieu Casanova
 */
public final class ConditionalExpression extends OperatorExpression {

  private final Expression condition;
  private final Expression valueIfTrue;
  private final Expression valueIfFalse;

  public ConditionalExpression(final Expression condition,
                               final Expression valueIfTrue,
                               final Expression valueIfFalse) {
    super(-1, condition.sourceStart, valueIfFalse.sourceEnd);
    this.condition = condition;
    this.valueIfTrue = valueIfTrue;
    this.valueIfFalse = valueIfFalse;
  }

  public String toStringExpression() {
    final String conditionString = condition.toStringExpression();
    final String valueIfTrueString = valueIfTrue.toStringExpression();
    final String valueIfFalse = this.valueIfFalse.toStringExpression();
    final StringBuffer buff = new StringBuffer(8 +
                                               conditionString.length() +
                                               valueIfTrueString.length() +
                                               valueIfFalse.length());
    buff.append("(");
    buff.append(conditionString);
    buff.append(") ? ");
    buff.append(valueIfTrueString);
    buff.append(" : ");
    buff.append(valueIfFalse);
    return buff.toString();
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
  public void getModifiedVariable(final List list) {
    condition.getModifiedVariable(list);
    valueIfTrue.getModifiedVariable(list);
    valueIfFalse.getModifiedVariable(list);
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(final List list) {
    condition.getUsedVariable(list);
    valueIfTrue.getUsedVariable(list);
    valueIfFalse.getUsedVariable(list);
  }
}
