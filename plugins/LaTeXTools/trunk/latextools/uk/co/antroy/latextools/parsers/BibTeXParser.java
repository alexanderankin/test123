/*:folding=indent:
* BibTeXParser.java - BibTeX Parser
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
package uk.co.antroy.latextools.parsers; 

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

import uk.co.antroy.latextools.*;

public class BibTeXParser {

  //~ Instance/static variables ...............................................

  private List bibEntries = new ArrayList();
  private List bibFiles = new ArrayList();
  private Buffer buffer;
  private View view;
  
  //~ Constructors ............................................................

  public BibTeXParser(View view, Buffer buff) {
      this.buffer = buff;
      this.view = view;
      loadBibFiles();
  }

  //~ Methods .................................................................

  public List getBibEntries(){
      return bibEntries;
  }
  
  public Object[] getBibEntryArray(){
      return bibEntries.toArray();
  }
  
  
  private void loadBibEntries() {

    RE refRe;
    RE titleRe;
    RE authorRe;
		
    try {
      refRe = new RE("@\\w+?\\{([a-zA-Z0-9]+?),");
      titleRe = new RE("title\\s*=\\s*\\{(.+?)\\}");
      authorRe =  new RE("author\\s*=\\s*\\{(.*?)\\}\\s*(?:,|\\})");
    } catch (REException e) {
        e.printStackTrace();
        return;
    }

    Iterator it = bibFiles.iterator();
    File bib;

    while (it.hasNext()) {
      bib = (File) it.next();

      try {

        FileReader reader = new FileReader(bib);
        BufferedReader in = new BufferedReader(reader);
        String nextLine = in.readLine();
        boolean newEntry = false;
        int count = 0;
        String bibref = "";

        while (nextLine != null) {

          if (newEntry) {

            REMatch rm = titleRe.getMatch(nextLine);
            REMatch authorMatch = authorRe.getMatch(nextLine);

            if (rm != null) {
              newEntry = false;

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

    Collections.sort(bibEntries);
  }

  private void loadBibFiles() {
    bibEntries.clear();
    bibFiles.clear();
    
    File texFile = new File(buffer.getPath());
    
    DefaultMutableTreeNode files = LaTeXMacros.getProjectFiles(view, buffer);
    
    filesLoop:
    for (Enumeration it = files.getLastLeaf().pathFromAncestorEnumeration(files); it.hasMoreElements();) {
       DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.nextElement();
       File in = (File) node.getUserObject();
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

  //~ Inner classes ...........................................................


}
