/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.DFish.tools.calculator.parser;

import org.DFish.tools.calculator.helper.CalculatorOperator;

/**
 *
 * @author yuyiwei
 * LOR = Left + Operator + Right
 */
public class LORCounter {

  private Number left;
  private Number right;
  private CalculatorOperator operator;

  public LORCounter() {
    clear();
  }

  public LORCounter(Number l, CalculatorOperator o, Number r) {
    left = l;
    operator = o;
    right = r;
  }

  public void clear() {
    // the default LOR is "0+null"
    left = 0L; // Long class
    operator = CalculatorOperator.PLUS;
    right = null;
  }

  public void setLeft(Number n) {
    left = n;
  }

  public void setOperator(CalculatorOperator o) {
    operator = o;
  }

  public void setRight(Number n) {
    right = n;
  }

  public Number getLeft() {
    return left;
  }

  public Number getRight() {
    return right;
  }

  public CalculatorOperator getOperator() {
    return operator;
  }

  public int compareOperator(CalculatorOperator o) {
    return CalculatorOperator.comparePriority(getOperator(), o);
  }

  protected Long countAsLong(Long lLeft, CalculatorOperator o, Long lRight) {
    Long result = null;

    switch (o) {
      case PLUS:
        result = lLeft + lRight;
        break;
      case DECREASE:
        result = lLeft - lRight;
        break;
      case MULTIPLY:
        result = lLeft * lRight;
        break;
      case DIVIDE:
        result = lLeft / lRight;
        break;
      case MODE:
        result = lLeft % lRight;
        break;
      case LEFT_SHIFT:
        result = (lLeft << lRight);
        break;
      case RIGHT_SHIFT:
        result = (lLeft >> lRight);
        break;
      case AND:
        result = (lLeft & lRight);
        break;
      case XOR:
        result = (lLeft ^ lRight);
        break;
      case OR:
        result = (lLeft | lRight);
        break;
      case POWER:
        result = (long)Math.pow(lLeft.doubleValue(), lRight.doubleValue());
        break;
      default:
        throw new UnsupportedOperationException();
    }

    return result;
  }

  protected Double countAsDouble(Double dLeft, CalculatorOperator o, Double dRight) {
    Double result = null;

    switch (o) {
      case PLUS:
        result = dLeft + dRight;
        break;
      case DECREASE:
        result = dLeft - dRight;
        break;
      case MULTIPLY:
        result = dLeft * dRight;
        break;
      case DIVIDE:
        result = dLeft / dRight;
        break;
      case POWER:
        result = Math.pow(dLeft, dRight);
        break;
      default:
        throw new UnsupportedOperationException();
    }

    return result;
  }

  public Number count() {
    if (right == null) {
      return null;
    }

    Number result = null;

    try {
      switch (operator) {
        case MODE:
        case LEFT_SHIFT:
        case RIGHT_SHIFT:
        case AND:
        case OR:
        case XOR:
          // especial for interger operators
          result = countAsLong(getLeft().longValue(), getOperator(), getRight().longValue());
          break;
        default:
          if (left.getClass().equals(Long.class) && right.getClass().equals(Long.class)) {
            result = countAsLong(left.longValue(), operator, right.longValue());
          } else if (left.getClass().equals(Double.class) || right.getClass().equals(Double.class)) {
            result = countAsDouble(left.doubleValue(), operator, right.doubleValue());
          }
      }
    } catch (UnsupportedOperationException ex) {
      result = null;
    }

    return result;
  }
}
