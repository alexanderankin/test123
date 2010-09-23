package ise.plugin.nav;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

/**
 * A JOptionPane won't quite work for this, I want the text field to only accept
 * numbers greater than or equal to 0.
 */
public class GoToLineDialog extends JDialog {

    static int previousEntry = -1;

    private View parent;
    private LineNumberTextField lineEntry;
    private JButton okButton;
    private JButton cancelButton;
    
    private boolean cancelled = false;

    public GoToLineDialog(View parent) {
        super(parent, jEdit.getProperty("navigator.gotoLine", "Go to line"), true);
        this.parent = parent;
        installUI();
        installListeners();
        setVisible(true);
    }

    private void installUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(11, 11, 12, 12));

        Icon icon = GUIUtilities.loadIcon("22x22/actions/go-jump.png");

        lineEntry = new LineNumberTextField(previousEntry == -1 ? "" : String.valueOf(previousEntry), 15);

        okButton = new JButton(jEdit.getProperty("common.ok", "OK"));
        cancelButton = new JButton(jEdit.getProperty("common.cancel", "Cancel"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 6));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(new JLabel(icon), BorderLayout.WEST);
        mainPanel.add(lineEntry, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        pack();
        setLocationRelativeTo(parent);
        lineEntry.requestFocus();
        lineEntry.selectAll();
        getRootPane().setDefaultButton(okButton);        
    }

    private void installListeners() {
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if ("".equals(lineEntry.getText())) {
                    cancelled = true;       
                }
                else {
                    int line = Integer.parseInt(lineEntry.getText());
                    previousEntry = line >= 0 ? line : 0;
                }
                close();
            }
        } );
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener (new ActionListener() {
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
     * @return The line number entered by the user or -1 if the user cancelled.    
     */
    public int getLineNumber() {
        if (cancelled) {
            return -1;   
        }
        return previousEntry - 1 >= 0 ? previousEntry - 1 : 0;
    }
}
