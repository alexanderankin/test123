package com.lipstikLF.delegate;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serializable;

import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;
import com.lipstikLF.util.LipstikBorderFactory;


public class LipstikComboBoxButton extends JButton
{
    private final JList listBox;
    private final CellRendererPane rendererPane;
    private static final ComboBoxButtonIcon comboIcon = new ComboBoxButtonIcon();

    private JComboBox comboBox;
    protected boolean iconOnly = false;

    LipstikComboBoxButton(JComboBox comboBox,boolean iconOnly,
                          CellRendererPane rendererPane, JList listBox)
    {
        super("");
        setModel(new DefaultButtonModel()
            {
                public void setArmed(boolean armed)
                {
                    super.setArmed(isPressed() || armed);
                }
            });
        this.comboBox  = comboBox;
        this.iconOnly  = iconOnly;
        this.rendererPane = rendererPane;
        this.listBox = listBox;
        setEnabled(comboBox.isEnabled());
        setFocusable(false);
        setRequestFocusEnabled(comboBox.isEnabled());
        if (iconOnly)
            setBorder(UIManager.getBorder("ComboBox.arrowButtonBorder"));
        setMargin(new Insets(2, 4, 0, 3));
    }

    public JComboBox getComboBox()
    {
        return comboBox;
    }

    public void setComboBox(JComboBox cb)
    {
        comboBox = cb;
    }

    public Icon getComboIcon()
    {
        return comboIcon;
    }

    public boolean isIconOnly()
    {
        return iconOnly;
    }

    public boolean isFocusTraversable()
	{
		  return false;
	}

    public void setIconOnly(boolean b)
    {
        iconOnly = b;
    }

    /**
     * Paints the component; honors the 3D settings and
     * tries to switch the renderer component to transparent.
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        boolean leftToRight = comboBox.getComponentOrientation().isLeftToRight();

        Insets insets = getInsets();

        int width  = getWidth()  - (insets.left + insets.right);
        int height = getHeight() - (insets.top  + insets.bottom);

        if (height <= 0 || width <= 0)
            return;

        int left = insets.left;
        int top  = insets.top;

        int iconLeft;
        int iconWidth = 0;
        int hoffs = 0;
        int xoffs = 0;

        // Paint the icon
        if (comboIcon != null)
        {
            iconWidth = comboIcon.getIconWidth();
            int iconHeight = comboIcon.getIconHeight();
            int iconTop = (getHeight() - iconHeight) >> 1;

            if (iconOnly)
            {
                iconLeft = (width  - iconWidth)  >> 1;
                if (!leftToRight) iconLeft++;
            }
            else
            {
                if (leftToRight)
                {
                    iconLeft = (left + (width)) - iconWidth;
                    hoffs = iconLeft-4;
                    xoffs = left;
                }
                else
                {
                    iconLeft = left;
                    hoffs = iconLeft+iconWidth+2;
                    xoffs = hoffs+5;
                }
            }
            comboIcon.paintIcon(this, g, iconLeft, iconTop);
        }

        // Let the renderer paint
        if (!iconOnly && comboBox != null)
        {
            LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
            g.setColor(theme.getControlDarkShadow());
            g.drawLine(hoffs,0,hoffs,height+1);

            ListCellRenderer renderer = comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(
                    listBox,
                    comboBox.getSelectedItem(),
                    -1,
                    getModel().isPressed(),
                    false);
            c.setFont(rendererPane.getFont());

            if (model.isArmed() && model.isPressed())
            {
                if (isOpaque())
                    c.setBackground(UIManager.getColor("Button.select"));

                c.setForeground(comboBox.getForeground());
            }
            else
            if (!comboBox.isEnabled())
            {
                if (isOpaque())
                    c.setBackground(UIManager.getColor("ComboBox.disabledBackground"));

                c.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
            }
            else
            {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            }

            int cWidth = width - (insets.right + iconWidth);

            // Fix for 4238829: should lay out the JPanel.
            boolean shouldValidate = c instanceof JPanel;
            if (c instanceof JComponent)
            {
                // In case, we are in 3D mode _and_ have a JComponent renderer,
                // store the opaque state, set it to transparent, paint, then restore.
                JComponent component = (JComponent) c;
                component.setOpaque(false);
                rendererPane.paintComponent(
                    g,
                    c,
                    this,
                    xoffs,
                    top+1,
                    cWidth,
                    height-3,
                    shouldValidate);
                component.setOpaque(true);
            }
            else
            {
                rendererPane.paintComponent(
                    g,
                    c,
                    this,
                    xoffs,
                    top+1,
                    cWidth,
                    height-3,
                    shouldValidate);
            }
        }

        if (comboBox != null && comboBox.hasFocus())
        	LipstikBorderFactory.paintFocusBorder(g, 2, 2, getWidth()-4, getHeight()-4);
    }
}

class ComboBoxButtonIcon implements Icon, Serializable
{

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        JComponent component = (JComponent)c;
        int iconWidth = getIconWidth();

        g.translate(x, y);
        g.setColor( component.isEnabled()
                    ? MetalLookAndFeel.getControlInfo()
                    : MetalLookAndFeel.getControlDarkShadow() );
        g.drawLine( 0, 0, iconWidth - 1, 0 );
        g.drawLine( 1, 1, 1 + (iconWidth - 3), 1 );
        g.drawLine( 2, 2, 2 + (iconWidth - 5), 2 );
        g.drawLine( 3, 3, 3 + (iconWidth - 7), 3 );
        g.translate( -x, -y );
    }

    public int getIconWidth()  { return 8; }
    public int getIconHeight() { return 4; }
}
