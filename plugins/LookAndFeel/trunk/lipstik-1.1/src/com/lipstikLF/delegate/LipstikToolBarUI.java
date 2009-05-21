package com.lipstikLF.delegate;

import com.lipstikLF.util.LipstikBorderFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

public class LipstikToolBarUI extends BasicToolBarUI
{
  /**
   * Create the ui delegate for the given component
   *
   * @param c The component for which to create the ui delegate
   * @return The created ui delegate
   */
  public static ComponentUI createUI (JComponent c)
  {
      return new LipstikToolBarUI();
  }

  /**
   * Create a rollover border. This border will be used if rollover borders are enabled.
   */
  protected Border createRolloverBorder ()
  {
      return LipstikBorderFactory.getButtonToolBorder();
  }
}
