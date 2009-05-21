package com.lipstikLF.delegate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;
import com.lipstikLF.util.LipstikBorderFactory;
import com.lipstikLF.util.LipstikGradients;
import com.lipstikLF.util.LipstikIconFactory;

public class LipstikSliderUI extends BasicSliderUI
{
	private static EmptyBorder emptyBorder = new EmptyBorder(2,2,2,2);
	private static int trackWidth = 8;

	public LipstikSliderUI() {
        super (null);
    }

    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikSliderUI();
    }

    /**
     * Install the UI delegate for the given component
     *
     * @param c The component for which to install the ui delegate.
     */
    public void installUI( JComponent c )
    {
    	super.installUI(c);
    	c.setBorder(emptyBorder);
    }

    /**
     * Paint the slider's thumb
     *
     * @param g The graphics resource used to paint the component
     */
    public void paintThumb(Graphics g)
    {
    	LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
        Rectangle knobBounds = thumbRect;

        int sx = knobBounds.x;
        int sy = knobBounds.y;
        int sw = knobBounds.width;
        int sh = knobBounds.height;

        if (slider.isEnabled())
        {
            LipstikGradients.drawGradient(g, theme.getControl(), null, sx+1, sy+1, sw-1, sh-1, slider.getOrientation() != JSlider.VERTICAL);
            LipstikBorderFactory.paintRoundBorder(g, sx, sy, sw, sh, theme, theme.getControl(), LipstikBorderFactory.BORDER_SHADOW);
            g.setClip(sx, sy, sw-2, sh-2);

            if (slider.getOrientation() != JSlider.VERTICAL)
                g.drawImage(LipstikIconFactory.vicon1.getImage(), knobBounds.x+6, knobBounds.y+4,null);
            else
                g.drawImage(LipstikIconFactory.hicon1.getImage(), knobBounds.x+4, knobBounds.y+6,null);
        }
        else
        {
            g.setColor(slider.getParent().getBackground());
            g.fillRect(sx, sy, sw, sh);
            LipstikBorderFactory.paintRoundBorder(g, sx, sy, sw, sh, theme, slider.getBackground(), LipstikBorderFactory.BORDER_DISABLED);
        }
    }

    protected int getThumbOverhang()
    {
        if (slider.getOrientation() == JSlider.VERTICAL)
            return (int) (getThumbSize().getWidth() - trackWidth) / 2;
        else
            return (int) (getThumbSize().getHeight() - trackWidth) / 2;
    }
    
    protected Dimension getThumbSize() {
        Dimension size = new Dimension();

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
        	size.width = 16;
	    	size.height = 15;
        }
        else {
        	size.width = 15;
        	size.height = 16;
        }
        return size;
    }

	/**
     * Paint the slider's track
     *
     * @param g The graphics resource used to paint the component
     */
    public void paintTrack(Graphics g)
    {
        LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
        boolean leftToRight = slider.getComponentOrientation().isLeftToRight();
        g.translate(trackRect.x, trackRect.y);

        int trackLeft = 0;
        int trackTop = 0;
        int trackRight;
        int trackBottom;

        // Draw the track
        if (slider.getOrientation() == JSlider.HORIZONTAL)
        {
            trackBottom = 3;
            trackTop = 6;
            trackRight = trackRect.width - 1;
        }
        else
        {
            if (leftToRight)
                trackLeft = (trackRect.width - getThumbOverhang()) -  trackWidth;
            else
                trackLeft = getThumbOverhang();

            trackRight = 3;
            trackBottom = trackRect.height - 1;
        }

        if (slider.isEnabled())
            g.setColor(theme.getBorderNormal());
        else
            g.setColor(theme.getBorderDisabled());

        g.drawRect( trackLeft + 1, trackTop + 1, trackRight, trackBottom );
        g.translate(-trackRect.x, -trackRect.y);
    }

    protected void paintMinorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x)
    {
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(x, 0, x, tickBounds.height / 2 - 1);
    }

    protected void paintMajorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x)
    {
        g.setColor(Color.GRAY);
        g.drawLine(x, 0, x, tickBounds.height - 4);
    }

    protected void paintMinorTickForVertSlider(Graphics g, Rectangle tickBounds, int y)
    {
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, y, tickBounds.width / 2 - 2, y);
    }

    protected void paintMajorTickForVertSlider(Graphics g, Rectangle tickBounds, int y)
    {
        g.setColor(Color.GRAY);
        g.drawLine(0, y, tickBounds.width - 7, y);
    }


    public void paintFocus(Graphics g)
    {
        LipstikBorderFactory.paintFocusBorder(g, 0,0, slider.getWidth(), slider.getHeight());
    }
}
