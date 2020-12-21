package com.lipstikLF.delegate;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class LipstikCheckBoxUI extends LipstikRadioButtonUI 
{
	/**	The property prefix for checkboxes */
	private final static String PROPERTY_PREFIX = "CheckBox."; 

	/**	If true, the defaults have already been initialized */
	private static boolean defaults_initialized = false;

	 
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
	public static ComponentUI createUI(JComponent c)
	{
		return _checkboxUI;
	}
	 

	/**
     * Installs the defaults for the specified AbstractButton
     *
     * @param b The button for which to install defaults.
     */
	public void installDefaults(AbstractButton b)
	{
		super.installDefaults(b);	
		if (!defaults_initialized)
		{		
			// Get icons
			selectedEnabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"selectedEnabledIcon");
			selectedDisabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"selectedDisabledIcon");
			unselectedEnabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"unselectedEnabledIcon");
			unselectedDisabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"unselectedDisabledIcon");			
					
			defaults_initialized = true;
		}
	}


	/**	Returns the icon for selected, enabled checkboxes */
	public Icon getSelectedEnabledIcon()
	{
		return selectedEnabledIcon;
	}
	
	
	/**	Returns the icon for selected, disabled checkboxes */
	public Icon getSelectedDisabledIcon()
	{
		return selectedDisabledIcon;
	}
	
	
	/**	Returns the icon for unselected, enabled checkboxes */
	public Icon getUnselectedEnabledIcon()
	{
		return unselectedEnabledIcon;
	}
	
	
	/**	Returns the icon for unselected, disabled checkboxes */
	public Icon getUnselectedDisabledIcon()
	{
		return unselectedDisabledIcon;
	}	
	
	
	/**	Returns the property prefix for checkboxes */
	public String getPropertyPrefix() 
	{
		return PROPERTY_PREFIX;
	}

    /**
     * One instance handles all buttons.
     */
    private static LipstikCheckBoxUI _checkboxUI = new LipstikCheckBoxUI();    
}
