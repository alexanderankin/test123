/*
 * ScanHandler.java
 *
 * Created on 2007/11/118:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.DFish.tools.calculator.parser;

import org.DFish.tools.calculator.helper.CalculatorHandler;
import org.DFish.tools.calculator.helper.CalculatorOperator;

/**
 *
 * @author Yiwen Yu
 */
public class ScanHandler implements CalculatorHandler {

  protected int leftBracketCount = 0;
  protected int rightBracketCount = 0;
  protected boolean nextCouldBeLeftBracket;
  protected boolean nextCouldBeRightBracket;
  protected boolean nextCouldBeNumber;
  protected boolean nextCouldBeOperator;
  protected boolean nextCouldBeDescrase;
  protected boolean replaceDescraseWithBrackets;
  protected StringBuffer scanString = null;
  protected int curPositionBegin,  curPositionEnd;
  protected int appendedSize;

  /**
   * Creates a new instance of ScanHandler
   */
  public ScanHandler() {
    readyNow();
  }

  protected void readyNow() {
    leftBracketCount = 0;
    rightBracketCount = 0;

    nextCouldBeLeftBracket = true;
    nextCouldBeNumber = true;
    nextCouldBeRightBracket = false;
    nextCouldBeOperator = false;
    nextCouldBeDescrase = true;

    replaceDescraseWithBrackets = false;

    appendedSize = 0;
    curPositionBegin = 0;
    curPositionEnd = 0;
  }

  protected void clearAllCouldBe() {
    nextCouldBeLeftBracket = false;
    nextCouldBeNumber = false;
    nextCouldBeRightBracket = false;
    nextCouldBeOperator = false;
    nextCouldBeDescrase = false;
  }

  protected boolean checkBracket() {
    if ((leftBracketCount == 0) && (rightBracketCount == 0)) {
      return true;
    }

    return (leftBracketCount != rightBracketCount) ? false : true;
  }

  public String getScanedFormula() {
    return (scanString == null) ? null : scanString.toString();
  }

  //@Override
  public void startFormula(String formula) throws Exception {
    scanString = new StringBuffer(formula);

    readyNow();
  }

  //@Override
  public void endFormula() throws Exception {
    if (!checkBracket()) {
      throw new Exception("The number of left brackets is not equal to right brackets");
    }
  }

  //@Override
  public void error(String message) throws Exception {
    throw new Exception(" Find error:" + message);
  }

  //@Override
  public void fatalError(String message) {
  }

  //@Override
  public void number(Number n) throws Exception {
    if (!nextCouldBeNumber) {
      throw new Exception("No number(" + n + " is expected here");
    }

    if (replaceDescraseWithBrackets) {
      scanString.insert(appendedSize + curPositionEnd, ')');
      appendedSize++;

      // clean the flag
      replaceDescraseWithBrackets = false;
    }

    /*
    setup which could be the next one
    such as 
    8 + 
    8 )
     */
    clearAllCouldBe();
    nextCouldBeOperator = true;
    nextCouldBeRightBracket = true;
  }

  //@Override
  public void operator(CalculatorOperator o) throws Exception {
    if (o.equals(CalculatorOperator.LEFT_BRACKET)) {
      if (!nextCouldBeLeftBracket) {
        throw new Exception("No left bracket excpeted here");
      }

      leftBracketCount++;

      /*
      setup which could be the next one
      such as 
      ( 2
      ( (
       */
      clearAllCouldBe();
      nextCouldBeLeftBracket = true;
      nextCouldBeNumber = true;

      return;
    }

    if (o.equals(CalculatorOperator.RIGHT_BRACKET)) {
      if (!nextCouldBeRightBracket) {
        throw new Exception("No right bracket excpeted here");
      }

      rightBracketCount++;

      /*
      setup which could be the next one
      such as 
      ) + 
      ) )
       */
      clearAllCouldBe();
      nextCouldBeOperator = true;
      nextCouldBeRightBracket = true;

      return;
    }

    // if the first operator is '-' we will change it as (0-number)
    if (nextCouldBeDescrase && (o.equals(CalculatorOperator.DECREASE))) {
      scanString.insert(appendedSize + curPositionBegin, "(0");
      appendedSize += "(0".length();

      /*
      setup which could be the next one
      such as 
      -2 ==> (0 - 2
       */
      clearAllCouldBe();
      nextCouldBeNumber = true;

      // setup the flag
      replaceDescraseWithBrackets = true;

      return;
    }

    if (!nextCouldBeOperator) {
      throw new Exception("No operator(" + o + ") is expected here");
    }

    /*
    setup which could be the next one
    such as 
    + 8
    + (
     */
    clearAllCouldBe();
    nextCouldBeLeftBracket = true;
    nextCouldBeNumber = true;
  }

  //@Override
  public void blank(char c) {
    scanString.delete(appendedSize + curPositionBegin, appendedSize + curPositionEnd);
    appendedSize -= 1;
  }

  //@Override
  public void currentPosition(int begin, int end) {
    curPositionBegin = begin;
    curPositionEnd = end;
  }
}
