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
        optionPanel.lengthSpinner.setValue(Integer.valueOf(InputReplacePlugin.maxLength));
        addComponent(optionPanel);
    }

    protected void _save() {
        InputReplacePlugin.maxLength = (Integer) optionPanel.lengthSpinner.getValue();
        jEdit.setIntegerProperty("inputreplace.max-length", InputReplacePlugin.maxLength);
    }
}
