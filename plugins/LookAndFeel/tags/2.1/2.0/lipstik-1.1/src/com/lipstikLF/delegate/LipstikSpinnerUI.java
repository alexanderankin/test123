package com.lipstikLF.delegate;


import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class LipstikSpinnerUI extends BasicSpinnerUI
{
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikSpinnerUI();
    }

    protected void installListeners() {
        super.installListeners();

        spinner.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                String propertyName = evt.getPropertyName();
                if (propertyName.equals("componentOrientation") && spinner != null)
                {
                    JComponent editor = spinner.getEditor();

                    if (editor instanceof JPanel &&	editor.getBorder() == null && editor.getComponentCount() > 0)
                        editor = (JComponent)editor.getComponent(0);

                    if (editor != null)
                        editor.setComponentOrientation((ComponentOrientation)evt.getNewValue());

                    spinner.setComponentOrientation((ComponentOrientation)evt.getNewValue());
                }
            }
        });
    }

    protected JComponent createEditor()
    {
        JComponent editor = spinner.getEditor();
        setEditorBorder(editor);
        updateEditorAlignment(editor);
        return editor;
    }

    private void updateEditorAlignment(JComponent editor)
    {
        if (editor instanceof JSpinner.DefaultEditor)
        {
            // if editor alignment isn't set in LAF, we get 0 (CENTER) here
            int alignment = UIManager.getInt("Spinner.editorAlignment");
            JTextField text = ((JSpinner.DefaultEditor)editor).getTextField();
            text.setHorizontalAlignment(alignment);
        }
    }

    private void setEditorBorder(JComponent editor)
    {
        if (!UIManager.getBoolean("Spinner.editorBorderPainted"))
        {
            if (editor instanceof JPanel &&	editor.getBorder() == null && editor.getComponentCount() > 0)
                editor = (JComponent)editor.getComponent(0);
        }

        if (editor != null && editor.getBorder() instanceof UIResource)
        {
            editor.setBorder(UIManager.getBorder("Spinner.editorBorder"));
            editor.setBackground(UIManager.getColor("text"));
        }
    }

}
