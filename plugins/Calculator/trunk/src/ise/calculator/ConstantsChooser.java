/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import java.util.prefs.*;
import java.io.*;

/**
 * Shows a dialog to use, edit, and add constants.
 */
public class ConstantsChooser {

    private static JTable table = null;
    private static FunctionTableModel model = null;
    private static ArrayList<String> col_names = null;
    private static ArrayList<String> commands = null;
    private static ArrayList<File> files = null;
    private static JDialog dialog = null;

    private ConstantsChooser() { }

    /**
     * @param menu check against this to see if the constants are on the menu,
     * add a Boolean to the data if they are.
     * @return an ArrayList of ArrayLists for the table data.
     */
    private static ArrayList<ArrayList> getData(JMenu menu) {
        ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
        commands = new ArrayList<String>();
        files = new ArrayList<File>();
        int item_cnt = menu.getItemCount();
        ArrayList menu_commands = new ArrayList();
        for (int i = 0; i < item_cnt; i++) {
            JMenuItem mi = menu.getItem(i);
            if (mi != null) {
                menu_commands.add(mi.getActionCommand());
            }
        }

        try {
            File calc_dir = new File(System.getProperty("calc.home"), ".calc");
            if (!calc_dir.exists()) {
                return null;
            }
            String[] constants = calc_dir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("calc") && name.endsWith(".calc");
                }
            }
           );
            Arrays.sort(constants);
            for (int i = 0; i < constants.length; i++) {
                try {
                    String constant = constants[i];
                    File f = new File(calc_dir, constant);
                    if (f.length() == 0) {
                        continue;
                    }
                    FunctionReader fr = new FunctionReader(f);
                    if (fr.isConstant()) {
                        String desc = fr.getDescription();
                        String[] split = desc.split("<br>");
                        String name = split[0].substring(split[0].indexOf(' '));
                        String value = split[1].substring(split[1].indexOf(' '));
                        String uncertainty = split[2].substring(split[2].indexOf(' '));
                        String units = "<html>" + split[3].substring(split[3].indexOf(' '));
                        ArrayList row = new ArrayList();
                        row.add(Boolean.valueOf(menu_commands.contains(fr.getCommand())));
                        row.add(name);
                        row.add(value);
                        row.add(uncertainty);
                        row.add(units);
                        rows.add(row);
                        commands.add(fr.getCommand());
                        files.add(f);
                    }
                }
                catch (Exception e) {       // NOPMD
                    // e.printStackTrace();
                }
            }
        }
        catch (Exception e) {       // NOPMD
           // e.printStackTrace();
        }
        return rows;
    }

    public static void showChooser(JComponent parent, final JMenu constants_menu, final ActionListener listener) {
        if (dialog != null) {
            model.setData(getData(constants_menu), col_names);
            dialog.setVisible(true);
            return;
        }

        // set up the dialog
        dialog = new JDialog(GUIUtils.getRootJFrame(parent), "Choose Constant", true);
        LambdaLayout layout = new LambdaLayout();
        JPanel contents = new JPanel(layout);

        final JButton use_btn = new JButton("Use now");
        use_btn.setToolTipText("Put the selected constant into the X register.");
        use_btn.setEnabled(false);
        use_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    return;
                }
                System.out.println(commands.get(row));
                listener.actionPerformed(new ActionEvent(ae.getSource(), 0, (String) commands.get(row)));
                dialog.setVisible(false);
            }
        });

        final JButton apply_btn = new JButton("Apply");
        apply_btn.setEnabled(false);
        apply_btn.setToolTipText("Add/remove selected constants to the 'Constants' menu.");
        apply_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int cnt = constants_menu.getMenuComponentCount();
                for (int i = cnt - 1; i >= 2; i--) {
                    constants_menu.remove(i);
                }

                for (int row = 0; row < table.getModel().getRowCount(); row++) {
                    boolean checked = ((Boolean) table.getValueAt(row, 0)).booleanValue();
                    if (checked) {
                        String name = (String) table.getValueAt(row, 1);
                        // String value = (String) table.getValueAt(row, 2);
                        // String uncertainty = (String) table.getValueAt(row, 3);
                        // String units = (String) table.getValueAt(row, 4);
                        JMenuItem mi = new JMenuItem(name);
                        mi.setActionCommand((String) commands.get(row));
                        mi.addActionListener(listener);
                        constants_menu.add(mi);
                        Preferences prefs = Calculator.PREFS.node("constants_menu");
                        prefs.put((String) commands.get(row) + ".calc", (String) commands.get(row) + ".calc");
                    }
                    else {
                        Preferences prefs = Calculator.PREFS.node("constants_menu");
                        prefs.remove((String) commands.get(row) + ".calc");
                    }
                }
            }
        }
       );

        JButton new_btn = new JButton("New");
        new_btn.setToolTipText("Create a new constant.");
        new_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    File calc_dir = new File(System.getProperty("calc.home"), ".calc");
                    File f = File.createTempFile("const", ".calc", calc_dir);
                    new ConstantsEditor(dialog, "New Constant", f);                    // modal dialog
                    model.setData(getData(constants_menu), col_names);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
       );

        final JButton edit_btn = new JButton("Edit");
        edit_btn.setToolTipText("Edit the selected constant.");
        edit_btn.setEnabled(false);
        edit_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    return;
                }
                File f = (File) files.get(row);
                new ConstantsEditor(dialog, "Edit Constant", f);                // modal dialog
                model.setData(getData(constants_menu), col_names);
                int item_cnt = constants_menu.getItemCount();
                for (int i = 0; i < item_cnt; i++) {
                    JMenuItem mi = constants_menu.getItem(i);
                    if (mi != null && mi.getActionCommand().equals(commands.get(row))) {
                        mi.setText(table.getValueAt(row, 1).toString());
                        break;
                    }
                }
            }
        }
       );

        final JButton delete_btn = new JButton("Delete");
        delete_btn.setToolTipText("Delete the selected constant. This cannot be undone!");
        delete_btn.setEnabled(false);
        delete_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    return;
                }
                String name = model.getValueAt(row, 1).toString();
                int rtn = JOptionPane.showConfirmDialog(dialog, "<html>Are you sure you want to delete the constant '" + name + "'?\nThere is no undo!", "Confirm Delete", JOptionPane.WARNING_MESSAGE);
                if (rtn == JOptionPane.YES_OPTION) {
                    File f = (File) files.get(row);
                    if (f.exists()) {
                        f.delete();
                    }
                    int item_cnt = constants_menu.getItemCount();
                    for (int i = 0; i < item_cnt; i++) {
                        JMenuItem mi = constants_menu.getItem(i);
                        if (mi != null && mi.getActionCommand().equals(commands.get(row))) {
                            constants_menu.remove(i);
                            Preferences prefs = Calculator.PREFS.node("constants_menu");
                            prefs.remove((String) commands.get(row) + ".calc");
                            break;
                        }
                    }
                    model.setData(getData(constants_menu), col_names);
                }
            }
        }
       );

        JButton cancel_btn = new JButton("Close");
        cancel_btn.setToolTipText("Close this dialog.");
        cancel_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
            }
        }
       );

        // set up for the table
        col_names = new ArrayList<String>();
        col_names.add("On Menu");
        col_names.add("Constant");
        col_names.add("Value");
        col_names.add("Uncertainty");
        col_names.add("Units");

        model = new FunctionTableModel();

        table = new JTable();
        model.setData(getData(constants_menu), col_names);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
                int index = table.getSelectedRow();
                use_btn.setEnabled(index > -1);
                apply_btn.setEnabled(index > -1);
                edit_btn.setEnabled(index > -1);
                delete_btn.setEnabled(index > -1);
            }
        } );
        table.setModel(model);

        // add the parts
        contents.add(new JLabel("Available Constants:"), "0, 0, 8, 1, W, w, 5");
        contents.add(new JScrollPane(table), "0, 1, 8, 4, 0, wh, 5");
        contents.add(use_btn, "1, 6, 1, 1, 0, wh, 5");
        contents.add(apply_btn, "2, 6, 1, 1, 0, wh, 5");
        contents.add(new_btn, "3, 6, 1, 1, 0, wh, 5");
        contents.add(edit_btn, "4, 6, 1, 1, 0, wh, 5");
        contents.add(delete_btn, "5, 6, 1, 1, 0, wh, 5");
        contents.add(cancel_btn, "6, 6, 1, 1, 0, wh, 5");
        layout.makeColumnsSameWidth(new int[] {1,2,3,4,5,6} );
        dialog.setContentPane(contents);
        dialog.pack();
        GUIUtils.center(GUIUtils.getRootJFrame(parent), dialog);
        dialog.setVisible(true);
    }

}
