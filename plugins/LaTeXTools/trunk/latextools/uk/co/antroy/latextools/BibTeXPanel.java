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


public class BibTeXPanel
  extends AbstractToolPanel {

  //~ Instance/static variables ...............................................

  private ArrayList bibEntries = new ArrayList();
  private ArrayList bibFiles = new ArrayList();

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

//     if (bufferChanged) {
      removeAll();
//      bufferChanged = false;
//     }
 
    if (!isTeXFile(buffer)) {
      log("!isTexFile");
      displayNotTeX(BorderLayout.CENTER);
    } else {
      loadBibFiles();
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

    Object[] be = bibEntries.toArray();
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

  private void loadBibEntries() {

    RE refRe;
    RE titleRe;
		
    try {
      refRe = new RE("@\\w+?\\{([a-zA-Z0-9]+?),");
      titleRe = new RE("title\\s*=\\s*\\{(.+?)\\}");
    } catch (REException e) {

      return;
    }

    Iterator it = bibFiles.iterator();
    File bib;

    while (it.hasNext()) {
      bib = (File) it.next();

      //log(0);
      try {

        // log(1);
        // log("Bib: " + bib.toString());
        FileReader reader = new FileReader(bib);

        // log(2);
        BufferedReader in = new BufferedReader(reader);

        // log(3);
        String nextLine = in.readLine();

        // log("nextLine=" + nextLine);
        boolean newEntry = false;
        int count = 0;
        String bibref = "";

        while (nextLine != null) {

          if (newEntry) {

            REMatch rm = titleRe.getMatch(nextLine);

            if (rm != null) {
              newEntry = false;

              // log("entry: " + rm.toString(1));
              BibEntry bibEntry = new BibEntry(bibref.trim(), 
                                               rm.toString(1).trim());
              bibEntries.add(bibEntry);
            }
          } else {

            REMatch rm = refRe.getMatch(nextLine);

            if (rm != null) {
              count++;
              newEntry = true;
              bibref = rm.toString(1);

              int l = bibref.length();
            }
          }

          nextLine = in.readLine();
        }
        in.close();
      } catch (Exception e) {
      }
    }

    //log("size"+bibEntries.size());
    Collections.sort(bibEntries);
  }

  private void loadBibFiles() {
    bibEntries.clear();
    bibFiles.clear();
    
    File texFile = new File(tex);
    
    DefaultMutableTreeNode files = LaTeXMacros.getProjectFiles(view, buffer);
    
    filesLoop:
    for (Enumeration it = files.getLastLeaf().pathFromAncestorEnumeration(files); it.hasMoreElements();) {
       DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.nextElement();
       File in = (File) node.getUserObject();
       log(in.toString());
       Buffer buff = jEdit.openTemporary(view, in.getParent(), in.getName(),false);
       bufferLoop:
       for (int i = buff.getLineCount() - 1; i > 0; i--) {
   
         String nextLine = buff.getLineText(i);
   
         if (nextLine.indexOf("%Bibliography") != -1 || 
             nextLine.indexOf("\\begin{document}") != -1) {
   
           break bufferLoop;
         }
   
         RE bibRe = null;    
         RE theBibRe = null;
         
         try{
            bibRe    = new RE("[^%]*?\\\\bibliography\\{(.+?)\\}.*");
            theBibRe = new RE("[^%]*?\\\\begin\\{thebibliography\\}.*");
         } catch (Exception e){
            e.printStackTrace();
         }
         
         boolean index = bibRe.isMatch(nextLine);
         boolean tbindex = theBibRe.isMatch(nextLine);
         
         if (index) {
           StringTokenizer st = new StringTokenizer(bibRe.getMatch(nextLine).toString(1), ",");
   
           //Add referenced bibtex files to bibFiles list.
           while (st.hasMoreTokens()) {
   
             String s = st.nextToken().trim();
   
             if (!s.endsWith(".bib"))
               s = s + ".bib";
   
             File f = new File(LaTeXMacros.getMainTeXDir(buffer), s);
   
             log(f.toString());
             if (!f.exists())
               f = new File(s);
   
             bibFiles.add(f);
           }
   
           loadBibEntries();
   
           return;
         } else if (tbindex) {
           loadTheBibliography(i);
           return;
         }
       }
    }
  }

  private void loadTheBibliography(int startIndex) {

    int index = startIndex;
    int refStart;
    int end = buffer.getLineText(index).indexOf("\\end{thebibliography}");

    while (end == -1) {

      String line = buffer.getLineText(index);
      refStart = line.indexOf("\\bibitem{");

      if (refStart != -1) {

        int refEnd = line.indexOf("}");
        String bibRef = line.substring(refStart + 9, refEnd);
        BibEntry be = new BibEntry(bibRef.trim(), "");
        bibEntries.add(be);
      }

      end = buffer.getLineText(++index).indexOf("\\end{thebibliography}");
    }

    Collections.sort(bibEntries);
  }

  private void popup(String s) {
    JOptionPane.showMessageDialog(view, s);
  }

  private void popup(int s) {
    JOptionPane.showMessageDialog(view, "" + s);
  }

  private void popup() {
    popup("Green Eggs and Ham");
  }

  //~ Inner classes ...........................................................

  private class BibEntry
    implements Comparable {

    //~ Instance/static variables .............................................

    private String ref;
    private String title;

    //~ Constructors ..........................................................

    public BibEntry() {
      this("", "");
    }

    public BibEntry(String r, String t) {
      ref = r;
      setTitle(t);
    }

    //~ Methods ...............................................................

    public void setRef(String s) {
      this.ref = s;
    }

    public String getRef() {

      return ref;
    }

    public void setTitle(String s) {

      int bibLength = jEdit.getIntegerProperty("bibtex.bibtitle.wordlength", 0);
      int bibCount = jEdit.getIntegerProperty("bibtex.bibtitle.wordcount", 0);
      StringTokenizer st = new StringTokenizer(s, " ");
      StringBuffer sb = new StringBuffer("");
      int count = bibCount;
      int length = st.countTokens();

      if (count == 0) {
        count = length;
      }

      while (st.hasMoreTokens() && count > 0) {
        count--;

        String ss = st.nextToken();

        if (bibLength != 0 && ss.length() > bibLength)
          ss = ss.substring(0, bibLength) + ".";

        sb.append(" " + ss);
      }

      if (bibCount != 0 && length > bibCount) {
        sb.append("...");
      }

      this.title = sb.toString();
    }

    public String getTitle() {

      return title;
    }

    public int compareTo(BibEntry be) {

      return ref.compareTo(be.getRef());
    }

    public int compareTo(Object o) {

      return compareTo((BibEntry) o);
    }

    public String toString() {

      return ref + "  : " + title;
    }
  }
}
