package ise.plugin.svn.gui;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.EmptyBorder;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.data.UpdateData;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * Shows the results of an update.  Conflicted files, updated files,
 * added files, and deleted files are shown in separate tables.
 */
public class UpdateResultsPanel extends JPanel {

    public UpdateResultsPanel( UpdateData results ) {
        super( new LambdaLayout() );
        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );

        JLabel label = new JLabel("Updated to revision: " + results.getRevision());

        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.y = 0;
        con.s = "wh";
        con.p = 3;


        add(label, con);

        boolean added = false;

        List<String> list = results.getConflictedFiles();
        if (list != null) {
            ++con.y;
            add(createPanel("Files with conflicts:", list), con);
            added = true;
        }

        list = results.getUpdatedFiles();
        if (list != null) {
            ++con.y;
            add(createPanel("Updated files:", list), con);
            added = true;
        }

        list = results.getAddedFiles();
        if (list != null) {
            ++con.y;
            add(createPanel("Added files:", list), con);
            added = true;
        }

        list = results.getDeletedFiles();
        if (list != null) {
            ++con.y;
            add(createPanel("Deleted files:", list), con);
            added = true;
        }

        if (!added) {
            label.setText(label.getText() + " (Already up to date.)");
        }
    }

    private JPanel createPanel(String title, List<String> values) {
        JLabel label = new JLabel(title);
        String[][] data = new String[values.size()][1];
        for (int i = 0; i < values.size(); i++) {
            data[i][0] = values.get(i);
        }
        JTable table = new JTable(data, new String[]{"Path:"});
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EtchedBorder());
        panel.add(label, BorderLayout.NORTH);
        panel.add(GUIUtils.createTablePanel(table), BorderLayout.CENTER);
        return panel;
    }

}
