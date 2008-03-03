/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.DFish.tools.calculator;

import javax.swing.text.JTextComponent;
import org.DFish.tools.calculator.helper.CalculatorParser;
import org.DFish.tools.calculator.helper.PointOutErrorAbility;
import org.DFish.tools.calculator.parser.CheckHandler;
import org.DFish.tools.calculator.parser.FormulaParser;
import org.DFish.tools.calculator.parser.ScanHandler;
import org.DFish.tools.calculator.parser.StackHandler;

/**
 *
 * @author yuyiwei
 */
public class JCalculatorParser {

  private CalculatorParser parser;
  private ScanHandler scanHandler;
  private StackHandler handler;

  public JCalculatorParser() {
    scanHandler = new CheckHandler();
    handler = new StackHandler();

    parser = new FormulaParser();
  }

  public Number calculate(String str) {
    parser.applyHandler(scanHandler);
    if (parser.parse(str) != CalculatorParser.PARSER_OK) {
      return null;
    }

    str = scanHandler.getScanedFormula();
    if (str == null) {
      return null;
    }

    parser.applyHandler(handler);
    parser.parse(str);
    
    return handler.getResult();
  }
  
  public void PointOutError(JTextComponent text){
    ((PointOutErrorAbility)scanHandler).pointOutLastError(text);
  }
}
