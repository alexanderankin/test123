package com.lipstikLF.delegate;

import com.lipstikLF.util.MenuItemRenderer;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;

public class LipstikMenuItemUI extends BasicMenuItemUI
{
    private static final int MINIMUM_WIDTH = 80;
    private MenuItemRenderer renderer;

    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikMenuItemUI();
    }

    protected void installDefaults()
    {
        super.installDefaults();
        renderer = new MenuItemRenderer(menuItem,
                acceleratorFont,
                selectionForeground,
                disabledForeground,
                acceleratorForeground,
                acceleratorSelectionForeground);

        Integer gap = (Integer) UIManager.get(getPropertyPrefix() + ".textIconGap");
        defaultTextIconGap = gap != null ? gap.intValue() : 2;
    }

    protected void uninstallDefaults()
    {
        super.uninstallDefaults();
        renderer = null;
    }

    protected Dimension getPreferredMenuItemSize(JComponent c, Icon aCheckIcon,
            Icon anArrowIcon, int textIconGap)
    {
        Dimension size = renderer.getPreferredMenuItemSize(c, aCheckIcon, anArrowIcon, textIconGap);
        return new Dimension(Math.max(MINIMUM_WIDTH, size.width), size.height);
    }

	protected void paintMenuItem(Graphics g, JComponent c, Icon aCheckIcon,
            Icon anArrowIcon, Color background, Color foreground,
            int textIconGap)
    {
    	renderer.paintMenuItem(g, c, aCheckIcon, anArrowIcon, background, foreground, textIconGap);
    }

}