package com.lipstikLF.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;

public final class MenuItemRenderer
{
	/*
     * Implementation note: The protected visibility prevents the String value
     * from being encrypted by the obfuscator. An encrypted String key would
     * break the client property lookup in the #paint method below.
     */
    protected static final String HTML_KEY = BasicHTML.propertyKey;

    /* Client Property keys for text and accelerator text widths */
    private static final String MAX_TEXT_WIDTH = "maxTextWidth";
    private static final String MAX_ACC_WIDTH = "maxAccWidth";

    static Rectangle zeroRect = new Rectangle(0, 0, 0, 0);
    static Rectangle iconRect = new Rectangle();
    static Rectangle textRect = new Rectangle();
    static Rectangle acceleratorRect = new Rectangle();
    static Rectangle checkIconRect = new Rectangle();
    static Rectangle arrowIconRect = new Rectangle();
    static Rectangle viewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
    static Rectangle r = new Rectangle();

    private final JMenuItem menuItem;
    private final Font acceleratorFont;
    private final Color selectionForeground;
    private final Color disabledForeground;
    private final Color acceleratorForeground;
    private final Color acceleratorSelectionForeground;
    private final String acceleratorDelimiter;
    private final Icon fillerIcon;

    /**
     * Constructs a MenuItemRenderer for the specified menu item and settings.
     */
    public MenuItemRenderer(JMenuItem menuItem, Font acceleratorFont, Color selectionForeground,
            Color disabledForeground, Color acceleratorForeground, Color acceleratorSelectionForeground)
    {
        this.menuItem = menuItem;
        this.acceleratorFont = acceleratorFont;
        this.selectionForeground = selectionForeground;
        this.disabledForeground = disabledForeground;
        this.acceleratorForeground = acceleratorForeground;
        this.acceleratorSelectionForeground = acceleratorSelectionForeground;
        this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
        this.fillerIcon = new MinimumSizedIcon();
    }

    /**
     * Looks up and answers the appropriate menu item icon.
     */
    private static Icon getIcon(JMenuItem aMenuItem, Icon defaultIcon)
    {
        Icon icon = aMenuItem.getIcon();
        if (icon == null)
            return defaultIcon;

        ButtonModel model = aMenuItem.getModel();
        if (!model.isEnabled())
        {
            return model.isSelected() ?
            		aMenuItem.getDisabledSelectedIcon() :
                    aMenuItem.getDisabledIcon();
        }
        else if (model.isPressed() && model.isArmed())
        {
            Icon pressedIcon = aMenuItem.getPressedIcon();
            return pressedIcon != null ? pressedIcon : icon;
        }
        else if (model.isSelected())
        {
            Icon selectedIcon = aMenuItem.getSelectedIcon();
            return selectedIcon != null ? selectedIcon : icon;
        }
        else
            return icon;
    }

    /**
     * Answers the wrapped icon.
     */
    private Icon getWrappedIcon(Icon icon)
    {
        if (icon == null)
            return fillerIcon;
        else
            return new MinimumSizedIcon(icon);
    }

    private void resetRects()
    {
        iconRect.setBounds(zeroRect);
        textRect.setBounds(zeroRect);
        acceleratorRect.setBounds(zeroRect);
        checkIconRect.setBounds(zeroRect);
        arrowIconRect.setBounds(zeroRect);
        viewRect.setBounds(0, 0, Short.MAX_VALUE, Short.MAX_VALUE);
        r.setBounds(zeroRect);
    }

    public Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap)
    {
        JMenuItem b = (JMenuItem) c;
        String text = b.getText();
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = "";

        if (accelerator != null)
        {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0)
            {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                acceleratorText += acceleratorDelimiter;
            }
            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0)
            {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            }
            else
            {
                acceleratorText += accelerator.getKeyChar();
            }
        }

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);
        FontMetrics fmAccel = b.getFontMetrics(acceleratorFont);

        resetRects();

        Icon wrappedIcon = getWrappedIcon(getIcon(menuItem, checkIcon));
        Icon wrappedArrowIcon = getWrappedIcon(arrowIcon);

        layoutMenuItem(fm, text, fmAccel, acceleratorText,
                wrappedIcon, wrappedArrowIcon,
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(),
                b.getHorizontalTextPosition(), viewRect, iconRect,
                textRect, acceleratorRect, checkIconRect, arrowIconRect,
                text == null ? 0 : defaultTextIconGap, defaultTextIconGap);

        // find the union of the icon and text rects
        r.setBounds(textRect);
        r = SwingUtilities.computeUnion(iconRect.x, iconRect.y, iconRect.width, iconRect.height, r);
        // r = iconRect.union(textRect);

        // To make the accelerator texts appear in a column, find the widest MenuItem text
        // and the widest accelerator text.

        // Get the parent, which stores the information.
        Container parent = menuItem.getParent();

        // Check the parent, and see that it is not a top-level menu.
        if (parent != null
                && parent instanceof JComponent
                && !(menuItem instanceof JMenu && ((JMenu) menuItem)
                        .isTopLevelMenu()))
        {
            JComponent p = (JComponent) parent;

            // Get widest text so far from parent, if no one exists null is returned.
            Integer maxTextWidth = (Integer) p.getClientProperty(MAX_TEXT_WIDTH);
            Integer maxAccWidth = (Integer) p.getClientProperty(MAX_ACC_WIDTH);

            int maxTextValue = maxTextWidth != null ? maxTextWidth.intValue() : 0;
            int maxAccValue = maxAccWidth != null ? maxAccWidth.intValue() : 0;

            // Compare the text widths, and adjust the r.width to the widest.
            if (r.width < maxTextValue)
                r.width = maxTextValue;
            else
                p.putClientProperty(MAX_TEXT_WIDTH, new Integer(r.width));

            // Compare the accelarator widths.
            if (acceleratorRect.width > maxAccValue)
            {
                maxAccValue = acceleratorRect.width;
                p.putClientProperty(MAX_ACC_WIDTH, new Integer(acceleratorRect.width));
            }

            // Add on the widest accelerator
            r.width += maxAccValue;
            r.width += 10;
        }

        if (useCheckAndArrow())
        {
            // Add in the checkIcon
            r.width += checkIconRect.width;
            r.width += defaultTextIconGap;

            // Add in the arrowIcon
            r.width += defaultTextIconGap;
            r.width += arrowIconRect.width;
        }

        r.width += 2 * defaultTextIconGap;

        Insets insets = b.getInsets();

        if (insets != null)
        {
            r.width += insets.left + insets.right;
            r.height += insets.top + insets.bottom;
        }

        if (r.height % 2 == 1)
            r.height++;

        return r.getSize();
    }

    public void paintMenuItem(Graphics g, JComponent c, Icon checkIcon,
            Icon arrowIcon, Color background, Color foreground,
            int defaultTextIconGap)
    {
        JMenuItem b = (JMenuItem) c;
        ButtonModel model = b.getModel();

        // Dimension size = b.getSize();
        int menuWidth = b.getWidth();
        int menuHeight = b.getHeight();
        Insets i = c.getInsets();

        resetRects();

        viewRect.setBounds(0, 0, menuWidth, menuHeight);
        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= (i.right + viewRect.x);
        viewRect.height -= (i.bottom + viewRect.y);

        Font holdf = g.getFont();
        Font f = c.getFont();
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        FontMetrics fmAccel = g.getFontMetrics(acceleratorFont);

        // get Accelerator text
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = "";
        if (accelerator != null)
        {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0)
            {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                acceleratorText += acceleratorDelimiter;
            }

            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0)
                acceleratorText += KeyEvent.getKeyText(keyCode);
            else
                acceleratorText += accelerator.getKeyChar();
        }

        Icon wrappedIcon = getWrappedIcon(getIcon(menuItem, checkIcon));
        Icon wrappedArrow = new MinimumSizedIcon(arrowIcon);

        // layout the text and icon
        int accOffset = layoutMenuItem(fm, b.getText(), fmAccel, acceleratorText,
                wrappedIcon, wrappedArrow,
                b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(),
                b.getHorizontalTextPosition(), viewRect, iconRect,
                textRect, acceleratorRect, checkIconRect, arrowIconRect,
                b.getText() == null ? 0 : defaultTextIconGap, defaultTextIconGap);

        // Paint background
        paintBackground(g, b, background);

        // Paint icon
        Color holdc = g.getColor();
        if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
            g.setColor(foreground);

        wrappedIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
        g.setColor(holdc);

        // Draw the Text
        if (b.getText() != null)
        {
            View v = (View) c.getClientProperty(HTML_KEY);
            if (v != null)
                v.paint(g, textRect);
            else
                paintText(g, b, textRect, b.getText());
        }

        // Draw the Accelerator Text
        if (!"".equals(acceleratorText))
        {
            g.setFont(acceleratorFont);

            if (!model.isEnabled())
                g.setColor(disabledForeground);
            else
            if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
                g.setColor(acceleratorSelectionForeground);
            else
                g.setColor(acceleratorForeground);

            BasicGraphicsUtils.drawString(g, acceleratorText, 0, acceleratorRect.x - accOffset, acceleratorRect.y + fmAccel.getAscent());
        }

        // Paint the Arrow
        if (arrowIcon != null)
        {
            if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
                g.setColor(foreground);
            if (useCheckAndArrow())
                wrappedArrow.paintIcon(c, g, arrowIconRect.x,  arrowIconRect.y);
        }
        g.setColor(holdc);
        g.setFont(holdf);
    }

    /**
     * Compute and return the location of the icons origin, the location of
     * origin of the text baseline, and a possibly clipped version of the
     * compound labels string. Locations are computed relative to the viewRect
     * rectangle.
     */
    private int layoutMenuItem(FontMetrics fm, String text,
                               FontMetrics fmAccel, String acceleratorText,
                               Icon checkIcon, Icon arrowIcon, int verticalAlignment,
                               int horizontalAlignment, int verticalTextPosition,
                               int horizontalTextPosition, Rectangle viewRectangle,
                               Rectangle iconRectangle, Rectangle textRectangle,
                               Rectangle acceleratorRectangle, Rectangle checkIconRectangle,
                               Rectangle arrowIconRectangle, int textIconGap, int menuItemGap)
    {
    	int accOffset = 0;
        SwingUtilities.layoutCompoundLabel(menuItem, fm, text, null,
                verticalAlignment, horizontalAlignment, verticalTextPosition,
                horizontalTextPosition, viewRectangle, iconRectangle,
                textRectangle, textIconGap);

        /*
         * Initialize the acceleratorText bounds rectangle textRect. If a null
         * or and empty String was specified we substitute "" here and use
         * 0,0,0,0 for acceleratorTextRect.
         */
        if ((acceleratorText == null) || acceleratorText.equals(""))
        {
            acceleratorRectangle.width = acceleratorRectangle.height = 0;
        }
        else
        {
            acceleratorRectangle.width = SwingUtilities.computeStringWidth(fmAccel, acceleratorText);
            acceleratorRectangle.height = fmAccel.getHeight();
        }

        boolean useCheckAndArrow = useCheckAndArrow();

        // Initialize the checkIcon bounds rectangle's width & height.
        if (useCheckAndArrow)
        {
            if (checkIcon != null)
            {
                checkIconRectangle.width = checkIcon.getIconWidth();
                checkIconRectangle.height = checkIcon.getIconHeight();
            }
            else
                checkIconRectangle.width = checkIconRectangle.height = 0;

            // Initialize the arrowIcon bounds rectangle width & height.
            if (arrowIcon != null)
            {
                arrowIconRectangle.width = arrowIcon.getIconWidth();
                arrowIconRectangle.height = arrowIcon.getIconHeight();
            }
            else
                arrowIconRectangle.width = arrowIconRectangle.height = 0;
        }

        Rectangle labelRect = iconRectangle.union(textRectangle);
        if (isLeftToRight(menuItem))
        {
            textRectangle.x += menuItemGap;
            iconRectangle.x += menuItemGap;

            // Position the Accelerator text rect
            acceleratorRectangle.x = viewRectangle.x + viewRectangle.width
                    - arrowIconRectangle.width - menuItemGap
                    - acceleratorRectangle.width;

            // Position the Check and Arrow Icons
            if (useCheckAndArrow)
            {
                checkIconRectangle.x = viewRectangle.x;
                textRectangle.x += menuItemGap + checkIconRectangle.width;
                iconRectangle.x += menuItemGap + checkIconRectangle.width;
                arrowIconRectangle.x = viewRectangle.x + viewRectangle.width - menuItemGap - arrowIconRectangle.width;
            }

        	Container parent = menuItem.getParent();
        	if (parent != null && parent instanceof JComponent)
            {
                JComponent p = (JComponent) parent;
                Integer maxValueInt = ((Integer) p.getClientProperty(MAX_ACC_WIDTH));
                int maxValue = maxValueInt != null ? maxValueInt.intValue() : acceleratorRect.width;
                accOffset = maxValue - acceleratorRect.width;

            }
        }
        else
        {
            textRectangle.x -= menuItemGap;
            iconRectangle.x -= menuItemGap;

            // Position the Accelerator text rect
            acceleratorRectangle.x = viewRectangle.x + arrowIconRectangle.width + menuItemGap;

            // Position the Check and Arrow Icons
            if (useCheckAndArrow)
            {
                checkIconRectangle.x = viewRectangle.x + viewRectangle.width - checkIconRectangle.width;
                textRectangle.x -= menuItemGap + checkIconRectangle.width;
                iconRectangle.x -= menuItemGap + checkIconRectangle.width;
                arrowIconRectangle.x = viewRectangle.x + menuItemGap;
            }
        }

        // Align the accelerator text and the check and arrow icons vertically
        // with the center of the label rect.
        acceleratorRectangle.y = labelRect.y + (labelRect.height / 2) - (acceleratorRectangle.height / 2);
        if (useCheckAndArrow)
        {
            arrowIconRectangle.y = labelRect.y + (labelRect.height / 2) - (arrowIconRectangle.height / 2);
            checkIconRectangle.y = labelRect.y + (labelRect.height / 2) - (checkIconRectangle.height / 2);
        }
        return accOffset;

    }

    /*
     * Returns false if the component is a JMenu and it is a top level menu (on
     * the menubar).
     */
    private boolean useCheckAndArrow()
    {
        boolean isTopLevelMenu = menuItem instanceof JMenu  && ((JMenu) menuItem).isTopLevelMenu();
        return !isTopLevelMenu;
    }

    private static boolean isLeftToRight(Component c)
    {
        return c.getComponentOrientation().isLeftToRight();
    }

    // Copies from 1.4.1 ****************************************************

    /**
     * Draws the background of the menu item. Copied from 1.4.1 BasicMenuItem to
     * make it visible to the MenuItemLayouter
     *
     * @param g the paint graphics
     * @param aMenuItem menu item to be painted
     * @param bgColor selection background color
     * @since 1.4
     */
    public void paintBackground(Graphics g, JMenuItem aMenuItem, Color bgColor)
    {
        ButtonModel model = aMenuItem.getModel();
        int menuWidth  = aMenuItem.getWidth();
        int menuHeight = aMenuItem.getHeight();
        Color oldColor = g.getColor();

        if (model.isArmed() || (aMenuItem instanceof JMenu && model.isSelected()))
        {
        	LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
            g.setColor(theme.getMenuItemSelectedBackground());
            g.fillRect(1,1,menuWidth-2, menuHeight-2);
            LipstikBorderFactory.paintRoundBorder(g, 1,0, menuWidth-2, menuHeight, theme, bgColor, LipstikBorderFactory.BORDER_BRIGHTER);
        }
        else
        {
        	int offs = menuItem.isOpaque() ? 0 : LipstikBorderFactory.ICON_BAR_WIDTH;
            g.setColor(menuItem.getBackground());
            g.fillRect(offs, 0, menuWidth-offs, menuHeight);
        }
        g.setColor(oldColor);
    }

    /**
     * Renders the text of the current menu item.
     * <p>
     *
     * @param g graphics context
     * @param aMenuItem menu item to render
     * @param textRectangle bounding rectangle for rendering the text
     * @param text string to render
     * @since 1.4
     */
    public void paintText(Graphics g, JMenuItem aMenuItem, Rectangle textRectangle, String text)
    {
        ButtonModel model = aMenuItem.getModel();
        FontMetrics fm = g.getFontMetrics();
        int mnemIndex = aMenuItem.getDisplayedMnemonicIndex();

        if (!model.isEnabled())
        {
            // *** paint the text disabled
            if (UIManager.get("MenuItem.disabledForeground") instanceof Color)
            {
                g.setColor(UIManager.getColor("MenuItem.disabledForeground"));
                drawStringUnderlineCharAt(g, text, mnemIndex, textRectangle.x, textRectangle.y + fm.getAscent());
            } else
            {
                g.setColor(aMenuItem.getBackground().brighter());
                drawStringUnderlineCharAt(g, text, mnemIndex, textRectangle.x, textRectangle.y + fm.getAscent());
                g.setColor(aMenuItem.getBackground().darker());
                drawStringUnderlineCharAt(g, text, mnemIndex, textRectangle.x - 1, textRectangle.y + fm.getAscent()- 1);
            }
        }
        else
        {
            // *** paint the text normally
            if (model.isArmed() || (aMenuItem instanceof JMenu && model.isSelected()))
                g.setColor(selectionForeground); // Uses protected field.

            drawStringUnderlineCharAt(g, text, mnemIndex, textRectangle.x, textRectangle.y + fm.getAscent());
        }
    }

    /**
     * Draws a string with the graphics <code>g</code> at location (<code>x</code>,
     * <code>y</code>) just like <code>g.drawString</code> would. The
     * character at index <code>underlinedIndex</code> in text will be
     * underlined. If <code>index</code> is beyond the bounds of
     * <code>text</code> (including < 0), nothing will be underlined.
     *
     * @param g Graphics to draw with
     * @param text String to draw
     * @param underlinedIndex Index of character in text to underline
     * @param x x coordinate to draw at
     * @param y y coordinate to draw at
     * @since 1.4
     */
    public static void drawStringUnderlineCharAt(Graphics g, String text, int underlinedIndex, int x, int y)
    {
        g.drawString(text, x, y);
        if (underlinedIndex >= 0 && underlinedIndex < text.length())
        {
            FontMetrics fm = g.getFontMetrics();
            int underlineRectX = x + fm.stringWidth(text.substring(0, underlinedIndex));
            int underlineRectWidth = fm.charWidth(text.charAt(underlinedIndex));
            int underlineRectHeight = 1;
            g.fillRect(underlineRectX, y + fm.getDescent() - 1, underlineRectWidth, underlineRectHeight);
        }
    }
}

class MinimumSizedIcon implements Icon
{
    private final Icon icon;
    private final int width;
    private final int height;
    private final int xOffset;
    private final int yOffset;

    public MinimumSizedIcon()
    {
        this(null);
    }

    public MinimumSizedIcon(Icon icon)
    {
        this.icon = icon;
        int iconWidth = icon == null ? 0 : icon.getIconWidth();
        int iconHeight = icon == null ? 0 : icon.getIconHeight();
        width = Math.max(iconWidth, 26);
        height = Math.max(iconHeight, 26);
        xOffset = Math.max(0, (width - iconWidth) >> 1);
        yOffset = Math.max(0, (height - iconHeight) >> 1);
    }

    public int getIconHeight()
    {
        return height;
    }

    public int getIconWidth()
    {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        if (icon != null)
            icon.paintIcon(c, g, x + xOffset, y + yOffset);
    }
}
