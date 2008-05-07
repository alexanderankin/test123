package org.DFish.tools.calculator.helper;

public class DefaultHandler implements CalculatorHandler {
  
  public void endFormula() throws Exception {
    //do nothing
  }
  
  public void error(String message) throws Exception {
    //do nothing
  }
  
  public void fatalError(String message) {
    //do nothing
  }
  
  public void operator(CalculatorOperator operator) throws Exception {
    //do nothing
  }
  
  public void startFormula(String formula) throws Exception {
    //do nothing
  }

  public void blank(char c) {
    // do nothing
  }

  public void number(Number n) throws Exception {
    //do nothing
  }

  public void currentPosition(int begin, int end) {
    //do nothing
  }
  
}
