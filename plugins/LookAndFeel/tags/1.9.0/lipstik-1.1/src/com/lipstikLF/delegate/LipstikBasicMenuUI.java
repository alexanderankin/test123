package com.lipstikLF.delegate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuUI;

import com.lipstikLF.util.LipstikListenerFactory;
import com.lipstikLF.util.MenuItemRenderer;

public class LipstikBasicMenuUI extends BasicMenuUI
{
    private static final String MENU_PROPERTY_PREFIX = "Menu";
    private static final String SUBMENU_PROPERTY_PREFIX = "MenuItem";

    // May be changed to SUBMENU_PROPERTY_PREFIX later
    private String propertyPrefix = MENU_PROPERTY_PREFIX;
    private MenuItemRenderer renderer;
    private MouseListener mouseListener;

    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikBasicMenuUI();
    }

    protected void installDefaults()
    {
        super.installDefaults();

        if (arrowIcon == null || arrowIcon instanceof UIResource)
            arrowIcon = UIManager.getIcon("Menu.arrowIcon");

        renderer = new MenuItemRenderer(menuItem,
                acceleratorFont,
                selectionForeground,
                disabledForeground,
                acceleratorForeground,
                acceleratorSelectionForeground);

        Integer gap = (Integer) UIManager.get(getPropertyPrefix()  + ".textIconGap");
        defaultTextIconGap = gap != null ? gap.intValue() : 2;
    }

    protected void uninstallDefaults()
    {
        super.uninstallDefaults();
        renderer = null;
    }

    protected String getPropertyPrefix()
    {
        return propertyPrefix;
    }

    protected Dimension getPreferredMenuItemSize(JComponent c, Icon aCheckIcon, Icon anArrowIcon, int textIconGap)
    {
        if (isSubMenu(menuItem))
        {
            ensureSubMenuInstalled();
            return renderer.getPreferredMenuItemSize(c, aCheckIcon, anArrowIcon, textIconGap);
        }
        return super.getPreferredMenuItemSize(c, aCheckIcon, anArrowIcon, textIconGap);
    }

    protected void paintMenuItem(Graphics g, JComponent c, Icon aCheckIcon, Icon anArrowIcon, Color background, Color foreground, int textIconGap)
    {
        if (isSubMenu(menuItem))
            renderer.paintMenuItem(g, c, aCheckIcon, anArrowIcon, background, foreground, textIconGap);
        else
            super.paintMenuItem(g, c, aCheckIcon, anArrowIcon, background, foreground, textIconGap);
    }

    /**
     * Checks if we have already detected the correct menu type,
     * menu in menu bar vs. sub menu; reinstalls if necessary.
     */
    private void ensureSubMenuInstalled()
    {
        if (propertyPrefix.equals(SUBMENU_PROPERTY_PREFIX))
            return;

        uninstallRolloverListener();
        uninstallDefaults();
        propertyPrefix = SUBMENU_PROPERTY_PREFIX;
        installDefaults();
    }

    protected void installListeners()
    {
        super.installListeners();
        mouseListener = LipstikListenerFactory.getButtonRolloverMouseListener();
        menuItem.addMouseListener(mouseListener);
    }

    protected void uninstallListeners()
    {
        super.uninstallListeners();
        uninstallRolloverListener();
    }

    private void uninstallRolloverListener()
    {
        if (mouseListener != null)
        {
            menuItem.removeMouseListener(mouseListener);
            mouseListener = null;
        }
    }

    private boolean isSubMenu(JMenuItem aMenuItem)
    {
        return !((JMenu) aMenuItem).isTopLevelMenu();
    }
}