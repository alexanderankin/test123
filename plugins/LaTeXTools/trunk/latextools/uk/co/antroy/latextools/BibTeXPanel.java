/*:folding=indent:
* BibTeXPanel.java - BibTeX Dialog
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

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.util.StringTokenizer;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.*;

import uk.co.antroy.latextools.parsers.*;

public class BibTeXPanel
  extends AbstractToolPanel {

  //~ Instance/static variables ...............................................

  //  private JTable bibTable;
  private JList bibList = new JList();
  private ActionListener insert;

  //~ Constructors ............................................................

  /**
   * Creates a new BibTeXPanel object.
   * 
   * @param view the current view
   * @param buff the active buffer
   */
  public BibTeXPanel(View view, Buffer buff) {
    super(view, buff, "Bib");
    refresh();
  }

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @param view ¤
   * @param buff ¤
   */
  public static void createBibTeXDialog(View view, Buffer buff) {

    final BibTeXPanel n = new BibTeXPanel(view, buff);
    EnhancedDialog ed = new EnhancedDialog(view, "Insert Citation", false) {
      public void cancel() {
        this.hide();
      }

      public void ok() {
        n.insert();
        this.hide();
      }
    };

    ed.setContentPane(n);
    ed.pack();
    ed.show();
  }

  /**
   * ¤
   */
  public void refresh() {

    removeAll();

    if (!LaTeXMacros.isTeXFile(buffer)) {
      displayNotTeX(BorderLayout.CENTER);
    } else {
      //loadBibFiles();
      buildList();

      JScrollPane scp = new JScrollPane(bibList, 
                                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(400, 400));
      add(scp, BorderLayout.CENTER);
//      add(createButtonPanel(REFRESH), BorderLayout.SOUTH);
    }

    repaint();
  }

  public void reload(){
  }
  
  private void buildList() {
    BibTeXParser parser = new BibTeXParser(view, buffer);
    Object[] be = parser.getBibEntryArray();
    bibList = null;
    bibList = new JList(be);
    bibList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() == 2) {
          insert();
        }
      }
    });
    
    bibList.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == e.VK_ENTER) {
          insert();
        }
      }
    });

  }

  private void insert() {

    Object[] sels = bibList.getSelectedValues();

    if (sels.length == 0) {
      System.err.println("Code should have returned by now!");

      return;
    }

    BibEntry bi = (BibEntry) sels[0];
    StringBuffer sb = new StringBuffer(bi.getRef());

    for (int i = 1; i < sels.length; i++) {
      bi = (BibEntry) sels[i];
      sb.append(",");
      sb.append(bi.getRef());
    }

    if (jEdit.getBooleanProperty("bibtex.inserttags")) {
      sb.insert(0, "\\cite{");
      sb.append("}");
    }

    int posn = view.getTextArea().getCaretPosition();
    buffer.insert(posn, sb.toString());
  }

}
