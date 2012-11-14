package com.lipstikLF.delegate;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;
import com.lipstikLF.util.LipstikGradients;

public final class LipstikMenuUI extends LipstikBasicMenuUI
{
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikMenuUI();
    }

    protected void paintBackground(Graphics g, JMenuItem aMenuItem, Color bgColor)
    {
        int menuWidth  = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();

        Color old = g.getColor();
        LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();

        ButtonModel model = aMenuItem.getModel();
        if (model.isArmed() || (model.isRollover() && !model.isSelected()))
            LipstikGradients.drawGradient(g, theme.getMenuBarBackground(), theme.getMenuBackground(), 1,1, menuWidth-3, menuHeight-3, true);
        else
        if (aMenuItem.isOpaque() || model.isSelected())
        {
    		g.setColor(theme.getMenuBarBackground());
    		g.fillRect(0, 0, menuWidth, menuHeight);
        }
        g.setColor(old);
    }
}