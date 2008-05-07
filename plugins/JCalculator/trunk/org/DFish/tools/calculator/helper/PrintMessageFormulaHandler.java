/*
 * FormulaHandler.java
 *
 * Created on 2007/11/10 12:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.DFish.tools.calculator.helper;

/**
 *
 * @author Yiwen Yu
 */
public class PrintMessageFormulaHandler implements CalculatorHandler{
  
  /** Creates a new instance of FormulaHandler */
  public PrintMessageFormulaHandler() {
  }

  public void startFormula(String formula) throws Exception {
    System.out.println("[startFormula] <-- " + formula);
  }

  public void endFormula() throws Exception {
    System.out.println("[endFormula] <-- ");
  }

  public void error(String message) throws Exception {
    System.out.println("[error] <-- " + message);
  }

  public void fatalError(String message) {
    System.out.println("[fatalError] <--" + message);
  }

  public void number(Number n) throws Exception {
    System.out.println("[number] <-- " + n);
  }

  public void operator(CalculatorOperator o) throws Exception {
    System.out.println("[operate] <-- " + o.toString());
  }

  public void blank(char c) {
    System.out.println("[blank] <--");
  }

  public void currentPosition(int begin, int end) {
    System.out.println("[currentPosition] <-- ("+begin+","+end+")");
  }
}
