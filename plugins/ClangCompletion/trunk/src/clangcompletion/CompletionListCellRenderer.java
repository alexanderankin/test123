package clangcompletion;

import java.awt.Component;
import javax.swing.*;
import completion.util.*;


public class CompletionListCellRenderer extends DefaultListCellRenderer
{

    public CompletionListCellRenderer()
    {
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        JLabel renderer = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ClangCompletionCandidate cc = (ClangCompletionCandidate)value;
        renderer.setText(CompletionUtil.prefixByIndex(cc.getLabelText(), index));
        return renderer;
    }
}
