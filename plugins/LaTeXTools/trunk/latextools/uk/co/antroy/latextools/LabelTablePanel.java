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


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;

import java.awt.Dimension;
import java.awt.event.MouseEvent;



import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.DisplayManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.*;

import javax.swing.table.TableModel;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import uk.co.antroy.latextools.parsers.LabelParser;
import uk.co.antroy.latextools.parsers.LabelTableModel;

import tableutils.TableSorter;

public class LabelTablePanel
  extends AbstractToolPanel {

  //~ Instance/static variables ...............................................

  private JTable table;
  private TableModel model;
  private ActionListener insert;
  private boolean enableInsert = true;
  private boolean suppress = false;


  //~ Constructors ............................................................

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


  public void refresh() {
      if (suppress){
          return;
      }      
    if (bufferChanged) {
        removeAll();
        bufferChanged = false;
    }

    if (!LaTeXMacros.isTeXFile(buffer)) {
      displayNotTeX(BorderLayout.CENTER);
    } else {
        buildPanel();
    }

  }

  private void buildPanel(){
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
 Log.log(Log.DEBUG, this, "Refreshing Cursor Position");
                    refreshCurrentCursorPosn();
                }

                if ((e.getModifiers() & e.ALT_MASK) == e.ALT_MASK) {
 Log.log(Log.DEBUG, this, "Suppress");
                   suppress = true;
                } else {
                    suppress = false;
 Log.log(Log.DEBUG, this, "UnSuppress");
                }

                visitLabel();
            }
        }
        public void mouseExited(MouseEvent e) {
            Log.log(Log.DEBUG, this, "EXITING");
           suppress = false;
        }
    });    
    String key = "Enter";
    
    table.getActionMap().put(key, new AbstractAction(){
        public void actionPerformed(ActionEvent e){
            insert();
        }
    });
    
    table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),key);
    
    sorter.addMouseListenerToHeaderInTable(table);
    JScrollPane scp = new JScrollPane(table, 
                                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(400, 400));
    add(scp, BorderLayout.CENTER);
  }
  
  public void reload(){
  }

  private void insert() {
    int sel = table.getSelectedRow();
    StringBuffer sb = new StringBuffer();

    LabelTableModel mod = (LabelTableModel) model;
    LaTeXAsset bi = mod.getRowEntry(sel);
    sb.append(bi.name);

    if (jEdit.getBooleanProperty("reference.inserttags")) {
      sb.insert(0, "\\ref{");
      sb.append("}");
    }
    
    view.setBuffer(currentBuffer);
    currentBuffer.insert(currentCursorPosn, sb.toString());
    view.getTextArea().setCaretPosition(
            currentCursorPosn + sb.length());
  }
  
    private void visitLabel() {
        int sel = table.getSelectedRow();
        LabelTableModel mod = (LabelTableModel) model;
        LaTeXAsset asset = mod.getRowEntry(sel);
        Buffer goToBuff = jEdit.openFile(view, asset.getFile().toString());
        view.setBuffer(goToBuff);
        
        Log.log(Log.DEBUG,this,"Ref Start: " + asset.start.getOffset());
        Log.log(Log.DEBUG,this,"Buffer Length: " + goToBuff.getLength());
        int line = goToBuff.getLineOfOffset(asset.start.getOffset());
        Log.log(Log.DEBUG,this,"***");
        
        JEditTextArea textArea = view.getTextArea();
        DisplayManager fvm = textArea.getDisplayManager();
        fvm.expandFold(line, false);
        textArea.setFirstPhysicalLine(line);
        int lineStart = view.getTextArea().getLineStartOffset(line);
        int lineEnd = view.getTextArea().getLineEndOffset(line);
        Selection.Range selectionRange = new Selection.Range(lineStart, lineEnd);
        view.getTextArea().setSelection(selectionRange);
    }

}
