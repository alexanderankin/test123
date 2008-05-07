package org.DFish.tools.calculator.helper;

public interface CalculatorHandler {
  public void startFormula(String formula) throws Exception;
  public void endFormula() throws Exception;
  public void error(String message) throws Exception;
  public void fatalError(String message);
  public void number(Number n) throws Exception;
  public void operator(CalculatorOperator o) throws Exception;
  public void blank(char c);
  public void currentPosition(int begin, int end);
}
