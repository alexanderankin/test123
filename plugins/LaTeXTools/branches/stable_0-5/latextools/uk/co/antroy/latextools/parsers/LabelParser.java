/*:folding=indent::latex.root=Thesis.tex:
* LabelParser.java - Label Parser
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
import org.gjt.sp.util.*;

import uk.co.antroy.latextools.*;

public class LabelParser {

  //~ Instance/static variables ...............................................
    public static final String REFERENCE_EXP = "\\\\((?:label)|(?:chapter)|(?:(?:sub)?section))\\{(.*?)\\}";
    private ActionListener insert;
    private ArrayList refEntries = new ArrayList();
    private boolean suppress = false;
    private View view;
    private Buffer buffer;
    private String section = "";
    private String prefix = ""; 
    private int maxLength = 0;
  //~ Constructors ............................................................

  public LabelParser(View view, Buffer buff) {
      this.buffer = buff;
      this.view = view;
      parse();
  }


    //~ Constructors ............................................................



    //~ Methods .................................................................

    public Object[] getLabelArray(){
        return refEntries.toArray();
    }
        
    public List getLabelList(){
        return refEntries;
    }
    
     public void parse() {
        refEntries.clear();
        DefaultMutableTreeNode files = LaTeXMacros.getProjectFiles(view, buffer);

        for (Enumeration it = files.preorderEnumeration(); it.hasMoreElements();) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.nextElement();
            File in = (File) node.getUserObject();
            Buffer buff = jEdit.openTemporary(view, in.getParent(), 
                                              in.getName(), false);
            loadReferences(buff);
        }
    }

    private void loadReferences(Buffer buff) {
        String text = buff.getText(0, buff.getLength());
        RE labelRe = null;

        try {
            labelRe = new RE(REFERENCE_EXP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        REMatch[] matches = labelRe.getAllMatches(text);

        for (int i = 0; i < matches.length; i++) {
            int posn = matches[i].getStartIndex();
            int line = buff.getLineOfOffset(posn);
            String preText = buff.getLineText(line).substring(0,(posn - buff.getLineStartOffset(line))+1);
            
            if (preText.indexOf("%") >= 0) continue;

            String key = matches[i].toString(1);
            if (key.equals("label")){
                LaTeXAsset asset = LaTeXAsset.createAsset(matches[i].toString(2), 
                                                          buff.createPosition(matches[i].getStartIndex()), 
                                                          buff.createPosition(matches[i].getEndIndex()), 
                                                          0, 0);
                asset.setFile(new File(buff.getPath()));
                asset.setSection(getSection());
                refEntries.add(asset);
            } else{
                if (key.equals("chapter")){
                    prefix = "C ";
                } else if (key.equals("section")){
                     prefix = "§ ";
                } else if (key.equals("subsection")){
                    prefix = "¶ ";
                }
                section = matches[i].toString(2);
                maxLength = Math.max(maxLength, section.length());
            }
        }

    }

    public int getMaxLength(){
        return maxLength;
    }
    
    public String getSection(){
        StringBuffer out = new StringBuffer(prefix);
        // for (int i = 0; i < tabCount; i++) {
        //     out.append("_");
        // }
        out.append(section);
        return out.toString();
    }

}
