package com.lipstikLF.delegate;


import com.lipstikLF.util.LipstikBorderFactory;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;


public class LipstikRadioButtonUI extends LipstikToggleButtonUI
{
	/**	Cached rectangle for painting */
	private static Rectangle prefViewRect = new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle prefIconRect = new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle prefTextRect = new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Insets prefInsets = new Insets(2,2,1,1);

	/**	Cached rectangle for painting */
	private static Dimension size = new Dimension();
	
	/**	Cached rectangle for painting */
	private static Rectangle viewRect = new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle iconRect = new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle textRect = new Rectangle();

	protected Icon selectedEnabledIcon;
	protected Icon selectedDisabledIcon;
	protected Icon unselectedEnabledIcon;
	protected Icon unselectedDisabledIcon;
	protected Icon defaultIcon;

	private boolean defaults_initialized = false;

	private final static String propertyPrefix = "RadioButton" + ".";


    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
	public static ComponentUI createUI(JComponent c)
	{
		return _radioButtonUI;
	}
	
	
	/**	Returns the property prefix for RadioButtons */
	protected String getPropertyPrefix()
	{
		return propertyPrefix;
	}

	
	/**
     * Installs the UI defaults for the specified button
     *
     * @param b The button for which to install defaults.
     */
	protected void installDefaults(AbstractButton b)
	{
		super.installDefaults(b);
		if (!defaults_initialized)
		{
			defaultIcon = UIManager.getIcon(getPropertyPrefix() + "icon");
			
			// Get icons
			selectedEnabledIcon  =
					UIManager.getIcon(getPropertyPrefix()+"selectedEnabledIcon");
			selectedDisabledIcon =
					UIManager.getIcon(getPropertyPrefix()+"selectedDisabledIcon");
			unselectedEnabledIcon =
					UIManager.getIcon(getPropertyPrefix()+"unselectedEnabledIcon");
			unselectedDisabledIcon =
					UIManager.getIcon(getPropertyPrefix()+"unselectedDisabledIcon");			
					
			defaults_initialized = true;
		}
	}


	/**
     * Uninstalls the defaults for the specified button
     *
     * @param b The button for which to uninstall defaults.
     */
	public void uninstallDefaults(AbstractButton b)
	{
		super.uninstallDefaults(b);
		defaults_initialized = false;
	}
	

    /**
     * Paint the component.
     *
     * @param g The graphics resource used to paint the component
     * @param c The component to paint.
     */
	public synchronized void paint(Graphics g, JComponent c)
	{
		AbstractButton b  = (AbstractButton) c;
		ButtonModel model = b.getModel();

		Font f = c.getFont();
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics();

		size = b.getSize(size);
		viewRect.x = viewRect.y = 0;
		viewRect.width = size.width;
		viewRect.height= size.height;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height= 0;
		textRect.x = textRect.y = textRect.width = textRect.height= 0;

		Icon altIcon = b.getIcon();

		String text =
			SwingUtilities.layoutCompoundLabel(
				c,
				fm,
				b.getText(),
				altIcon != null ? altIcon : defaultIcon,
				b.getVerticalAlignment(),
				b.getHorizontalAlignment(),
				b.getVerticalTextPosition(),
				b.getHorizontalTextPosition(),
				viewRect,
				iconRect,
				textRect,
				b.getText() == null ? 0 : b.getIconTextGap());

		if (c.getComponentOrientation().isLeftToRight())
		{
	        textRect.x += prefInsets.left;
	        iconRect.x += prefInsets.left;
		}
		else
		{
	        textRect.x -= prefInsets.left;
	        iconRect.x -= prefInsets.left;			
		}
		
		// fill background
		if (c.isOpaque())
		{
			g.setColor(b.getBackground());
			g.fillRect(0, 0, size.width, size.height);
		}

		// Paint the radio button
		if (b.getIcon() != null)
		{
			altIcon = b.getIcon();

			if (!model.isEnabled())
			{
				if (model.isSelected())
					altIcon = b.getDisabledSelectedIcon();
				else
					altIcon = b.getDisabledIcon();
			}
			else if (model.isPressed() && model.isArmed())
			{
				altIcon = b.getPressedIcon();
				if (altIcon == null)
					altIcon = b.getSelectedIcon();
			}
			else if (model.isSelected())
			{
				if (b.isRolloverEnabled() && model.isRollover())
				{
					altIcon = b.getRolloverSelectedIcon();
					if (altIcon == null)
						altIcon = b.getSelectedIcon();
				}
				else
					altIcon = b.getSelectedIcon();
			}
			else 
            if (b.isRolloverEnabled() && model.isRollover())
				altIcon = b.getRolloverIcon();
			
			if (altIcon == null)
				altIcon = b.getIcon();

			altIcon.paintIcon(c, g, iconRect.x, iconRect.y);
		}
		else
		{
			Icon icon;
			if (model.isEnabled())
			{
				if (model.isSelected())
					icon = selectedEnabledIcon;
				else
					icon = unselectedEnabledIcon;
			}
			else
			{
				if (model.isSelected())
					icon = selectedDisabledIcon;
				else
					icon = unselectedDisabledIcon;
			}

			icon.paintIcon(c, g, iconRect.x, iconRect.y);
		}
		
		// Draw the Text
		if (text != null)
		{
			View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null)
			{
				v.paint(g, textRect);
			}
			else
			{
				paintText(g, b, textRect, text);
				if (b.hasFocus()
					&& b.isFocusPainted()
					&& textRect.width > 0
					&& textRect.height > 0)
				{
					LipstikBorderFactory.paintFocusBorder(g, 0, 0, size.width, size.height);
				}
			}
		}        
	}

	/**	Returns the preferred size for the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		if (c.getComponentCount() > 0)
			return null;

		AbstractButton b = (AbstractButton) c;
		String text = b.getText();

		Icon buttonIcon = b.getIcon();
		
		if (buttonIcon == null)
			buttonIcon= defaultIcon;

		FontMetrics fm = b.getFontMetrics(b.getFont());

		prefViewRect.x = prefViewRect.y= 0;
		prefViewRect.width = Short.MAX_VALUE;
		prefViewRect.height = Short.MAX_VALUE;
		prefIconRect.x = prefIconRect.y = prefIconRect.width = prefIconRect.height= 0;
		prefTextRect.x = prefTextRect.y = prefTextRect.width = prefTextRect.height= 0;

		SwingUtilities.layoutCompoundLabel(
			c,
			fm,
			text,
			buttonIcon,
			b.getVerticalAlignment(),
			b.getHorizontalAlignment(),
			b.getVerticalTextPosition(),
			b.getHorizontalTextPosition(),
			prefViewRect,
			prefIconRect,
			prefTextRect,
			text == null ? 0 : b.getIconTextGap());

		prefTextRect.width += 2;
		
		// find the union of the icon and text rects (from Rectangle.java)
		int x1 = Math.min(prefIconRect.x, prefTextRect.x);
		int x2 =
			Math.max(
				prefIconRect.x + prefIconRect.width,
				prefTextRect.x + prefTextRect.width);
		int y1 = Math.min(prefIconRect.y, prefTextRect.y);
		int y2 =
			Math.max(
				prefIconRect.y + prefIconRect.height,
				prefTextRect.y + prefTextRect.height);
		int width  = x2 - x1;
		int height = y2 - y1;

		//prefInsets= b.getInsets(prefInsets);
		width += prefInsets.left + prefInsets.right;
		height += prefInsets.top + prefInsets.bottom;
		return new Dimension(width, height);
	}
    
    /**
     * One instance handles all radios
     */
    private static final LipstikRadioButtonUI _radioButtonUI = new LipstikRadioButtonUI();    
}
