/*
 * CalculatorOperator.java
 *
 * Created on 2007/11/11 3:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.DFish.tools.calculator.helper;

/**
 *
 * @author Yiwen Yu
 */
public enum CalculatorOperator {
  LEFT_BRACKET(10000),
  RIGHT_BRACKET(10000),
  POWER(9999),
  MULTIPLY(11),
  DIVIDE(11),
  MODE(11),
  PLUS(10),
  DECREASE(10),
  LEFT_SHIFT(9),
  RIGHT_SHIFT(9),
  AND(6),
  XOR(5),
  OR(4);
  
  private int priority;
  public static final int MAX_PRIORITY = 10000;
  public static final int FUNCTION_PRIORITY = (MAX_PRIORITY-1);
  
  CalculatorOperator(int priority){
    this.priority = priority;
  }
  
  public int getPriority(){
    return priority;
  }
  
  public static int comparePriority(CalculatorOperator left, CalculatorOperator right){
    if(left.compareTo(right) == 0) return 0;
    
    return (left.priority - right.priority);
  }
}
