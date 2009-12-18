package com.lipstikLF.delegate;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

import com.lipstikLF.util.LipstikBorderFactory;

public class LipstikPopupMenuSeparatorUI extends BasicSeparatorUI
{
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
	public static ComponentUI createUI(JComponent c)
	{
		return _menuSeparatorUI;
	}

    /**
     * Install the UI delegate for the given component
     *
     * @param c The component for which to install defaults.
     */
    public void installUI(JComponent c)
    {
        installDefaults((JSeparator) c);
    }

	/**
     * Installs the defaults for the specified JSeparator
     *
     * @param s The component for which to install defaults.
     */
    protected void installDefaults(JSeparator s)
    {
        LookAndFeel.installColors(
            s,
            "PopupMenuSeparator.background",
            "PopupMenuSeparator.foreground");
    }


	/**
     * Uninstalls the defaults for the specified JSeparator
     *
     * @param s The separator for which to uninstall defaults.
     */
    protected void uninstallDefaults(JSeparator s)
    {
    }


    /**
     * Paint the component.
     *
     * @param g The graphics resource used to paint the component
     * @param c The component to paint.
     */
    public void paint(Graphics g, JComponent c)
    {
        Dimension s = c.getSize();
        g.setColor(c.getForeground());
        g.drawLine(LipstikBorderFactory.ICON_BAR_WIDTH, 1, s.width-21, 1);
    }


    /**	Returns the preferred size of this component */
    public Dimension getPreferredSize(JComponent c)
    {
        return new Dimension(0, 3);
    }

    /**
     * One instance handles all popup separators
     */
    private static final LipstikPopupMenuSeparatorUI _menuSeparatorUI = new LipstikPopupMenuSeparatorUI();
}