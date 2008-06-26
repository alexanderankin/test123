// $Id$
package ise.plugin.calculator;

import ise.calculator.CalculatorPanel;
import javax.swing.*;
import org.gjt.sp.jedit.View;

/**
 * Wrap the calculator in a new panel so it is accessible to jEdit without
 * modifying the original calculator code.
 * @author Dale Anson, Dec 2003
 */
public class CalculatorPluginPanel extends JPanel {
   public CalculatorPluginPanel( View view ) {
      CalculatorPanel cp = new CalculatorPanel();
      cp.setCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      add( cp );
   }
}
