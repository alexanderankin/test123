/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.DFish.tools.calculator.parser;

import org.DFish.tools.calculator.helper.CalculatorOperator;

/**
 *
 * @author yuyiwei
 */
public class StackCounter extends LORCounter{

  private boolean pushed;
  
  public StackCounter(){
    super();
    
    pushed = false;
  }
  
  public StackCounter(Number l, CalculatorOperator o, Number r){
    super(l, o, r);
    
    pushed = false;
  }
  
  @Override
  public void clear(){
    super.clear();
    
    pushed = false;
  }

  public boolean isPushed() {
    return pushed;
  }

  public void setPushed(boolean pushed) {
    this.pushed = pushed;
  }
  
}
