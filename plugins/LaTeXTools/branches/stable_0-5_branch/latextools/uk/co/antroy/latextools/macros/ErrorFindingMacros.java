//       \includegraphics*[width=7cm]{graphics\complexes.png}
//      :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools.macros;

import uk.co.antroy.latextools.parsers.*;
import uk.co.antroy.latextools.*;

import console.Console;
import console.Shell;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.io.FilenameFilter;

import java.util.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
                   
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.search.SearchAndReplace;

public class ErrorFindingMacros {

    public static void displayDuplicateLabels(final View view, Buffer buff){
        LabelParser parser = new LabelParser(view, buff);
        List duplicates = parser.getDuplicateList();
        JComponent out = null;
        
        if (duplicates.size() == 0) {
            out = new JLabel("Success! No Duplicates Found!");
        }else {
            final JList list = new JList(duplicates.toArray());
            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                        LaTeXAsset asset = (LaTeXAsset)list.getSelectedValue();
                        TextMacros.visitAsset(view, asset);
                    }
            });
            out = new JScrollPane(list, 
                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }

        LaTeXDockable.getInstance().setInfoPanel(out, "Duplicate Labels:");
        
    }
    
    public static void displayOrphanedRefs(final View view, Buffer buff){
        LabelParser labParser = new LabelParser(view, buff);
        Set labels = labParser.getLabelNameSet();
        LabelParser refParser = new LabelParser(view, buff, LabelParser.REF);
        Set refs = new HashSet(refParser.getLabelList());
        List orphans = new ArrayList();
        
        for (Iterator it = refs.iterator(); it.hasNext(); ){
            LaTeXAsset asset = (LaTeXAsset) it.next();
            String name = asset.getShortString();
            if(!labels.contains(name)){
                orphans.add(asset);
            }
        }
        
        JComponent out = null;
        
        if (orphans.size() == 0) {
            out = new JLabel("Success! No Duplicates Found!");
        }else {
            final JList list = new JList(orphans.toArray());
            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                        LaTeXAsset asset = (LaTeXAsset)list.getSelectedValue();
                        TextMacros.visitAsset(view, asset);
                    }
            });
            out = new JScrollPane(list, 
                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }
        
    LaTeXDockable.getInstance().setInfoPanel(out, "Orphaned References:");
    
    }
}
