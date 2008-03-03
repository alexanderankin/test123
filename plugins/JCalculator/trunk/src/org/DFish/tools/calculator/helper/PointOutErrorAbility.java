/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.DFish.tools.calculator.helper;

import javax.swing.text.JTextComponent;


/**
 *
 * @author yuyiwei
 */
public interface PointOutErrorAbility {
  public String getLastError();
  public void pointOutLastError(JTextComponent component);
  public void pointOutError(String message, JTextComponent component);
}
