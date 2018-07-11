package com.lipstikLF.delegate;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.plaf.basic.BasicArrowButton;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;
import com.lipstikLF.util.LipstikBorderFactory;
import com.lipstikLF.util.LipstikGradients;

/**
 * Renders the arrow buttons in scroll bars and spinners.
 */
final class LipstikArrowButton extends BasicArrowButton
{
	public LipstikArrowButton(int direction)
    {
		super(direction);
	}

	public void paint(Graphics g)
    {
		boolean isEnabled = getParent().isEnabled();
		boolean isPressed = getModel().isPressed();

        LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
		Color arrowColor = isEnabled ? Color.BLACK	: theme.getControlShadow();

        int startX;
        int startY;
		int w = getWidth();
		int h = getHeight();
        int arrowSize = 4;
        int bstyle = LipstikBorderFactory.BORDER_BRIGHTER|LipstikBorderFactory.BORDER_SHADOW; 
        	        
        if (isPressed)
        {
	    	Color bg = getBackground();
	    	bg = new Color(
        			Math.max(bg.getRed()-10,0),
        			Math.max(bg.getGreen()-10,0),
        			Math.max(bg.getBlue()-10, 0));

	    	g.setColor(bg);
            g.fillRect(1,1,w-2,h-2);
            
            bstyle -= LipstikBorderFactory.BORDER_SHADOW;
        }
        else
            LipstikGradients.drawGradient(g, getBackground(), null, 2, 2, w-3, h-3, true);

        g.setColor(theme.getControlHighlight());
        g.drawRect(0,0,w-1,h-1);

        g.setColor(arrowColor);
        if (getDirection() == WEST)
        {        	
        	startX = ((w - arrowSize) >> 1);
            startY = (h >> 1);
            for (int line = 0; line < arrowSize; line++)
                g.drawLine(startX + line,startY - line,startX + line,startY + line);

            LipstikBorderFactory.paintRoundBorder(g, 1, 1, w-1, h-2, theme, theme.getControlHighlight(), bstyle);

        }
        else
        if (getDirection() == EAST)
        {
            startX = ((w - arrowSize) >> 1) + arrowSize - 1;
            startY = (h >> 1);
            for (int line = 0; line < arrowSize; line++)
                g.drawLine(startX - line,startY - line,startX - line,startY + line);

            LipstikBorderFactory.paintRoundBorder(g, 0, 1, w-1, h-2, theme, theme.getControlHighlight(), bstyle);
        }
        else
        if (getDirection() == NORTH)
        {
            startY = 1+(h - arrowSize) >> 1;
            startX = w >> 1;
            for (int line = 0; line < arrowSize; line++)
                g.drawLine(startX - line, startY + line, startX + line, startY + line);
            
            LipstikBorderFactory.paintRoundBorder(g, 1, 1, w-2, h-1, theme, theme.getControlHighlight(), bstyle);
        }
		else
        {
            startY = ((h  - arrowSize) >> 1) + arrowSize - 1;
            startX = w >> 1;
            for (int line = 0; line < arrowSize; line++)
                g.drawLine(startX - line, startY - line, startX + line, startY - line);
            
            LipstikBorderFactory.paintRoundBorder(g, 1, 0, w-2, h-1, theme, theme.getControlHighlight(), bstyle);
        }
    }
}