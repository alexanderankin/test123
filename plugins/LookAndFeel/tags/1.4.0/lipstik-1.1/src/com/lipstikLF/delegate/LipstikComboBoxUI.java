package com.lipstikLF.delegate;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.theme.LipstikColorTheme;


public class LipstikComboBoxUI extends MetalComboBoxUI
{
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikComboBoxUI();
    }

    /**
     * Creates the editor that is to be used in editable combo boxes.
     * This method only gets called if a custom editor has not already
     * been installed in the JComboBox.
     */
    protected ComboBoxEditor createEditor()
    {
        return new LipstikComboBoxEditor.UIResource();
    }

    protected ComboPopup createPopup()
    {
        return new LipstikComboPopup(comboBox);
    }

    /**
     * Creates and answers the arrow button that is to be used in the combo box.s
     */
    protected JButton createArrowButton()
    {
        return new LipstikComboBoxButton(
            comboBox,
            comboBox.isEditable(),
            currentValuePane,
            listBox);
    }

    public PropertyChangeListener createPropertyChangeListener()
    {
        return new LipstikPropertyChangeListener();
    }

    private class LipstikPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            super.propertyChange(e);
            String propertyName = e.getPropertyName();

            if (propertyName.equals("editable"))
            {
                LipstikComboBoxButton button = (LipstikComboBoxButton) arrowButton;

                button.setIconOnly(comboBox.isEditable());
                button.setBorder(comboBox.isEditable() ?
                		UIManager.getBorder("ComboBox.arrowButtonBorder") :
                		UIManager.getBorder("Button.border"));
                comboBox.repaint();
            }
            else
            if (propertyName.equals("background"))
            {
                Color color = (Color) e.getNewValue();
                arrowButton.setBackground(color);
                listBox.setBackground(color);

            }
            else
            if (propertyName.equals("foreground"))
            {
                Color color = (Color) e.getNewValue();
                arrowButton.setForeground(color);
                listBox.setForeground(color);
            }
            else
            if (propertyName.equals("componentOrientation"))
            {
            	if (editor != null)
            		editor.setComponentOrientation((ComponentOrientation)e.getNewValue());

            	comboBox.setComponentOrientation((ComponentOrientation)e.getNewValue());
            	listBox.setComponentOrientation((ComponentOrientation)e.getNewValue());
            }
        }
    }
    /**
     * Overriden to correct the combobox height.
     */
    public Dimension getMinimumSize(JComponent c)
    {
        if (!isMinimumSizeDirty)
            return new Dimension(cachedMinimumSize);

        Dimension size;

        if (!comboBox.isEditable()
            && arrowButton != null
            && arrowButton instanceof LipstikComboBoxButton)
        {

            LipstikComboBoxButton button = (LipstikComboBoxButton) arrowButton;
            Insets buttonInsets = button.getInsets();
            Insets buttonMargin = button.getMargin();
            Insets insets = comboBox.getInsets();
            size = getDisplaySize();

            /*
             * The next line will lead to good results if used with standard renderers;
             * In case, a custom renderer is used, it may use a different height,
             * and we can't help much.
             */
            size.width  += insets.left + insets.right;
            size.width  += buttonInsets.left + buttonInsets.right;
            size.width  += buttonMargin.left + buttonMargin.right;
            size.width  += button.getComboIcon().getIconWidth();
            size.height += insets.top + insets.bottom;
            size.height += buttonInsets.top + buttonInsets.bottom;
        }
        else
        if (comboBox.isEditable() && arrowButton != null && editor != null)
        {
            // The display size does often not include the editor's insets
            size = getDisplaySize();
            Insets insets = comboBox.getInsets();

            int buttonWidth = UIManager.getInt("ScrollBar.width");

            size.width  += insets.left + insets.right;
            size.width  += buttonWidth - 2;
            size.height += insets.top + insets.bottom+2;
        }
        else
        	size = super.getMinimumSize(c);

        cachedMinimumSize.setSize(size.width, size.height);
        isMinimumSizeDirty = false;
        return new Dimension(cachedMinimumSize);
    }

    protected LayoutManager createLayoutManager()
    {
        return new LipstikComboBoxLayoutManager();
    }

    private class LipstikComboBoxLayoutManager extends MetalComboBoxUI.MetalComboBoxLayoutManager
    {
        public void layoutContainer(Container parent)
        {
            JComboBox cb = (JComboBox) parent;

            // Use superclass behavior if the combobox is not editable.
            if (!cb.isEditable())
            {
                super.layoutContainer(parent);
                return;
            }

            int width  = cb.getWidth();
            int height = cb.getHeight();

            Insets insets = getInsets();
            int buttonWidth  = 15;
            int buttonHeight = height - (insets.top + insets.bottom);

            if (arrowButton != null)
                if (cb.getComponentOrientation().isLeftToRight())
                {
                    arrowButton.setBounds(
                        width - (insets.right + buttonWidth),
                        insets.top,
                        buttonWidth,
                        buttonHeight);
                }
                else
                {
                    arrowButton.setBounds(
                        insets.left,
                        insets.top,
                        buttonWidth,
                        buttonHeight);
                }

            Rectangle rect = rectangleForCurrentValue();

            if (editor != null)
                editor.setBounds(rect);
        }
    }

    // Differs from the MetalComboPopup in that it uses the standard popmenu border.
    private static class LipstikComboPopup extends BasicComboPopup
    {
        private static Border LIST_BORDER = new LineBorder(LipstikLookAndFeel.getMyCurrentTheme().getBorderNormal(), 1);

        private LipstikComboPopup(JComboBox combo)
        {
            super(combo);
        }
        protected void configurePopup()
        {
            super.configurePopup();
        	setBorder(LIST_BORDER);   
        }
    }
}

class LipstikComboBoxEditor extends BasicComboBoxEditor
{
    LipstikComboBoxEditor()
    {
        editor = new JTextField("", UIManager.getInt("ComboBox.editorColumns"));
        editor.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
    }
    static final class UIResource extends LipstikComboBoxEditor implements javax.swing.plaf.UIResource
	{
	}
}