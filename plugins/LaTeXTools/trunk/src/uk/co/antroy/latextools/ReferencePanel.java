/*:folding=indent:
 * ReferencePanel.java - Reference Panel
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
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


//import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.Selection;


public class ReferencePanel
  extends DefaultToolPanel {

  //~ Instance/static variables ...............................................

  private ActionListener insert;
  private ArrayList refEntries = new ArrayList();
  private JList refList;

  //~ Constructors ............................................................

  /**
   * Creates a new ReferencePanel object.
   * 
   * @param view the current view
   * @param buff the active buffer
   */
  public ReferencePanel(View view, Buffer buff) {
    super(view, buff, "Ref");
    refresh();
  }

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @param view ¤
   * @param buff ¤
   */
  public static void createReferenceDialog(View view, Buffer buff) {

    final ReferencePanel n = new ReferencePanel(view, buff);
    EnhancedDialog ed = new EnhancedDialog(view, "Insert Cross Reference", 
                                           false) {
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

    if (bufferChanged) {
      removeAll();
      bufferChanged = false;
    }

    if (!isTeXFile(buffer)) {
      displayNotTeX(BorderLayout.CENTER);
    } else {
      loadReferences();
      refList = new JList(refEntries.toArray());
      refList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      refList.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {

          if (e.getClickCount() == 2) {
            insert();
          } else if (e.getClickCount() == 1) {
            refreshCurrentCursorPosn();
            visitLabel();
          }
        }
      });

      JScrollPane scp = new JScrollPane(refList, 
                                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(400, 100));
      add(scp, BorderLayout.CENTER);
      add(createButtonPanel(REFRESH), BorderLayout.SOUTH);
    }
  }

  private void insert() {

    TagPair refTagPair = (TagPair) refList.getSelectedValue();
    String ref = refTagPair.tag;

    if (ref != null) {

      if (jEdit.getBooleanProperty("reference.inserttags")) {
        ref = "\\ref{" + ref + "}";
      }

      buffer.insert(currentCursorPosn, ref);
      view.getTextArea().setCaretPosition(currentCursorPosn + ref.length());
    }
  }

  private void loadReferences() {
    refEntries.clear();

    int index = buffer.getLineCount() - 1;

    //    boolean last = false;
    //TODO: Optimize the algorithm below. In particular find the last entry first
    // and then begin the main loop from that point.
    while (index > 0) {

      String line = buffer.getLineText(index);
      int refStart = line.indexOf("\\label{");

      //      if (!last && refStart>=0) {
      //         last = true;
      //         String lastLine = index+"";
      //         maxLineLen = lastLine.length();
      //      }
      //
      while (refStart >= 0) {

        int refEnd = line.indexOf("}", refStart);
        String ref = line.substring(refStart + 7, refEnd);
        TagPair tp = new TagPair(ref.trim(), index); //,maxLineLen);
        refEntries.add(tp);
        refStart = line.indexOf("\\label{", refEnd);
      }

      index--;
    }

    Collections.sort(refEntries);
  }

  private void visitLabel() {

    TagPair refTagPair = (TagPair) refList.getSelectedValue();
    int line = refTagPair.line;
    view.getTextArea().setFirstLine(line);

    int lineStart = view.getTextArea().getLineStartOffset(line);
    int lineEnd = view.getTextArea().getLineEndOffset(line);
    Selection.Range sel = new Selection.Range(lineStart, lineEnd);
    view.getTextArea().setSelection(sel);
  }
}

class TagPair
  implements Comparable {

  //~ Instance/static variables ...............................................

  int level;
  int line;
  String tag;

  //~ Constructors ............................................................

  TagPair(String tag, int line, int level) {
    this.tag = tag;
    this.line = line;
    this.level = level;
  }

  TagPair(String tag, int line) {
    this(tag, line, 0);
  }

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @return ¤
   */
  public int getLevel() {

    return level;
  }

  /**
   * ¤
   * 
   * @return ¤
   */
  public int getLine() {

    return line;
  }

  /**
   * ¤
   * 
   * @return ¤
   */
  public String getTag() {

    return tag;
  }

  /**
   * ¤
   * 
   * @param be ¤
   * @return ¤
   */
  public int compareTo(TagPair be) {

    if (be.getLine() == line) {

      String comp = line + tag;

      return comp.compareTo(be.getLine() + be.getTag());
    } else

      return line - be.getLine();
  }

  /**
   * ¤
   * 
   * @param o
   * @return ¤
   */
  public int compareTo(Object o) {

    return compareTo((TagPair) o);
  }

  /**
   * ¤
   * 
   * @return ¤
   */
  public String toString() {

    boolean NUMBERS_OFF = jEdit.getBooleanProperty("tagpair.linenumbers");

    if (line < 0 || NUMBERS_OFF)

      return tag;

    return line + "  : " + tag;
  }
}
