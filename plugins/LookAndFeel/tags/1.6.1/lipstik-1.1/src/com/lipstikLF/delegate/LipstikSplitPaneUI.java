package com.lipstikLF.delegate;


import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class LipstikSplitPaneUI extends BasicSplitPaneUI
{
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */    
    public static ComponentUI createUI(JComponent c)
	{
		return new LipstikSplitPaneUI();
	}
	
	/**	Creates the default divider */
	public BasicSplitPaneDivider createDefaultDivider() 
	{
		return new LipstikSplitPaneDivider(this);
	}    
	
}
