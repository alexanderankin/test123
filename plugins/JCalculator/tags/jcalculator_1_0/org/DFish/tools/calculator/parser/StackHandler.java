/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.DFish.tools.calculator.parser;

import java.util.ArrayList;
import org.DFish.tools.calculator.helper.CalculatorHandler;
import org.DFish.tools.calculator.helper.CalculatorOperator;

/**
 *
 * @author yuyiwei
 * We don't scan the input formula, please lauch ScanHandler firstly
 */
public class StackHandler implements CalculatorHandler {

  private ArrayList<StackCounter> array = null;
  private StackCounter currentCounter,  baseCounter = null;
  private int currentPriority = 0; // default is same priority
  private Number result;

  public StackHandler() {
  //readyNow();
  }

  protected void readyNow() {
    if (array == null) {
      array = new ArrayList<StackCounter>();
    } else {
      array.clear();
    }

    if (baseCounter == null) {
      baseCounter = new StackCounter();
    }
    
    baseCounter.clear();

    array.add(baseCounter);

    currentCounter = baseCounter;
    
    result = null;
  }

  protected void pop() {
    int size = array.size();
    StackCounter c = array.get(size - 2);
    c.setRight(currentCounter.count());
    
    array.remove(size-1);
    currentCounter = c;
  }

  protected void push() {
    currentCounter = new StackCounter();
    currentCounter.setPushed(true);
    
    array.add(currentCounter);
  }

  protected void lowerPriority(CalculatorOperator o) {
    if(currentCounter.isPushed()){
      currentCounter.setLeft(currentCounter.count());
      currentCounter.setOperator(o);

      return;
    }
    
    if (currentCounter.equals(baseCounter)) {
      currentCounter.setLeft(currentCounter.count());
      currentCounter.setOperator(o);

      return;
    }

    int size = array.size();
    StackCounter c = array.get(size - 2);
    currentPriority = c.compareOperator(o);
    if (currentPriority > 0) {
      currentCounter.setLeft(currentCounter.count());
      currentCounter.setOperator(o);
    } else {
      c.setRight(currentCounter.count());
      c.setLeft(c.count());
      c.setOperator(o);

      array.remove(size - 1); // remove the last one -- currentCounter
      currentCounter = c;
    }
  }

  protected void samePriority(CalculatorOperator o) {
    currentCounter.setLeft(currentCounter.count());
    currentCounter.setOperator(o);
  }

  protected void higherPriority(CalculatorOperator o) {
    currentCounter = new StackCounter(currentCounter.getRight(), o, null);
    array.add(currentCounter);
  }

  public void startFormula(String formula) throws Exception {
    readyNow();
  }

  public void endFormula() throws Exception {
    if (array.isEmpty()) {
      throw new Exception("The array is empty!");
    }
    
    int size = array.size();
    StackCounter c;
    result = null;
    for(int i = size-1; i >= 0; i--){
      c = array.get(i);
      if(result != null) c.setRight(result);
      result = c.count();
    }
  }

  public void error(String message) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void fatalError(String message) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void number(Number n) throws Exception {
    currentCounter.setRight(n);
  }

  public void operator(CalculatorOperator o) throws Exception {
    switch (o) {
      case LEFT_BRACKET:
        push();
        break;
      case RIGHT_BRACKET:
        pop();
        break;
      default:
        currentPriority = currentCounter.compareOperator(o);
        if (currentPriority > 0) {
          lowerPriority(o);
        } else if (currentPriority == 0) {
          samePriority(o);
        } else {
          higherPriority(o);
        }
    }
  }

  public void blank(char c) {
  // we don't deal with blank character(s)
  }

  public void currentPosition(int begin, int end) {
  // do nothing
  }

  public Number getResult() {
    return result;
  }
}
