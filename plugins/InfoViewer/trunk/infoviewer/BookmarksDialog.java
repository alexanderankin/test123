/*
 * BookmarksDialog.java - "Edit Bookmarks" dialog in InfoViewer
 * Copyright (C) 1999 Dirk Moebius
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package infoviewer;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.EnhancedDialog;


public class BookmarksDialog extends EnhancedDialog {

    private JTable table;
    private JButton bOk, bCancel, bAdd, bDelete, bMoveUp, bMoveDown;
    private Bookmarks model;
    private InfoViewer viewer;
    
    public BookmarksDialog(InfoViewer viewer) {
        super(viewer, jEdit.getProperty("infoviewer.bdialog.title"), true);

        this.viewer = viewer;
        model = new Bookmarks();        
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scroller = new JScrollPane(table);
        
        bOk       = new JButton(jEdit.getProperty("infoviewer.bdialog.ok"));
        bCancel   = new JButton(jEdit.getProperty("infoviewer.bdialog.cancel"));
        bAdd      = new JButton(jEdit.getProperty("infoviewer.bdialog.add"));
        bDelete   = new JButton(jEdit.getProperty("infoviewer.bdialog.delete"));
        bMoveUp   = new JButton(jEdit.getProperty("infoviewer.bdialog.moveup"));
        bMoveDown = new JButton(jEdit.getProperty("infoviewer.bdialog.movedown"));

        ActionHandler ah = new ActionHandler();
        bOk.addActionListener(ah);
        bCancel.addActionListener(ah);
        bAdd.addActionListener(ah);
        bDelete.addActionListener(ah);
        bMoveUp.addActionListener(ah);
        bMoveDown.addActionListener(ah);
        
        Box buttons1 = Box.createHorizontalBox();
        buttons1.add(Box.createHorizontalGlue());
        buttons1.add(bOk);
        buttons1.add(Box.createRigidArea(new Dimension(20,20)));
        buttons1.add(bCancel);
        buttons1.add(Box.createHorizontalGlue());
        
        Box buttons2 = Box.createVerticalBox();
        buttons2.add(bAdd);
        buttons2.add(bDelete);        
        buttons2.add(Box.createRigidArea(new Dimension(20,20)));
        buttons2.add(bMoveUp);
        buttons2.add(bMoveDown); 
        buttons2.add(Box.createVerticalGlue());

        getContentPane().setLayout(new BorderLayout(5,5));
        getContentPane().add(scroller, BorderLayout.CENTER);
        getContentPane().add(buttons1, BorderLayout.SOUTH);
        getContentPane().add(buttons2, BorderLayout.EAST);
        getRootPane().setDefaultButton(bOk);
        getRootPane().setBorder(BorderFactory.createMatteBorder(
            10,10,10,10,new Color(205,205,205)));
        setSize(500,300);
        GUIUtilities.loadGeometry(this, "infoviewer.bdialog");
        setLocationRelativeTo(viewer);
        setVisible(true);
    }
    
    
    public void ok() {
        model.save();
        GUIUtilities.saveGeometry(this, "infoviewer.bdialog");
        setVisible(false);
        viewer.updateBookmarksMenu();
    }
    
    
    public void cancel() { 
        GUIUtilities.saveGeometry(this, "infoviewer.bdialog");
        setVisible(false);
    }

    
    /************************************************************************/
    
    
    private class ActionHandler implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton) evt.getSource();
            if (button == bAdd) {
                model.add("", "");
            }
            else if (button == bDelete) {
                int rows[] = table.getSelectedRows();
                if (rows.length == 0) {
                    GUIUtilities.error(null, 
                        "infoviewer.error.bdialog.noselection", null);
                } else {
                    for (int i = rows.length - 1; i >= 0; i--) {
                        model.delete(rows[i]);
                    }
                }
            }
            else if (button == bMoveUp) {
                int rows[] = table.getSelectedRows();
                if (rows.length == 0) {
                    GUIUtilities.error(null, 
                        "infoviewer.error.bdialog.noselection", null);
                } else if (rows.length > 1) {
                    GUIUtilities.error(null, 
                        "infoviewer.error.bdialog.selecttoomuch", null);
                } else if (rows[0] > 0) {
                    model.moveup(rows[0]);
                    table.setRowSelectionInterval(rows[0]-1, rows[0]-1);
                }
            }
            else if (button == bMoveDown) {
                int rows[] = table.getSelectedRows();
                if (rows.length == 0) {
                    GUIUtilities.error(null, 
                        "infoviewer.error.bdialog.noselection", null);
                } else if (rows.length > 1) {
                    GUIUtilities.error(null, 
                        "infoviewer.error.bdialog.selecttoomuch", null);
                } else if (rows[0] < model.getRowCount()-1) {
                    model.movedown(rows[0]);
                    table.setRowSelectionInterval(rows[0]+1, rows[0]+1);
                }
            }
            else if (button == bOk) {
                ok();
            }
            else if (button == bCancel) {
                cancel();
            }
        }
    } // inner class ActionHandler
}

