/**
 * FindFileDialog.java - The main search dialog.
 * :folding=explicit:collapseFolds=1:
 *
 * @author Nicholas O'Leary
 * @version $Id: FindFileDialog.java 13886 Thu Oct 16 16:01:01 CDT 2008 keeleyt83 $
 */

package findfile;

//{{{ imports
import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
//}}}

public class FindFileDialog extends EnhancedDialog implements ActionListener {

    //{{{ Private members
    private HistoryTextField directoryField;
    private HistoryTextField filterField;
    private JCheckBox recursive;
    private JCheckBox openAllResults;
    private JCheckBox keepDialog;
    private JCheckBox ignoreCase;
    private JCheckBox skipBinaryFiles;
    private JCheckBox skipHiddenBackups;
    private View view;
    //}}}

    //{{{ Constructor
    /**
     * Constructor.
     * @param view The active View.
     */
    public FindFileDialog(View view) {
        this(view,null);
    }//}}}

    //{{{ Constructor
    /**
     * Constructor.
     * @param view The active View.
     * @param path The initial search path.
     */
    // TODO: This is gross.
    public FindFileDialog(View view, String path) {
        super(view,jEdit.getProperty("FindFilePlugin.search-dialog.title"),false);
        this.setResizable(false);
        GUIUtilities.loadGeometry(this,"FindFilePlugin");
        this.view = view;

        JLabel directoryLabel, filterLabel;
        JButton findButton, closeButton;
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(content);

        JPanel innerContent = new JPanel(new GridBagLayout());
        innerContent.setBorder(new EmptyBorder(0,0,10,0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        if (path == null) {
            String userHome = System.getProperty("user.home");
            String defaultPath = jEdit.getProperty("vfs.browser.defaultPath");
            if (defaultPath.equals("home")) {
            path = userHome;
            } else {
                if (defaultPath.equals("working")) {
                    path = System.getProperty("user.dir");
                } else {
                    if (defaultPath.equals("buffer")) {
                        if (view != null) {
                            Buffer buffer = view.getBuffer();
                            path = buffer.getDirectory();
                        } else {
                            path = userHome;
                        }
                    } else {
                        path = userHome;
                    }
                }
            }
        }

        // Initialize filter objects and add them to panel.
        filterLabel = new JLabel(jEdit.getProperty("FindFilePlugin.search-dialog.labels.filter"));
        filterLabel.setBorder(new EmptyBorder(0,0,0,12));
        filterField = new HistoryTextField("FindFilePlugin.filter");
        filterField.setColumns(15);
        filterField.setText("*.*");
        filterField.addActionListener(this);

        innerContent.add(filterLabel, gbc);
        gbc.gridx = 1;
        innerContent.add(filterField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;

        // Initialize directory objects and add them to panel.
        directoryLabel = new JLabel(jEdit.getProperty("FindFilePlugin.search-dialog.labels.directory"));
        directoryLabel.setBorder(new EmptyBorder(0,0,0,12));
        directoryField = new HistoryTextField("FindFilePlugin.path");
        directoryField.setColumns(50);
        directoryField.setText(path);
        directoryField.addActionListener(this);
        
        innerContent.add(directoryLabel,gbc);
        gbc.gridx = 1;
        innerContent.add(directoryField,gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Initialize checkboxs and add to panel.
        // TODO: remember previous settings. Add other checkboxes.
        recursive = new JCheckBox(jEdit.getProperty("FindFilePlugin.option-pane-labels.recursiveSearch"));
        recursive.setSelected(jEdit.getProperty("options.FindFilePlugin.recursiveSearch","false").equals("true"));

        innerContent.add(recursive,gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        openAllResults = new JCheckBox(jEdit.getProperty("FindFilePlugin.option-pane-labels.openAllResults"));
        openAllResults.setSelected(jEdit.getProperty("options.FindFilePlugin.openAllResults","false").equals("true"));

        innerContent.add(openAllResults,gbc);

        // Initialize buttons and add to panel.
        content.add(BorderLayout.CENTER, innerContent);
        Box buttons = Box.createHorizontalBox();

        buttons.add(Box.createHorizontalGlue());
        findButton = new JButton(jEdit.getProperty("FindFilePlugin.search-dialog.buttons.find"));
        findButton.addActionListener(this);
        this.getRootPane().setDefaultButton(findButton);
        buttons.add(findButton);
        buttons.add(Box.createHorizontalStrut(10));

        closeButton = new JButton(jEdit.getProperty("FindFilePlugin.search-dialog.buttons.close"));
        closeButton.addActionListener(this);
        buttons.add(closeButton);
        buttons.add(Box.createHorizontalStrut(10));
        content.add(BorderLayout.SOUTH,buttons);
        
        pack();
    }//}}}

//    private JPanel createCriteriaPanel(String p) {
//
//        JLabel filterLabel;
//        JLabel directoryLabel;
//        JPanel criteriaPanel = new JPanel(new GridBagLayout());
//        criteriaPanel.setBorder(new EmptyBorder(0,0,12,12));
//
//        GridBagConstraints cons = new GridBagConstraints();
//		cons.fill = GridBagConstraints.BOTH;
//		cons.gridy = 0;
//		cons.gridwidth = 2;
//
//        // TODO: change this to remember.
//        if (p == null) {
//            String userHome = System.getProperty("user.home");
//            String defaultPath = jEdit.getProperty("vfs.browser.defaultPath");
//            if (defaultPath.equals("home")) {
//                p = userHome;
//            } else {
//                if (defaultPath.equals("working")) {
//                    p = System.getProperty("user.dir");
//                } else {
//                    if (defaultPath.equals("buffer")) {
//                        if (view != null) {
//                            Buffer buffer = view.getBuffer();
//                            p = buffer.getDirectory();
//                        } else {
//                            p = userHome;
//                        }
//                    } else {
//                        p = userHome;
//                    }
//                }
//            }
//        }
//
//        filterLabel = new JLabel("Filter:");
//
//        filterField = new HistoryTextField("FindFilePlugin.filter");
//        filterField.setColumns(20);
//        filterField.setText("*.*"); //TODO: make this remember things.
//        filterField.addActionListener(this);
//
//        directoryLabel = new JLabel("Directory:");
//
//        directoryField = new HistoryTextField("FindFilePlugin.path");
//        directoryField.setColumns(75);
//        directoryField.setText(p);
//        directoryField.addActionListener(this);
//
//
//
//        return criteriaPanel;
//
//    }
//
//    private JPanel createSettingsPanel() {
//
//        JPanel settingsPanel = new JPanel(new GridBagLayout());
//
//        return settingsPanel;
//
//    }
//
//    private JPanel createButtonsPanel() {
//
//        JPanel buttonsPanel = new JPanel(new GridBagLayout());
//
//        return buttonsPanel;
//
//    }

    //{{{ ok
    /**
     * Called when the dialog is confirmed.
     * Performs the search.
     */
    public void ok() {
        view.getDockableWindowManager().addDockableWindow("FindFilePlugin");
        FindFileResults results = (FindFileResults) view.getDockableWindowManager() .getDockable("FindFilePlugin");
        if (results == null) {
            setVisible(false);
            return;
        }
        results.searchStarted();
        SearchOptions options = new SearchOptions();
        options.path = directoryField.getText();
        options.filter = filterField.getText();
        options.recursive = recursive.isSelected();
        options.openResults = openAllResults.isSelected();
        FindFileRequest request = new FindFileRequest(view,results,options);
        VFSManager.runInWorkThread(request);
        GUIUtilities.saveGeometry(this,"FindFilePlugin");
        directoryField.addCurrentToHistory();
        filterField.addCurrentToHistory();
        setVisible(false);
    }
    ///}}}

    //{{{ cancel
    /**
     * Called when the dialog is canceled.
     */
    public void cancel() {
        GUIUtilities.saveGeometry(this,"FindFilePlugin");
        setVisible(false);
    }//}}}

    //{{{ actionPerformed
    /**
     * Action handler for dialog buttons.
     */
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(jEdit.getProperty("FindFilePlugin.search-dialog.buttons.find"))) {
            ok();
        } else {
            cancel();
        }
    }//}}}
}
