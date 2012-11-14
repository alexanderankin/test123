package com.lipstikLF.delegate;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;
import com.lipstikLF.util.LipstikBorderFactory;


public class LipstikSeparatorUI extends BasicSeparatorUI
{
  /**
    * Create the UI delegate for the given component
    *
    * @param c The component for which to create the ui delegate
    * @return The created ui delegate
    */
	public static ComponentUI createUI(JComponent c)
	{
		return _separatorUI;
	}


    /**
     * Install the UI delegate for the given component
     *
     * @param c The component for which to install the ui delegate.
     */
	public void installUI(JComponent c)
	{
		installDefaults((JSeparator) c);
		installListeners((JSeparator) c);
	}


    /**
     * Uninstall the UI delegate for the given component
     *
     * @param c The component for which to install the ui delegate.
     */
	public void uninstallUI(JComponent c)
	{
		uninstallDefaults((JSeparator) c);
		uninstallListeners((JSeparator) c);
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
		Component p = c.getParent();

        if (p instanceof JPopupMenu)
        {
        	g.setColor(c.getForeground());
            g.drawLine(LipstikBorderFactory.ICON_BAR_WIDTH, 1, s.width-21, 1);
            return;
        }

        LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
        g.setColor(theme.getControlDarkShadow());
        
        if (p instanceof JToolBar)
        {
        	if (((JToolBar)p).getOrientation() == JSeparator.HORIZONTAL)
				g.drawLine(5, 5, 5, s.height-6);
			else
	            g.drawLine(5, 5, s.width-6, 5);
        }
        else
        {
        	if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL)
        	{
        		int x = c.getWidth()>>1;
				g.drawLine(x+1, 0, x+1, s.height);
        		g.setColor(c.getBackground());
				g.drawLine(x, 0, x, s.height);
        	}
        	else
        	{
        		int y = c.getHeight()>>1;
        		g.drawLine(0, y+1, s.width, y+1);
        		g.setColor(c.getBackground());
        		g.drawLine(0, y, s.width, y);
        	}
        }
	}

	/**	Returns the preferred size for the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL)
			return new Dimension(10,20);
		else
			return new Dimension(20,3);
	}

	/**	Returns the maximum size for the specified component */
    public Dimension getMaximumSize(JComponent c)
	{
    	if (c.getParent() instanceof JToolBar)
    	{
    		JToolBar p = (JToolBar)c.getParent();
        	if (p.getOrientation() == JSeparator.HORIZONTAL)
        		return new Dimension(10, p.getHeight());
        	else
        		return new Dimension(p.getHeight(), 10);
    	}
    	else
    		return null;
	}

	/**
     * One instance handles all separators
     */
    private static final LipstikSeparatorUI _separatorUI = new LipstikSeparatorUI();
}
