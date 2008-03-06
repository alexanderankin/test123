/*
 * OperatorParserInfo.java
 *
 * Created on 2007/11/117:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.DFish.tools.calculator.info;

import org.DFish.tools.calculator.helper.CalculatorOperator;

/**
 *
 * @author Yiwen Yu
 */
public class OperatorParserInfo{
  public char[]              source;
  public boolean             find;
  public int                 checkedCharNumber;
  public int                 indexBeginCheck;
  public CalculatorOperator  operator;
}

