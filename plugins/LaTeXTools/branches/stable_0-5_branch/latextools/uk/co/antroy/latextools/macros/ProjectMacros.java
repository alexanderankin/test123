//       \includegraphics*[width=7cm]{graphics\complexes.png}
//      :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools.macros;

import uk.co.antroy.latextools.*;

import console.Console;
import console.Shell;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.Component;
import java.awt.*;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
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

public class ProjectMacros {
    public static final String MAIN_TEX_FILE_KEY = "latex.root";
    public static final String IMPORT_REG_EX = "\\\\in(?:(?:put)|(?:clude))\\{(.*?)\\}";

    public static void setMainFile(Buffer buffer) {
        String currentFile = buffer.getPath();
        jEdit.setProperty(MAIN_TEX_FILE_KEY, currentFile);
    }

    public static void resetMainFile() {
        jEdit.setProperty(MAIN_TEX_FILE_KEY, "");
    }

    public static void showMainFile(View v) {
        Macros.message(v, getMainTeXPath(v.getBuffer()));
    }

    public static String getMainTeXPath(Buffer buffer) {
        boolean mainInFile = false;
        String path = "";
        StringBuffer regex = new StringBuffer(":");
        regex.append(MAIN_TEX_FILE_KEY);
        regex.append("=(?:'|\"){0,1}(.*?)(?:'|\"){0,1}:");
        REMatch[] match = TextMacros.findInDocument(buffer, regex.toString(), 0, 
                                         Math.min(buffer.getLineCount() - 1, 5));

        if (match.length > 0) {
            path = match[0].toString(1);
            mainInFile = true;
        }

        File texFile = new File(buffer.getPath());
        String tex;

        if (mainInFile) {
            File main = new File(path);

            if (main.exists()) {
                tex = main.getAbsolutePath();
            } else {
                tex = new File(texFile.getParentFile(), path).getAbsolutePath();
            }
        } else {
            tex = jEdit.getProperty(MAIN_TEX_FILE_KEY);
        }

        if (tex == null || tex.equals("") || !(new File(tex).exists())) {
            tex = buffer.getPath().toString();
        }

        return tex;
    }

    public static String getMainTeXDir(Buffer buffer) {

        return getMainTeXFile(buffer).getParent();
    }

    public static File getMainTeXFile(Buffer buffer) {

        return new File(getMainTeXPath(buffer));
    }



    public static void openImport(View view, Buffer buffer) {
        String tex = getMainTeXPath(buffer);

        if (!(tex.substring(tex.length() - 3, tex.length()).equals("tex"))) {
            Macros.error(view, tex + " is not a TeX file.");

            return;
        }

        int line = view.getTextArea().getCaretLine();
        File[] match = getImportsInRange(buffer, line, line + 1);

        if (match.length <= 0)

            return;

        jEdit.openFile(view, match[0].toString());
    }
    
    public static void openAllProjectFiles(View view, Buffer buffer){
        DefaultMutableTreeNode files = getProjectFiles(view, buffer);
        for (Enumeration en = files.depthFirstEnumeration(); en.hasMoreElements(); ){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            File f = (File) node.getUserObject();
            jEdit.openFile(view, f.getAbsolutePath());
        }
    }

    public static void renameLabel(View view, Buffer buffer){
        String oldLabel = Macros.input(view, "Enter Old Label Name:");
        String newLabel = Macros.input(view, "Enter New Label Name:");
        renameLabel(view, buffer, oldLabel, newLabel);
    }
    
    public static void renameLabel(View view, Buffer buffer, String oldName, String newName){

        RE oldLabel = null;
        StringBuffer find = new StringBuffer("((?:\\\\ref)|(?:\\\\label))(\\{)").append(oldName).append("(\\})");
        StringBuffer replace = new StringBuffer("$1$2").append(newName).append("$3");
        
        SearchAndReplace.save();
        SearchAndReplace.setAutoWrapAround(true);
        SearchAndReplace.setBeanShellReplace(false);
        SearchAndReplace.setIgnoreCase(false) ;
        SearchAndReplace.setRegexp(true);
        SearchAndReplace.setReplaceString(replace.toString());
        SearchAndReplace.setReverseSearch(false);
        SearchAndReplace.setSearchString(find.toString());

        DefaultMutableTreeNode files = getProjectFiles(view, buffer);

        for (Enumeration en = files.depthFirstEnumeration(); en.hasMoreElements(); ){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            File f = (File) node.getUserObject();
            Buffer buff = jEdit.openTemporary(view, f.getParent(), f.getName(), false);
            boolean found = SearchAndReplace.replace(view, buff, 0, buff.getLength()-1);
            if (found) jEdit.commitTemporary(buff);
        }
        
        SearchAndReplace.load();
    }
    
    
    public static DefaultMutableTreeNode getProjectFiles(View view, 
                                                         Buffer buffer) {
        File main = getMainTeXFile(buffer);

        return getNestedImports(view, main);
    }

    private static DefaultMutableTreeNode getNestedImports(View view, File in) {
        DefaultMutableTreeNode out = new LaTeXMutableTreeNode(in);
        Buffer b = jEdit.openTemporary(view, in.getParent(), in.getName(), 
                                       false);
        File[] children = getImports(b);

        outer:
        for (int i = 0; i < children.length; i++) {
            File f = children[i];

            //Macros.message(null,f.toString());
            if (!f.exists()){
                StringTokenizer str = new StringTokenizer(jEdit.getProperty("latex.classpath.dirs",";"));
                
                while (str.hasMoreTokens()){
                    File test = new File(str.nextToken(), f.getName());
                    if (test.exists()){
                        out.add(getNestedImports(view, test));
                        continue outer;
                    }
                }
                
                continue outer;
            }

            //Macros.message(null,"Ex: " + f.toString());
            out.add(getNestedImports(view, f));
        }

        return out;
    }

    private static File[] getImportsInRange(Buffer buffer, int startLine, int endLine) {
        REMatch[] matches = TextMacros.findInDocument(buffer, IMPORT_REG_EX, startLine, endLine);
        ArrayList filelist = new ArrayList();
        String root = getMainTeXDir(buffer);

        for (int i = 0; i < matches.length; i++) {
            int posn = matches[i].getStartIndex() + buffer.getLineStartOffset(startLine);
            int line = buffer.getLineOfOffset(posn);
            String preText = buffer.getLineText(line).substring(0,(posn - buffer.getLineStartOffset(line))+1);
            if (preText.indexOf("%") >= 0) continue;

            String file = matches[i].toString(1);
            
            if (file.indexOf(".") < 0) {
                file += ".tex";
            }

            filelist.add(new File(root, file));
        }

        Object[] outObjects =  filelist.toArray();
        File[] out = new File[outObjects.length];
        
        for (int i=0; i < outObjects.length; i++){
           out[i] = (File) outObjects[i];
        }
        
        return out;
    }

    private static File[] getImports(Buffer buffer) {

        return getImportsInRange(buffer, 0, buffer.getLineCount() - 1);
    }

    
    public static int getProjectWordCount(View view, Buffer buffer){
        DefaultMutableTreeNode files = getProjectFiles(view, buffer);
        int count = 0;
        for (Enumeration it = files.preorderEnumeration(); it.hasMoreElements();) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.nextElement();
            File in = (File) node.getUserObject();
            Buffer buff = jEdit.openTemporary(view, in.getParent(), 
                                              in.getName(), false);
            count += getBufferWordCount(buff);
        }
        
        return count;
    }
    
    public static int getBufferWordCount(Buffer buff){
        int count = 0;
        String text = buff.getText(0,buff.getLength());
        RE wordExp = null;
        try{
            wordExp = new RE("(\\b|\\\\)(?:(?:[a-zA-Z]{2,})|a|i)\\b");
        } catch (REException e){
            System.err.println( "Regular Expression Failed!! Check expression in " +
                                "uk.co.antroy.latextools.macros.ProjectMacros.getBufferWordCount()");
            return -1;
        }
        REMatch[] matches = wordExp.getAllMatches(text);
        
        for (int i=0; i<matches.length; i++){
            if (!matches[i].toString(1).equals("\\")){
                count++;
            }
        }
        
        return count;
    }
    
    public static boolean isTeXFile(Buffer b) {
      if (b == null) return false;
      
      Mode mode = b.getMode();
      if (mode == null) return false;
      
      String s = mode.getName();
      boolean out = s.equals("tex");
      
      return out;
    }
    
    public static boolean isBibFile(Buffer b){
      if (b == null) return false;
      
      Mode mode = b.getMode();
      if (mode == null) return false;
      
      String s = mode.getName();
      boolean out = s.equals("bibtex");
      
      return out;
    }
    
    public static void showInformation(final View view, final Buffer buffer){
        Thread t = new Thread(new Runnable(){
           public void run(){
                _showInformation(view, buffer);
           }
        });
        t.start();
    }
    
    private static void _showInformation(View view, Buffer buffer){
       LaTeXDockable dockable = LaTeXDockable.getInstance();
       dockable.setInfoPanel(
                new JLabel("<html><font color='#dd0000'>Getting information..."), 
                "Project Information:");
       ProjectViewerPanel proj = new ProjectViewerPanel(view, buffer);

       StringBuffer info = new StringBuffer("");
       String main = getMainTeXPath(buffer);
       
       info.append("<html>");
       info.append("<b>Main File:</b> <font  size='-1'>");
       info.append(main).append("</font>");
       JEditorPane editor = new JEditorPane("text/html", info.toString());
       
       JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                                         new JScrollPane(editor), 
                                         new JScrollPane(proj));
       JComponent wind = view.getDockableWindowManager().getDockable("latextools-navigation-dock");
       if (wind != null){
           Dimension size = wind.getSize();
           int w = (int) (size.getWidth() / 4) * 3;
           panel.setDividerLocation(w);
       }
       
       dockable.setInfoPanel(panel, "Project Information:");
    }
    
    
    private static class LaTeXMutableTreeNode extends DefaultMutableTreeNode{
        File file;
        public LaTeXMutableTreeNode(File f){
            super(f);
            file = f;
        }
        
        public String toString(){
            return file.getName();
        }
    }
}
