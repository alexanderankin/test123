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
 * Shows a dialog to use, edit, and add function.
 */
public class FunctionChooser {

    private static JTable table = null;
    private static FunctionTableModel model = null;
    private static ArrayList<String> col_names = null;
    private static ArrayList<String> commands = null;
    private static ArrayList<File> files = null;
    private static JDialog dialog = null;

    private FunctionChooser() { }

    /**
     * @param menu check against this to see if the function are on the menu,
     * add a Boolean to the data if they are.
     * @return a Vector of Vectors for the table data.
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
            String[] functions = calc_dir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("calc") && name.endsWith(".calc");
                }
            }
           );
            Arrays.sort(functions);
            for (int i = 0; i < functions.length; i++) {
                try {
                    String function = functions[ i];
                    File f = new File(calc_dir, function);
                    if (f.length() == 0) {
                        continue;
                    }
                    FunctionReader fr = new FunctionReader(f);
                    if (!fr.isConstant()) {
                        ArrayList row = new ArrayList();
                        row.add(Boolean.valueOf(menu_commands.contains(fr.getCommand())));
                        row.add(fr.getName());
                        row.add(fr.getDescription());
                        rows.add(row);
                        commands.add(fr.getCommand());
                        files.add(f);
                    }
                }
                catch (Exception e) {   // NOPMD
                    // e.printStackTrace();
                }
            }
        }
        catch (Exception e) {       // NOPMD
            // e.printStackTrace();
        }
        return rows;
    }

    public static void showChooser(JComponent parent, final JMenu function_menu, final ActionListener listener) {
        if (dialog != null) {
            model.setData(getData(function_menu), col_names);
            dialog.setVisible(true);
            return ;
        }

        // set up the dialog
        dialog = new JDialog(GUIUtils.getRootJFrame(parent), "Choose Function", true);
        LambdaLayout layout = new LambdaLayout();
        JPanel contents = new JPanel(layout);

        final JButton use_btn = new JButton("Use now");
        use_btn.setToolTipText("Put the selected function into the X register.");
        use_btn.setEnabled(false);
        use_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    return ;
                }
                listener.actionPerformed(new ActionEvent(ae.getSource(), 0, (String) commands.get(row)));
                dialog.setVisible(false);
            }
        });

        final JButton apply_btn = new JButton("Apply");
        apply_btn.setEnabled(false);
        apply_btn.setToolTipText("Add/remove selected function to the 'Functions' menu.");
        apply_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // first remove all functions from the function menu. The
                // function menu has two JSeparators, one just before the first
                // function, the second just after the last function.
                Component[] items = function_menu.getMenuComponents();
                int first_item_index = 0;
                int last_item_index = items.length - 1;
                for (int i = 0; i < items.length; i++) {
                    if (items[ i] instanceof JSeparator) {
                        first_item_index = i + 1;
                        break;
                    }
                }
                for (int i = first_item_index; i < items.length; i++) {
                    if (items[ i] instanceof JSeparator) {
                        last_item_index = i - 1;
                        break;
                    }
                }
                for (int i = last_item_index; i >= first_item_index; i--) {
                    function_menu.remove(i);
                }
                // then add the checked functions to the function menu
                int index = first_item_index;
                for (int row = 0; row < table.getModel().getRowCount(); row++) {
                    boolean checked = ((Boolean) table.getValueAt(row, 0)).booleanValue();
                    if (checked) {
                        String name = (String) table.getValueAt(row, 1);
                        //String value = (String) table.getValueAt(row, 2);
                        JMenuItem mi = new JMenuItem(name);
                        mi.setActionCommand((String) commands.get(row));
                        mi.addActionListener(listener);
                        function_menu.insert(mi, index);
                        ++index;
                        Preferences prefs = Calculator.PREFS.node("function_menu");
                        prefs.put((String) commands.get(row) + ".calc", (String) commands.get(row) + ".calc");
                    }
                    else {
                        Preferences prefs = Calculator.PREFS.node("function_menu");
                        prefs.remove((String) commands.get(row) + ".calc");
                    }
                }
            }
        });

        JButton new_btn = new JButton("New");
        new_btn.setToolTipText("Create a new function.");
        new_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    File calc_dir = new File(System.getProperty("calc.home"), ".calc");
                    File f = File.createTempFile("calc", ".calc", calc_dir);
                    new FunctionEditor(dialog, "New Function", f);                    // modal dialog
                    model.setData(getData(function_menu), col_names);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final JButton edit_btn = new JButton("Edit");
        edit_btn.setToolTipText("Edit the selected function.");
        edit_btn.setEnabled(false);
        edit_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    return ;
                }
                File f = (File) files.get(row);
                new FunctionEditor(dialog, "Edit Function", f);                // modal dialog
                model.setData(getData(function_menu), col_names);
                int item_cnt = function_menu.getItemCount();
                for (int i = 0; i < item_cnt; i++) {
                    JMenuItem mi = function_menu.getItem(i);
                    if (mi != null && mi.getActionCommand().equals(commands.get(row))) {
                        mi.setText(table.getValueAt(row, 1).toString());
                        break;
                    }
                }
            }
        });

        final JButton delete_btn = new JButton("Delete");
        delete_btn.setToolTipText("Delete the selected function. This cannot be undone!");
        delete_btn.setEnabled(false);
        delete_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    return ;
                }
                String name = model.getValueAt(row, 1).toString();
                int rtn = JOptionPane.showConfirmDialog(dialog, "<html>Are you sure you want to delete the function '" + name + "'?\nThere is no undo!", "Confirm Delete", JOptionPane.WARNING_MESSAGE);
                if (rtn == JOptionPane.YES_OPTION) {
                    File f = (File) files.get(row);
                    if (f.exists()) {
                        f.delete();
                    }
                    int item_cnt = function_menu.getItemCount();
                    for (int i = 0; i < item_cnt; i++) {
                        JMenuItem mi = function_menu.getItem(i);
                        if (mi != null && mi.getActionCommand().equals(commands.get(row))) {
                            function_menu.remove(i);
                            Preferences prefs = Calculator.PREFS.node("function_menu");
                            prefs.remove((String) commands.get(row) + ".calc");
                            break;
                        }
                    }
                    model.setData(getData(function_menu), col_names);
                }
            }
        });

        JButton cancel_btn = new JButton("Close");
        cancel_btn.setToolTipText("Close this dialog.");
        cancel_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
            }
        });

        // set up for the table
        col_names = new ArrayList<String>();
        col_names.add("On Menu");
        col_names.add("Function");
        col_names.add("Description");

        model = new FunctionTableModel();

        table = new JTable();
        model.setData(getData(function_menu), col_names);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
                int index = table.getSelectedRow();
                use_btn.setEnabled(index > -1);
                apply_btn.setEnabled(index > -1);
                edit_btn.setEnabled(index > -1);
                delete_btn.setEnabled(index > -1);
            }
        });
        table.setModel(model);

        // add the parts
        contents.add(new JLabel("Available Functions:"), "0, 0, 8, 1, W, w, 5");
        contents.add(new JScrollPane(table), "0, 1, 8, 4, 0, wh, 5");
        contents.add(use_btn, "1, 5, 1, 1, 0, wh, 5");
        contents.add(apply_btn, "2, 5, 1, 1, 0, wh, 5");
        contents.add(new_btn, "3, 5, 1, 1, 0, wh, 5");
        contents.add(edit_btn, "4, 5, 1, 1, 0, wh, 5");
        contents.add(delete_btn, "5, 5, 1, 1, 0, wh, 5");
        contents.add(cancel_btn, "6, 5, 1, 1, 0, wh, 5");
        layout.makeColumnsSameWidth(new int[] {1,2,3,4,5,6} );
        dialog.setContentPane(contents);
        dialog.pack();
        GUIUtils.center(parent, dialog);
        dialog.setVisible(true);
    }
}
