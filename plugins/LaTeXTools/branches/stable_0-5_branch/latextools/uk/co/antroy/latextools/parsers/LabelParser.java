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
import uk.co.antroy.latextools.macros.*;

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
    public static final String REFERENCE_EXP = "\\\\((?:label)|(?:ref)|(?:chapter)|(?:(?:sub)?section))\\{(.*?)\\}";
    public static final String LABEL = "label";
    public static final String REF = "ref";
    private ActionListener insert;
    private ArrayList refEntries = new ArrayList();
    //private Set labelSet = new HashSet();
    private Map labelMap = new HashMap();
    private List duplicates = new ArrayList();
    private boolean suppress = false;
    private View view;
    private Buffer buffer;
    private String section = "";
    private String prefix = ""; 
    private int maxLength = 0;
    private String parseType;
  //~ Constructors ............................................................

  public LabelParser(View view, Buffer buff) {
      this(view, buff, LABEL);
  }

  public LabelParser(View view, Buffer buff, String parseType) {
      this.buffer = buff;
      this.view = view;
      this.parseType = (parseType == REF) ? REF : LABEL;
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
    
    public Set getLabelNameSet(){
        return labelMap.keySet();//labelSet;
    }
    
    public List getDuplicateList(){
        return duplicates;
    }
    
     public void parse() {
        refEntries.clear();
        labelMap.clear();
        duplicates.clear();
        DefaultMutableTreeNode files = ProjectMacros.getProjectFiles(view, buffer);

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
            if (key.equals(parseType)){
                String name = matches[i].toString(2);
                LaTeXAsset asset = LaTeXAsset.createAsset(name, 
                                                          buff.createPosition(matches[i].getStartIndex()), 
                                                          buff.createPosition(matches[i].getEndIndex()), 
                                                          0, 0);
                File assetFile = new File(buff.getPath());
                asset.setFile(assetFile);
                asset.setSection(getSection());
                // if (!labelSet.add(name)){
                //     duplicates.add(asset);
                // }
                if (labelMap.containsKey(name)){
                    duplicates.add(asset);
                    duplicates.add(labelMap.get(name));
                }
                labelMap.put(name, asset);
                refEntries.add(asset);
            } else{
                if (key.equals("chapter")){
                    prefix = "C ";
                } else if (key.equals("section")){
                     prefix = "§ ";
                } else if (key.equals("subsection")){
                    prefix = "¶ ";
                } else {
                    continue;
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
