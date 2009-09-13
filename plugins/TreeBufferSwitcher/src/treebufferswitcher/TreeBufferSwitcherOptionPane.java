package treebufferswitcher;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

// TODO: deny duplicates in shortcuts
public class TreeBufferSwitcherOptionPane extends AbstractOptionPane implements ActionListener {

    // components
    private JCheckBox enabled;
    private JComboBox provider;
    private JCheckBox useShortcuts;
    private JTextField shortcuts;
    private JSpinner visibleRowCount;

    public TreeBufferSwitcherOptionPane() {
        super("TreeBufferSwitcher");
    }

    @Override
    protected void _init() {
        final TreeBufferSwitcherPlugin plugin = TreeBufferSwitcherPlugin.instance();
        enabled = new JCheckBox(jEdit.getProperty("messages.TreeBufferSwitcher.enabled"), plugin.isEnabled());
        enabled.addActionListener(this);
        final List<String> providerNames = new ArrayList<String>(TreeBufferSwitcherPlugin.GroupingMode.values().length);
        for (TreeBufferSwitcherPlugin.GroupingMode groupingMode : TreeBufferSwitcherPlugin.GroupingMode.values()) {
            providerNames.add(jEdit.getProperty("messages.TreeBufferSwitcher.provider." + groupingMode));
        }
        provider = new JComboBox(providerNames.toArray());
        provider.setSelectedIndex(plugin.getGroupingMode().ordinal());
        useShortcuts = new JCheckBox(jEdit.getProperty("messages.TreeBufferSwitcher.use-shortcuts"), plugin.isUseShortcuts());
        useShortcuts.addActionListener(this);
        shortcuts = new JTextField(plugin.getShortcuts());
        visibleRowCount = new JSpinner(new SpinnerNumberModel(plugin.getVisibleRowCount(), 1, 100, 1));
        addComponent(enabled);
        addComponent(jEdit.getProperty("messages.TreeBufferSwitcher.provider"), provider);
        addComponent(useShortcuts);
        addComponent(jEdit.getProperty("messages.TreeBufferSwitcher.shortcuts"), shortcuts);
        addComponent(jEdit.getProperty("messages.TreeBufferSwitcher.visible-row-count"), visibleRowCount);
        updateState();
    }

    private void updateState() {
        provider.setEnabled(enabled.isSelected());
        useShortcuts.setEnabled(enabled.isSelected());
        shortcuts.setEnabled(enabled.isSelected() && useShortcuts.isSelected());
        visibleRowCount.setEnabled(enabled.isSelected());
    }

    public void actionPerformed(ActionEvent e) {
        updateState();
    }

    @Override
    protected void _save() {
        jEdit.setBooleanProperty("treebufferswitcher.enabled", enabled.isSelected());
        jEdit.setProperty("treebufferswitcher.provider", TreeBufferSwitcherPlugin.GroupingMode.values()[provider.getSelectedIndex()].name());
        jEdit.setBooleanProperty("treebufferswitcher.use-shortcuts", useShortcuts.isSelected());
        jEdit.setProperty("treebufferswitcher.shortcuts", shortcuts.getText());
        jEdit.setIntegerProperty("treebufferswitcher.visible-row-count", (Integer)visibleRowCount.getValue());
    }

}