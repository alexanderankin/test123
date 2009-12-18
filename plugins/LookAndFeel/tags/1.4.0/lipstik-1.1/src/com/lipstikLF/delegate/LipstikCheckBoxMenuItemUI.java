package com.lipstikLF.delegate;


import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;


public class LipstikCheckBoxMenuItemUI extends LipstikRadioButtonMenuItemUI
{
    protected String getPropertyPrefix() 
    { 
    	return "CheckBoxMenuItem"; 
    }

    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikCheckBoxMenuItemUI();
    }
}
