package com.lipstikLF.delegate;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class LipstikSplitPaneContentBorder implements Border
{
	/**	The first color for the shadow gradient */
	private Color fromColor;
	
	/**	The second color for the shadow gradient */
	private Color toColor;
	
	/**	The background color */
	private Color bg;
	
	/**	If true, draws a line around the bordered component */
	private boolean drawBoundaryLine;

	
	public LipstikSplitPaneContentBorder()
	{
		this(null, null, null, false);
	}
	
	public LipstikSplitPaneContentBorder(boolean drawBoundaryLine)
	{
		this(null, null, null, drawBoundaryLine);
	}

	public LipstikSplitPaneContentBorder(Color bg, Color fromColor, Color toColor)
	{
		this(bg, fromColor, toColor, false);
	}
		
	public LipstikSplitPaneContentBorder(Color bg, Color fromColor, Color toColor, boolean drawBoundaryLine)
	{
		this.drawBoundaryLine=drawBoundaryLine;
		
		if (bg != null)
			this.bg = bg;
		else
			this.bg = UIManager.getColor("Button.borderColor");
			
		if(fromColor != null)
			this.fromColor = fromColor;
		else
			this.fromColor = UIManager.getColor("Button.borderColor");
			
		if (toColor != null)
			this.toColor = toColor;
		else
			this.toColor = UIManager.getColor("ScrollPane.background");
	}
	

	/**	Returns the insets of this border */		
	public Insets getBorderInsets(Component c)
	{
		int leftTop=(drawBoundaryLine ? 1 : 0);
		int rightBottom=(drawBoundaryLine ? 4 : 3);
		
		return new Insets(leftTop, leftTop, rightBottom, rightBottom);
	}


	/**	Returns true if this border is opaque */
	public boolean isBorderOpaque()
	{
		return false;
	}
	
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		g.setColor(toColor);
		g.fillRect(x, y+height-3, 3, 3);
		g.fillRect(x+width-3, y, 3, 3);
		
		for(int i=0; i<3; i++)
		{
			g.setColor(blendColors(fromColor, toColor, (double)(i+2)/(double)4));
			g.drawLine(x+width-3+i, y+i+1, x+width-3+i, y+height-3+i);
			g.drawLine(x+i+1, y+height-3+i, x+width-3+i, y+height-3+i);
		}
		
		if(drawBoundaryLine)
		{
			g.setColor(bg);
			g.drawRect(x, y, x+width-4, y+height-4);
		}
	}
	
	
	/**	Blends two colors.
	 * 
	 * 	@param	c1 The first color
	 * 	@param	c2	The second color
	 * 	@param	factor The ratio between the first and second color. If this is 0.0,
	 * 	the result will be c1, if it is 1.0, the result will be c2.
	 * 
	 * 	@return A color resulting from blending c1 and c2
	 */
	private static Color blendColors(Color c1, Color c2, double factor)
	{
		if(c1 == null || c2 == null)
		{
			if(c1 != null)
				return c1;
			else 
			if(c2 != null)
				return c2;
			else
				return Color.BLACK;
		}
		
		int r=(int)(c2.getRed()*factor+c1.getRed()*(1.0-factor));
		int g=(int)(c2.getGreen()*factor+c1.getGreen()*(1.0-factor));
		int b=(int)(c2.getBlue()*factor+c1.getBlue()*(1.0-factor));
		
		return new Color(r,g,b);
	}
}
