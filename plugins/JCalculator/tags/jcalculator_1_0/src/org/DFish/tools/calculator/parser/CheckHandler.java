/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.DFish.tools.calculator.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.JTextComponent;
import org.DFish.tools.calculator.helper.CalculatorOperator;
import org.DFish.tools.calculator.helper.PointOutErrorAbility;

/**
 *
 * @author yuyiwei
 */
public class CheckHandler extends ScanHandler implements PointOutErrorAbility {

  String msg;
  
  public CheckHandler() {
    super();
  }

  @Override
  public void number(Number n) throws Exception {
    try {
      super.number(n);
    } catch (Exception e) {
      throw new Exception(reportError());
    }
  }

  @Override
  public void operator(CalculatorOperator o) throws Exception {
    try {
      super.operator(o);
    } catch (Exception e) {
      throw new Exception(reportError());
    }
  }

  protected String reportError() {
    msg = "<error begin=" + curPositionBegin + ", end=" + curPositionEnd + ">";
    return msg;
  }

  public int getBeginPosition(String message) {
    String regEx = "^<error begin=(.*), end=";
    Pattern p;
    Matcher m;
    
    p = Pattern.compile(regEx);
    m = p.matcher(message);

    if(m.find()){
      return Integer.parseInt(m.group(1));
    } else {
      return -1;
    }
  }

  public int getEndPosition(String message) {
    String regEx = "^<error begin=.*, end=(.*)>";
    Pattern p;
    Matcher m;
    
    p = Pattern.compile(regEx);
    m = p.matcher(message);

    if(m.find()){
      return Integer.parseInt(m.group(1));
    } else {
      return -1;
    }
  }

  public String getLastError() {
    return msg;
  }

  public void pointOutLastError(JTextComponent component) {
    component.setSelectionStart(curPositionBegin);
    component.setSelectionEnd(curPositionEnd);
  }

  public void pointOutError(String message, JTextComponent component) {
    component.setSelectionStart(getBeginPosition(message));
    component.setSelectionEnd(getEndPosition(message));
  }
}
