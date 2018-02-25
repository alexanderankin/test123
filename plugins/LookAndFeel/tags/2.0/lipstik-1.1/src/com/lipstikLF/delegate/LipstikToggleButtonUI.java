package com.lipstikLF.delegate;


import com.lipstikLF.util.LipstikBorderFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

public class LipstikToggleButtonUI extends com.lipstikLF.delegate.LipstikButtonUI
{
	private final static Border defaultBorder = LipstikBorderFactory.getButtonPushBorder();
	private final static String propertyPrefix = "ToggleButton" + ".";


    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
	public static ComponentUI createUI(JComponent c)
	{
		return _toggleButtonUI;
	}


	/**	Returns the property prefix for ToggleButtons */
	protected String getPropertyPrefix()
	{
		return propertyPrefix;
	}


    /**
     * Install the UI delegate for the given component
     *
     * @param c The component for which to install the ui delegate.
     */
	public void installUI(JComponent c)
	{
		super.installUI(c);

		if(c.getBorder()==null || (c.getBorder() instanceof UIResource))
			c.setBorder(defaultBorder);
	}


	/**	Overriden so that the text will not be rendered as shifted for
	 * 	Toggle buttons and subclasses.
	 */
	protected int getTextShiftOffset()
	{
		return 0;
	}

    /**
     * One instance handles all toggles
     */
    private static final LipstikToggleButtonUI _toggleButtonUI = new LipstikToggleButtonUI();
}
