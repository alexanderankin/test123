package com.lipstikLF.delegate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;
import com.lipstikLF.util.LipstikBorderFactory;
import com.lipstikLF.util.LipstikGradients;
import com.lipstikLF.util.LipstikListenerFactory;
import com.lipstikLF.util.LipstikBorderFactory.OptionalMatteBorder;


public class LipstikInternalFrameTitlePane extends MetalInternalFrameTitlePane
{
    /**	Creates an instance for the specified JInternalFrame */
    public LipstikInternalFrameTitlePane(JInternalFrame frame)
    {
        super(frame);
    }

    public JInternalFrame getFrame()
    {
    	return frame;
    }

	/**	Paints this component. */
	public void paintComponent(Graphics g)
	{
		LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();

		Color backColor;
		Color fontColor;
		Color buttonColor;

		// Draw gradient
		if (frame.isSelected())
		{
			backColor = UIManager.getColor("InternalFrame.activeTitleBackground");
			fontColor = UIManager.getColor("InternalFrame.activeTitleForeground");
			buttonColor = theme.getInternalButtonBackground();
		}
		else
		{
			backColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
			fontColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
			buttonColor = theme.getInternalButtonInactive();
		}

		LipstikGradients.drawGradient(g, backColor, null, 0,0, getWidth(), getHeight(), true);

		// Draw icon and text
		if (!isPalette)
		{
			iconButton.setBackground(buttonColor);
			maxButton.setBackground(buttonColor);
			closeButton.setBackground(buttonColor);

			Icon frameIcon = frame.getFrameIcon();
			int frameIconWidth = 1;
			
			if (frameIcon != null)
			{
				frameIcon.paintIcon(
					this,
					g,
					4,
					getHeight() / 2 - frameIcon.getIconHeight() / 2);
				
				frameIconWidth = frameIcon.getIconWidth();
			}
			
			g.setFont(UIManager.getFont("InternalFrame.font"));
			g.setColor(fontColor);
			FontMetrics fm = g.getFontMetrics();
			int yOffset = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
			int titleW = getWidth() - frameIconWidth - 10;
			if (frame.isClosable())
				titleW -= 17;
			if (frame.isMaximizable())
				titleW -= 17;
			if (frame.isIconifiable())
				titleW -= 17;

			g.drawString(getTitle(frame.getTitle(), fm, titleW), 8 + frameIconWidth, yOffset);
		}
	}

	/**	Creates a layout for this panel and returns it */
	protected LayoutManager createLayout()
	{
		 return new TitlePaneLayout();
	}

	/**	Creates the frame title buttons for minimizing, maximizing, and closing. */
	protected void createButtons()
	{
	    OptionalMatteBorder handyEmptyBorder = LipstikBorderFactory.getOptionalMatteBorder();

		MouseAdapter handler = LipstikListenerFactory.getFrameButtonMouseHandler();

		iconButton = new JButton();
		iconButton.setFocusPainted(false);
		iconButton.setFocusable(false);
		iconButton.setOpaque(true);
		iconButton.addActionListener(iconifyAction);
		iconButton.addMouseListener(handler);
		iconButton.setBorder(handyEmptyBorder);

		maxButton = new JButton();
		maxButton.setFocusPainted(false);
		maxButton.setFocusable(false);
		maxButton.setOpaque(true);
		maxButton.addActionListener(maximizeAction);
		maxButton.addMouseListener(handler);
		maxButton.setBorder(handyEmptyBorder);

		closeButton = new JButton();
		closeButton.setFocusPainted(false);
		closeButton.setFocusable(false);
		closeButton.setOpaque(true);
		closeButton.addActionListener(closeAction);
		closeButton.addMouseListener(handler);
		closeButton.setBorder(handyEmptyBorder);
		setButtonIcons();
	}

    public void setPalette(boolean b)
    {
    	super.setPalette(b);
    	if (b)
    	{
    		MouseAdapter handler = LipstikListenerFactory.getFrameButtonMouseHandler();
    		maxButton.removeMouseListener(handler);
			iconButton.removeMouseListener(handler);
			closeButton.removeMouseListener(handler);
    	}
	}

	/**	Layout for the InternalFrameTitlePane */
	class TitlePaneLayout implements LayoutManager
	{
		/**	Adds a component which is to be layouted */
		public void addLayoutComponent(String name, Component c)
		{
			// Does nothing
		}


		/**	Removes a component which is to be layouted */
		public void removeLayoutComponent(Component c)
		{
			// Does nothing
		}


		/**	Returns the preferred size of this layout for the specified component */
		public Dimension preferredLayoutSize(Container c)
		{
			return minimumLayoutSize(c);
		}


		/**	Returns the minimum size of this layout for the specified component */
		public Dimension minimumLayoutSize(Container c)
		{
			// Compute width.
			int height, width = 30;
			if (frame.isClosable())
				width += 12;

			if (frame.isMaximizable())
				width += 12;

			if (frame.isIconifiable())
				width += 12;

			FontMetrics fm = getFontMetrics(UIManager.getFont("InternalFrame.font"));
			String frameTitle = frame.getTitle();
			int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;
			int title_length = frameTitle != null ? frameTitle.length() : 0;

			if (title_length > 2)
			{
				int subtitle_w = fm.stringWidth(frame.getTitle().substring(0, 2) + "...");
				width += (title_w < subtitle_w) ? title_w : subtitle_w;
			}
			else
			{
				width += title_w;
			}

			// Compute height.
			if (isPalette)
				height = paletteTitleHeight;
			else
			{
				int fontHeight = fm.getHeight()+2;
				int iconHeight = 0;
				Icon icon = frame.getFrameIcon();
				if (icon != null)
				{
					// SystemMenuBar forces the icon to be 16x16 or less.
					iconHeight= Math.min(icon.getIconHeight(), 16);
				}
				height = Math.max(fontHeight, iconHeight);
			}
			return new Dimension(width, height);
		}


		/**	Does a layout for the specified container */
		public void layoutContainer(Container c)
		{
			boolean leftToRight = c.getComponentOrientation().isLeftToRight();

			int w = getWidth();
			int x = leftToRight ? w : 0;
			int spacing = 3;

			// assumes all buttons have the same dimensions
			// these dimensions include the borders
			int buttonHeight = 12; 
			int buttonWidth = 12; 
			int y = getHeight()/2-buttonHeight/2;

			if (frame.isClosable())
			{
				if (isPalette)
				{
					x += leftToRight ? - (buttonWidth)-1 : 0;
					closeButton.setBounds(x, 0, buttonWidth, getHeight()-1);
				}
				else
				{
					x += leftToRight ? -spacing - buttonWidth : spacing;
					closeButton.setBounds(x, y, buttonWidth, buttonHeight);
					if (!leftToRight)
						x += buttonWidth;
				}
			}

			if (frame.isMaximizable() && !isPalette)
			{
				x += leftToRight ? -spacing - buttonWidth : spacing;
				maxButton.setBounds(x, y, buttonWidth, buttonHeight);
				if (!leftToRight)
					x += buttonWidth;
			}

			if (frame.isIconifiable() && !isPalette)
			{
				x += leftToRight ? -spacing - buttonWidth : spacing;
				iconButton.setBounds(x, y, buttonWidth, buttonHeight);
				if (!leftToRight)
					x += buttonWidth;
			}
		}
	}
}
