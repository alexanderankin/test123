package org.DFish.tools.calculator.helper;

public interface CalculatorParser {
  public final int PARSER_OK = 0;
  public final int PARSER_ERROR = 1;
  public final int PARSER_FATAL_ERROR = 2;
  public final int PARSER_TERMINATE = 3;
  
  public void applyHandler(CalculatorHandler handler);
  public int parse (String formula);
  public void terminate ();
}
