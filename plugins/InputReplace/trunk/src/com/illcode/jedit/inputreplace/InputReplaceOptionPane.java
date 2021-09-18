package com.illcode.jedit.inputreplace;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class InputReplaceOptionPane extends AbstractOptionPane
{
    private OptionPanel optionPanel;

    public InputReplaceOptionPane() {
        super("inputreplace");
    }

    protected void _init() {
        optionPanel = new OptionPanel();
        optionPanel.maxLengthSpinner.setValue(Integer.valueOf(InputReplacePlugin.maxLength));
        optionPanel.minLengthSpinner.setValue(Integer.valueOf(InputReplacePlugin.minLength));
        addComponent(optionPanel);
    }

    protected void _save() {
        InputReplacePlugin.maxLength = (Integer) optionPanel.maxLengthSpinner.getValue();
        InputReplacePlugin.minLength = (Integer) optionPanel.minLengthSpinner.getValue();
        if (InputReplacePlugin.minLength > InputReplacePlugin.maxLength)
            InputReplacePlugin.minLength = InputReplacePlugin.maxLength;
        jEdit.setIntegerProperty("inputreplace.max-length", InputReplacePlugin.maxLength);
        jEdit.setIntegerProperty("inputreplace.min-length", InputReplacePlugin.minLength);
    }
}
