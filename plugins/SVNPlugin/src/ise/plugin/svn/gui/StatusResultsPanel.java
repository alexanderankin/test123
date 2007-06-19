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
import ise.plugin.svn.data.StatusData;
import org.tmatesoft.svn.core.wc.SVNStatus;

/**
 * Shows the results of a status check.
 */
public class StatusResultsPanel extends JPanel {

    public StatusResultsPanel( StatusData results ) {
        super( new LambdaLayout() );
        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );

        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.y = 0;
        con.s = "w";
        con.p = 3;

        JLabel label = new JLabel("Status checked against revision: " + results.getRevision());
        add(label, con);
        con.s = "wh";

        boolean added = false;

        List<SVNStatus> list = results.getConflicted();
        if (list != null) {
            ++con.y;
            add(createPanel("Files with conflicts (must merged):", list), con);
            added = true;
        }


        list = results.getOutOfDate();
        if (list != null) {
            ++con.y;
            add(createPanel("Out of date files (need updated?):", list), con);
            added = true;
        }

        list = results.getModified();
        if (list != null) {
            ++con.y;
            add(createPanel("Modified files (need committed?):", list), con);
            added = true;
        }

        list = results.getAdded();
        if (list != null) {
            ++con.y;
            add(createPanel("Added files (need committed?):", list), con);
            added = true;
        }

        list = results.getUnversioned();
        if (list != null) {
            ++con.y;
            add(createPanel("Unversioned files (need added?):", list), con);
            added = true;
        }

        list = results.getDeleted();
        if (list != null) {
            ++con.y;
            add(createPanel("Deleted files (need committed?):", list), con);
            added = true;
        }

        list = results.getMissing();
        if (list != null) {
            ++con.y;
            add(createPanel("Missing files (need deleted?):", list), con);
            added = true;
        }

        if (!added) {
            label.setText(label.getText() + " (All files up to date.)");
        }
    }

    private JPanel createPanel(String title, List<SVNStatus> values) {
        JLabel label = new JLabel(title);
        String[][] data = new String[values.size()][1];
        for (int i = 0; i < values.size(); i++) {
            data[i][0] = values.get(i).getFile().toString();
        }
        JTable table = new JTable(data, new String[]{"Path:"});
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EtchedBorder());
        panel.add(label, BorderLayout.NORTH);
        panel.add(GUIUtils.createTablePanel(table), BorderLayout.CENTER);
        return panel;
    }

}
