/*:folding=indent:
* LabelTablePanel.java - Label Dockable
* Copyright (C) 2002 Anthony Roy
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
package uk.co.antroy.latextools;

import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.awt.event.MouseEvent;

import java.net.*;

import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.help.HelpViewer;
import org.gjt.sp.util.Log;
import tableutils.TableSorter;

import uk.co.antroy.latextools.macros.ProjectMacros;
import uk.co.antroy.latextools.macros.*;
import uk.co.antroy.latextools.parsers.LabelParser;
import uk.co.antroy.latextools.parsers.LabelTableModel;
import uk.co.antroy.latextools.parsers.LaTeXAsset;


public class LabelTablePanel
    extends AbstractToolPanel {

    //~ Instance/static variables .............................................

    private JTable table;
    private TableModel model;
    private ActionListener insert;
    private boolean enableInsert = true;
    private boolean suppress = false;
    private Icon LOCKED_ICON = UtilityMacros.getIcon("locked.png");
    private Icon UNLOCKED_ICON = UtilityMacros.getIcon("unlocked.png");
    private JButton corner;
    

    //~ Constructors ..........................................................

    /**
     * Creates a new LabelTablePanel object.
     * 
     * @param view the current view
     * @param buff the active buffer
     */
    public LabelTablePanel(View view, Buffer buff) {
        super(view, buff, "Bib");
        buildPanel();
    }

    //~ Methods ...............................................................

    public void refresh() {

        if (suppress) {

            return;
        }

        if (bufferChanged) {
            removeAll();
            bufferChanged = false;
        }

        if (!ProjectMacros.isTeXFile(buffer)) {
            displayNotTeX(BorderLayout.CENTER);
        } else {
            buildPanel();
        }

        super.refresh();
    }

    public void reload() {
    }

    private void _buildPanel() {

        Action openHelp = new AbstractAction("",UNLOCKED_ICON){
            public void actionPerformed(ActionEvent e){
                try{
                    URL help = new URL(LabelTablePanel.class.getResource("/index.html"), "#label");
                    new HelpViewer(help);
                } catch(MalformedURLException ex){
                    ex.printStackTrace();
                    Log.log(Log.ERROR, this, "Url Malformed");
                }
            }
        };
        
        corner = new JButton(openHelp);
        corner.setToolTipText("Click here for help with the Label Navigator.");
        
        JLabel parsingLabel = new JLabel("<html><font color='#dd0000'>Parsing...");
        add(parsingLabel);

        LabelParser parser = new LabelParser(view, buffer);
        model = new LabelTableModel(parser.getLabelList());

        TableSorter sorter = new TableSorter(model);
        table = new JTable(sorter);
        table.getColumnModel().getColumn(0).setPreferredWidth(parser.getMaxLength());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    insert();
                } else if (e.getClickCount() == 1) {

                    if (!suppress) {
                        refreshCurrentCursorPosn();
                    }

                    if ((e.getModifiers() & e.ALT_MASK) == e.ALT_MASK) {
                        unSuppress();
                    } else {
                        suppress();
                    }

                    int sel = table.getSelectedRow();
                    LabelTableModel mod = (LabelTableModel)model;
                    LaTeXAsset asset = mod.getRowEntry(sel);
                    TextMacros.visitAsset(view, asset);
                }
            }
            public void mouseExited(MouseEvent e){
                unSuppress();
            }
        });
        
        String key = "Enter";
        table.getActionMap().put(key, 
                                 new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                insert();
            }
        });
        
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
                                key);
        sorter.addMouseListenerToHeaderInTable(table);

        final JScrollPane scp = new JScrollPane(table, 
                                          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        
                
        corner.setToolTipText("Click here for help on Label Navigation.");
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 400));
        remove(parsingLabel);
        add(scp, BorderLayout.CENTER);
        sendUpdateEvent("latextools-label-table-dock");
    }

    private void suppress(){
        suppress = true;
        corner.setIcon(LOCKED_ICON);
    }
    
    private void unSuppress(){
        suppress = false;
        corner.setIcon(UNLOCKED_ICON);
    }
    
    private void buildPanel() {

        Thread parseThread = new Thread(new Runnable() {
            public void run() {
                _buildPanel();
            }
        });
        parseThread.start();
    }

    private void insert() {

        int sel = table.getSelectedRow();
        StringBuffer sb = new StringBuffer();
        LabelTableModel mod = (LabelTableModel)model;
        LaTeXAsset bi = mod.getRowEntry(sel);
        sb.append(bi.name);

        if (jEdit.getBooleanProperty("reference.inserttags")) {
            sb.insert(0, "\\ref{");
            sb.append("}");
        }

        view.setBuffer(currentBuffer);
        currentBuffer.insert(currentCursorPosn, sb.toString());
        view.getTextArea().setCaretPosition(currentCursorPosn + sb.length());
    }
}
