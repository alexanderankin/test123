package uk.co.antroy.latextools;

import console.Console;
import console.Shell;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.BorderLayout;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.tree.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class ProjectViewerPanel extends AbstractToolPanel implements MouseListener{
        private JTree tree;
        private View view;
        
        public ProjectViewerPanel(View view, Buffer buffer){
            super(view, buffer, "Project Files");
            this.view = view;
            refresh();
        }
        
        public void reload(){
           
        }
        
        public void refresh(){
            
          if (!isTeXFile(buffer)) {
              log("!isTexFile");
              displayNotTeX(BorderLayout.CENTER);
            } else {
                removeAll();
                tree = new JTree(LaTeXMacros.getProjectFiles(view, buffer));
                tree.setShowsRootHandles(true);
                tree.addMouseListener(this);
                tree.setToggleClickCount(3);
                JScrollPane scrollpane = new JScrollPane(tree,
                                     JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                     JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                setLayout(new BorderLayout());
                setPreferredSize(new Dimension(400,400));
                add(scrollpane, BorderLayout.CENTER);

            }
        }
        
        public void mouseClicked(MouseEvent e){} 
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mousePressed(MouseEvent e){
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (node == null) {return;}
            String fileName = node.getUserObject().toString();
            Buffer buff = jEdit.getBuffer(fileName);
            if (e.getClickCount() == 2){
                if (buff == null){
                    jEdit.openFile(view, fileName);
                }
            }else if (e.getClickCount() == 1){
                if (buff != null){
                    if ((e.getModifiers() & e.ALT_MASK) == e.ALT_MASK) {
                        jEdit.closeBuffer(view, buff);
                    }else{
                        view.setBuffer(buff);
                    }
                }  
            }
            }
        public void mouseReleased(MouseEvent e){}
        
    }

