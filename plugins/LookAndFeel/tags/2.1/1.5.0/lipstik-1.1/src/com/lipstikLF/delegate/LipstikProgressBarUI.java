package com.lipstikLF.delegate;

import com.lipstikLF.LipstikLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

/**
 * This class represents the UI delegate for the JProgressBar component.
 */
public class LipstikProgressBarUI extends BasicProgressBarUI
{
    /**
     * The offset of the filled bar. This amount of space will be added on the
     * left, right top and bottom of the progress bar to its borders.
     */
    private static final int offset = 2;

    private Rectangle boxRect;

    /**
     * Creates the UI delegate for the given component.
     * 
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikProgressBarUI();
    }

    /**
     * Paints the given component.
     * 
     * @param g The graphics context to use.
     * @param c The component to paint.
     */
    protected void paintDeterminate(Graphics g, JComponent c)
    {
        boolean isHoriz = ((JProgressBar) c).getOrientation() == JProgressBar.HORIZONTAL;

        Insets b = progressBar.getInsets();
        int barRectWidth = progressBar.getWidth() - 2*offset;
        int barRectHeight = progressBar.getHeight() - 2*offset;
        int w,h,y = 0;
        
        Graphics2D g2 = (Graphics2D) g;
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        g2.translate(offset, offset);
        if (c.isEnabled())
        	g2.setColor(progressBar.getForeground());
        else
        	g2.setColor(LipstikLookAndFeel.getMyCurrentTheme().getBorderDisabled());

        if (isHoriz)
        {
            w = amountFull;
            h = barRectHeight;
        }
        else
        {
            w = barRectWidth;
            h = amountFull;
            y = barRectHeight-amountFull;
        }        
        g2.fillRect(0, y, w, h);

        // Deal with possible text painting
        if (progressBar.isStringPainted())
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
        g2.translate(offset, offset);
    }

    protected void paintIndeterminate(Graphics g, JComponent c)
    {
        Insets b = progressBar.getInsets();
        int barRectWidth = progressBar.getWidth() - 2*offset;
        int barRectHeight = progressBar.getHeight() - 2*offset;

        boxRect = getBox(boxRect);
        if (boxRect != null)
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(progressBar.getForeground());
            g2.fillRect(boxRect.x, boxRect.y, boxRect.width, boxRect.height);
        }
        // Deal with possible text painting
        if (progressBar.isStringPainted())
        {
            int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
        }
    }
}