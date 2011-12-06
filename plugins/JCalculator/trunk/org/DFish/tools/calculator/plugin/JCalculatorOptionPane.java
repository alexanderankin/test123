/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.DFish.tools.calculator.plugin;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 *
 * @author yuyiwei
 */
public class JCalculatorOptionPane extends AbstractOptionPane {
  JCheckBox changeOperator;
  JCheckBox toUpcase;
  JCheckBox removeblank;
  JCheckBox onlyResult;

  public JCalculatorOptionPane(){
    super("jcalculatorOption");
  }

  @Override
  public void _init(){
    addComponent(jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"name.label"), new JLabel(JCalculatorPlugin.NAME));
    addComponent(jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"author.label"), new JLabel(JCalculatorPlugin.AUTHOR));
    addComponent(jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"version.label"), new JLabel(JCalculatorPlugin.VERSION));
    
    changeOperator = new JCheckBox(jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"changeOperator.title"),
      jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"changeOperator").equals("true")
      );
    addComponent(changeOperator);
    
    toUpcase = new JCheckBox(jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"toUpcase.title"),
      jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"toUpcase").equals("true")
      );
    addComponent(toUpcase);
    
    removeblank = new JCheckBox(jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"removeblank.title"),
      jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"removeblank").equals("true")
      );
    addComponent(removeblank);
    
    onlyResult = new JCheckBox(jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"onlyResult.title"),
      jEdit.getProperty(JCalculatorPlugin.OPTION_PREFIX+"onlyResult").equals("true")
      );
    addComponent(onlyResult);
  }
  
  @Override
  public void _save(){
    jEdit.setProperty(JCalculatorPlugin.OPTION_PREFIX+"changeOperator", String.valueOf(changeOperator.isSelected()) );
    jEdit.setProperty(JCalculatorPlugin.OPTION_PREFIX+"toUpcase", String.valueOf(toUpcase.isSelected()) );
    jEdit.setProperty(JCalculatorPlugin.OPTION_PREFIX+"removeblank", String.valueOf(removeblank.isSelected()) );
    jEdit.setProperty(JCalculatorPlugin.OPTION_PREFIX+"onlyResult", String.valueOf(onlyResult.isSelected()) );
  }
}
