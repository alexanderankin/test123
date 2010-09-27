package ise.plugin.nav;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.TextArea;

import ise.java.awt.KappaLayout;

/**
 * A "go to line" dialog that uses the Navigator history lists.
 */
public class GoToLineDialog extends EscapeDialog {

    static NavPosition previousEntry = null;

    private View parent;
    private JComboBox lineChooser;
    private JButton okButton;
    private JButton cancelButton;

    private boolean cancelled = false;

    public GoToLineDialog(View parent) {
        super(parent, jEdit.getProperty("navigator.gotoLine", "Go to line"), true);
        this.parent = parent;
        installComponents();
        installListeners();
        setVisible(true);
    }

    private void installComponents() {
        JPanel mainPanel = new JPanel(new KappaLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(11, 11, 12, 12));

        Icon icon = GUIUtilities.loadIcon("22x22/actions/go-jump.png");

        // create the combobox model.  Only include items from the Navigator
        // list that are in the current edit pane and buffer by copying those
        // positions to a new NavStack.  A side effect of the copying is the
        // positions will be reversed in order, which is wanted.
        NavStack<NavPosition> combinedList = NavigatorPlugin.createNavigator(parent).getCombinedListModel();
        if (previousEntry != null) {
            combinedList.push(previousEntry);
        }
        NavStack comboBoxModel = new NavStack(combinedList.size());
        int editPaneHashCode = parent.getEditPane().hashCode();
        String bufferPath = parent.getBuffer().getPath();
        while (!combinedList.empty()) {
            NavPosition possible = combinedList.pop();
            if (possible.editPane == editPaneHashCode && possible.path.equals(bufferPath)) {
                comboBoxModel.push(possible);
            }
        }

        lineChooser = new JComboBox((ComboBoxModel) comboBoxModel);
        lineChooser.setEditable(true);
        if (comboBoxModel.size() > 0) {
            lineChooser.setSelectedIndex(0);
        }
        NumberTextField editor = new NumberTextField("", 15);
        editor.setMinValue(1);
        lineChooser.setEditor(editor);
        lineChooser.getEditor().selectAll();

        okButton = new JButton(jEdit.getProperty("common.ok", "OK"));
        cancelButton = new JButton(jEdit.getProperty("common.cancel", "Cancel"));

        KappaLayout buttonPanelLayout = new KappaLayout();
        JPanel buttonPanel = new JPanel(buttonPanelLayout);
        buttonPanel.add("0, 0, 1, 1, 0, w, 3", okButton);
        buttonPanel.add("1, 0, 1, 1, 0, w, 3", cancelButton);
        buttonPanelLayout.makeColumnsSameWidth(0, 1);

        mainPanel.add("0, 0, 1, 1, E, , 3", new JLabel(icon));
        mainPanel.add("1, 0, 2, 1, W, w, 3", lineChooser);
        mainPanel.add("0, 1", KappaLayout.createVerticalStrut(11));
        mainPanel.add("1, 2, 2, 1, E,  , 3", buttonPanel);

        setContentPane(mainPanel);

        pack();
        setLocationRelativeTo(parent);
        lineChooser.requestFocus();
        getRootPane().setDefaultButton(okButton);
    }

    private void installListeners() {
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object selectedItem = lineChooser.getSelectedItem();
                if (selectedItem == null || "".equals(selectedItem.toString())) {
                    cancelled = true;
                } else {
                    if (selectedItem instanceof NavPosition) {
                        previousEntry = (NavPosition) selectedItem;
                    }
                    int line = Integer.parseInt(selectedItem.toString());
                    EditPane editPane = parent.getEditPane();
                    TextArea textArea = editPane.getTextArea();
                    if (line >= textArea.getLineCount()) {
                        line = textArea.getLineCount() - 1;   
                    }
                    previousEntry = new NavPosition(editPane, parent.getBuffer(), textArea.getLineStartOffset(line - 1), textArea.getLineText(line));
                }
                close();
            }
        } );
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cancelled = true;
                close();
            }
        } );
    }

    private void close() {
        setVisible(false);
        dispose();
        parent.getTextArea().requestFocus();
    }

    /**
     * @return A NavPosition representing the line number in the current text area
     * as entered by the user or null if the user cancelled.
     */
    public NavPosition getLineNumber() {
        if (cancelled) {
            return null;
        }
        return previousEntry;
    }

    public void performEscapeAction(KeyEvent e) {
        close();
    }
}
