package com.lipstikLF.delegate;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;
import com.lipstikLF.util.LipstikBorderFactory;
import com.lipstikLF.util.LipstikGradients;
import com.lipstikLF.util.LipstikIconFactory;

public class LipstikScrollBarUI extends BasicScrollBarUI
{
	protected LipstikArrowButton increaseButton;
	protected LipstikArrowButton decreaseButton;
	protected int scrollBarWidth;

    /**
     * Installs some default values.
     */
    protected void installDefaults()
    {
    	scrollBarWidth = ((Integer) (UIManager.get("ScrollBar.width"))).intValue();
		super.installDefaults();
    }

    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikScrollBarUI();
    }

    /**
     * Creates the decrease button of the scrollbar.
     *
     * @param orientation The button's orientation.
     * @return The created button.
     */
    protected JButton createDecreaseButton(int orientation)
    {
        decreaseButton = new LipstikArrowButton(orientation);
        return decreaseButton;
    }

    /**
     * Creates the increase button of the scrollbar.
     *
     * @param orientation The button's orientation.
     * @return The created button.
     */
    protected JButton createIncreaseButton(int orientation)
    {
    	increaseButton = new LipstikArrowButton(orientation);
        return increaseButton;
    }

    /**
     * Paints the scrollbar's thumb.
     *
     * @param g The graphics context to use.
     * @param c The component to paint.
     * @param thumbBounds The track bounds.
     */
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
    	boolean isVertical = scrollbar.getOrientation() == JScrollBar.VERTICAL;

        int w  = thumbBounds.width;
        int h  = thumbBounds.height;

        g.translate(thumbBounds.x, thumbBounds.y);

        LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
        
        if (isVertical)
        {
            LipstikGradients.drawGradient(g, c.getForeground(), null, 3, 2, w-4, h-3, true);
        	LipstikBorderFactory.paintRoundBorder(g, 1,0, w-2, h, theme, theme.getControlHighlight(), LipstikBorderFactory.BORDER_BRIGHTER);            
        	if (h > 8)
            {
                int iw = LipstikIconFactory.vicon1.getIconWidth();
                int ih = LipstikIconFactory.vicon1.getIconHeight();
                int dy = 2 + (h - ih) >> 1;
                int dx = 2 + (w - iw) >> 1;

                if (h < 18)
                    g.setClip(dx, dy + 4, 4, 4);
                else
                if (h < 30)
                {
                    dy += 3;
                    g.setClip(dx, dy, 4, 10);
                }
                g.drawImage(LipstikIconFactory.vicon1.getImage(), dx, dy, null);
            }
        }
        else
        {
        	LipstikGradients.drawGradient(g, c.getForeground(), null, 2, 3, w-3, h-4, true);
        	LipstikBorderFactory.paintRoundBorder(g, 0,1, w, h-2, theme, theme.getControlHighlight(), LipstikBorderFactory.BORDER_BRIGHTER);
        	if (w > 8)
        	{
	        	int iw = LipstikIconFactory.hicon1.getIconWidth();
	            int ih = LipstikIconFactory.hicon1.getIconHeight();
	            int dy = 2 + (h-ih)>>1;
	            int dx = 2 + (w-iw)>>1;
	
	            if (w < 18)
	                g.setClip(dx+4,dy,4,4);
	            else
	            if (w < 30)
	            {
	                dx += 3;
	                g.setClip(dx,dy,10,4);
	            }
	            g.drawImage(LipstikIconFactory.hicon1.getImage(),dx, dy,null);
        	}
        }
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    /**
     * Paints the scrollbar's track.
     *
     * @param g The graphics context to use.
     * @param c The component to paint.
     * @param trackBounds The track bounds.
     */
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
    {
        LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
        g.translate(trackBounds.x, trackBounds.y);
        g.setColor(theme.getControlHighlight());
        g.fillRect(0, 0, trackBounds.width, trackBounds.height);
        g.translate(-trackBounds.x, -trackBounds.y);
    }
}


