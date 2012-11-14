package com.lipstikLF.delegate;

import com.lipstikLF.util.LipstikBorderFactory;
import com.lipstikLF.util.LipstikGradients;
import com.lipstikLF.util.LipstikListenerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

import java.awt.*;

public class LipstikButtonUI extends BasicButtonUI
{
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return _buttonUI;
    }

    /**
     * Install the UI delegate for the given component
     *
     * @param c The component for which to install the ui delegate.
     */
    public void installUI(JComponent c)
    {
        if (c.getParent() instanceof JToolBar)
        {
            c.addMouseListener(LipstikListenerFactory.getButtonRolloverMouseListener());
        }
        super.installUI(c);
    }

    /**
     * Uninstall the UI delegate for the given component
     *
     * @param c The component for which to uninstall the ui delegate.
     */
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);
        if (c.getParent() instanceof JToolBar)
            c.removeMouseListener(LipstikListenerFactory.getButtonRolloverMouseListener());
    }

    /**
     * Paint the component.
     *
     * @param g The graphics resource used to paint the component
     * @param c The component to paint.
     */
    public void paint(Graphics g, JComponent c)
    {
        if (c.isOpaque())
        {
            AbstractButton b = (AbstractButton) c;
            if (b.isContentAreaFilled())
            {
                ButtonModel model = b.getModel();
                if (is3D(b) && !model.isSelected())
                {
                    if (b.isRolloverEnabled() && !model.isRollover())
                    {
                        g.setColor(b.getParent().getBackground());
                        g.fillRect(0, 0, c.getWidth(), c.getHeight());
                    }
                    else
                    {
                        LipstikGradients.drawGradient(g, b.getBackground(), null,
                        		1, 1,
                        		c.getWidth()-2, c.getHeight()-2,
                        		true);
                    }
                }
                else
                {
                	Color bg = b.getBackground();
                	if (model.isSelected() || model.isPressed())
                	{
	                	bg = new Color(
	                			Math.max(bg.getRed()-10,0),
	                			Math.max(bg.getGreen()-10,0),
	                			Math.max(bg.getBlue()-10, 0));
                	}
                    g.setColor(bg);
                	g.fillRect(1, 1, c.getWidth()-2, c.getHeight()-2);
                }
            }

        }
        super.paint(g, c);
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect)
    {
        if (!(b.getParent() instanceof JToolBar) && !(b.isRolloverEnabled()))
        {
            LipstikBorderFactory.paintFocusBorder(g, 2, 2, b.getWidth()-4, b.getHeight()-4);
        }
    }

    /**
     * Paints the text for the specified button.
     *
     * @param g Graphics context into which to paint
     * @param b The button for which to draw text
     * @param textRect The rectangle enclosing the text
     * @param text The text to be drawn
     */
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text)
    {
        ButtonModel model = b.getModel();
        if (model.isPressed() && model.isArmed())
        {
            if (b instanceof JButton)
            {
                textRect.x++;
                textRect.y++;
            }
        }
    	super.paintText(g, b, textRect, text);
    }



    private boolean is3D(AbstractButton b)
    {
        ButtonModel model = b.getModel();
        return b.isBorderPainted()
            && !(model.isPressed() && model.isArmed())
            && !(b.getBorder() instanceof EmptyBorder);
    }

    /**
     * One instance handles all buttons.
     */
    private static final LipstikButtonUI _buttonUI = new LipstikButtonUI();
}
