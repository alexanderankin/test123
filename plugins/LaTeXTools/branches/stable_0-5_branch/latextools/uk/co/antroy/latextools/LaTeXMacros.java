//       \includegraphics*[width=7cm]{graphics\complexes.png}
//      :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools;

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

import javax.swing.JPanel;
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

public class LaTeXMacros {
    public static final String MAIN_TEX_FILE_KEY = "latex.root";
    public static final String IMPORT_REG_EX = "\\\\in(?:(?:put)|(?:clude))\\{(.*?)\\}";

    public static void repeat(String expression, int start, int no, View view) {
        StringBuffer sb = new StringBuffer("");

        for (int i = start; i < (no + start); i++) {
            String replace = "" + i;
            String exp = "";

            try {
                RE regEx = new RE("\\#");
                exp = regEx.substituteAll(expression, replace);
            } catch (REException e) {
            }

            //String exp = expression.replaceAll("#",replace);
            sb.append(exp).append("\n");
        }

        view.getTextArea().setSelectedText(sb.toString());
    }

    public static void repeat(View view, boolean startDialog) {
        String expression = Macros.input(view, 
                                         "Enter expression (# where numbers should go)");

        if (expression == null)

            return;

        String noString = Macros.input(view, "Enter number of iterations");

        if (noString == null)

            return;

        int no = Integer.parseInt(noString);
        int start;

        if (startDialog) {
            String startString = Macros.input(view, "Enter start number");

            if (startString == null)

                return;

            start = Integer.parseInt(startString);
        } else {
            start = 1;
        }

        repeat(expression, start, no, view);
    }

    public static void surround(View view, String prefix, String suffix) {
        JEditTextArea textArea = view.getTextArea();
        int caret = textArea.getCaretPosition();

        //      prefix = Macros.input(view, "Enter prefix");
        //      suffix = Macros.input(view, "Enter suffix");
        if (prefix == null || prefix.length() == 0)

            return;

        if (suffix == null || suffix.length() == 0)
            suffix = prefix;

        String text = textArea.getSelectedText();

        if (text == null)
            text = "";

        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(text);
        sb.append(suffix);
        textArea.setSelectedText(sb.toString());

        //if no selected text, put the caret between the tags
        if (text.length() == 0)
            textArea.setCaretPosition(caret + prefix.length());
    }

    public static void surround(View view) {
        String prefix = Macros.input(view, "Enter prefix");

        if (prefix == null)

            return;

        String suffix = Macros.input(view, "Enter suffix");

        if (suffix == null)

            return;

        surround(view, prefix, suffix);
    }

    public static void newCommand(View view) {
        String command = Macros.input(view, "Enter command");

        if (command == null)

            return;

        surround(view, "\\" + command + "{", "}");
    }

    public static void newEnvironment(View view) {
        String env = Macros.input(view, "Enter environment name");

        if (env == null)

            return;

        surround(view, "\\begin{" + env + "}\n", "\n\\end{" + env + "}");
    }

    // ***** Macros to work with a main file **********
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
        //Log.log(Log.DEBUG, LaTeXMacros.class, "Buffer Length: " + buffer.getLineCount());
        REMatch[] match = findInDocument(buffer, regex.toString(), 0, 
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

    private static REMatch[] findInDocument(Buffer buf, String regex) {

        return findInDocument(buf, regex, 0, buf.getLineCount() - 1);
    }

    private static REMatch[] findInDocument(Buffer buf, String regex, 
                                            int startLine, int endLine) {
        int start = buf.getLineStartOffset(startLine);
        int end = buf.getLineStartOffset(endLine);
        String text = buf.getText(start, end - start);
        RE exp = null;

        try {
            exp = new RE(regex, RE.REG_ICASE | RE.REG_MULTILINE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        REMatch[] matches = exp.getAllMatches(text);

        return matches;
    }

    private static void historyCommand(View view, Buffer buffer, boolean prompt, 
                                       String commandProps, 
                                       String historyProps, String dialogTitle){
                                          
        String tex = getMainTeXPath(buffer);

        if (!(tex.substring(tex.length() - 3, tex.length()).equals("tex"))) {
            Macros.error(view, tex + " is not a TeX file.");
            return;
        }

        tex = tex.substring(0, tex.length()-4);
        
        String command;
        String ext;

        if (prompt) {
            CommandHistoryDialog hd = new CommandHistoryDialog(view, historyProps, dialogTitle);
            hd.setVisible(true);
            command = hd.getCommand();
            ext = hd.getExtension();

            if (command == null) {
                Macros.message(view, "Aborting...");

                return;
            }
        } else {
            command = jEdit.getProperty(commandProps + ".command");
            ext = jEdit.getProperty(commandProps + ".ext");
        }

        jEdit.saveAllBuffers(view, false);
        String texRoot = new File(tex).getParent().toString();
        StringBuffer str = new StringBuffer(command);
        str.append(" '").append(tex).append(ext).append("'");
        command = str.toString();
        boolean detach = jEdit.getBooleanProperty(commandProps + ".detach");
        runCommand(view, texRoot, command, detach);
        
    }
    
    public static void compile(View view, Buffer buffer, boolean prompt) {
        historyCommand(view, buffer, prompt, "latex.compile", "latextools.compile.history", "Enter Compilation Command");
    }

    public static void bibtex(View view, Buffer buffer) {
        historyCommand(view, buffer, false, "latex.bibtex", null, "");
    }

    public static void viewOutput(View view, Buffer buffer, boolean prompt) {
        historyCommand(view, buffer, prompt, "latex.viewoutput", "latextools.viewoutput.history", "Enter Viewer Command");
    }
    
    private static void runCommand(View view, String dir, String command, boolean detach) {
        Console console = (Console)view.getDockableWindowManager().getDockable(
                                  "console");
        Shell _shell = Shell.getShell("System");
        console.setShell(_shell);
        console.run(_shell, console, "%kill");
        console.run(_shell, console, "cd " + '"' + dir + '"');
        console.run(_shell, console, command);
        if (detach){
            console.run(_shell, console, "%detach");
        }
    }
    
    public static void deleteWorkingFiles(View view, Buffer buffer) {
        String tex = getMainTeXPath(buffer);

        if (!(tex.substring(tex.length() - 3, tex.length()).equals("tex"))) {
            Macros.error(view, tex + " is not a TeX file.");

            return;
        }

        String[] extensions = {
            ".log", ".bak", ".aux", ".bbl", ".blg", ".toc", ".pdf", ".xyc", 
            ".out", ".tex~", ".bak"
        };
        WorkingClassDialog dialog = new WorkingClassDialog(view, extensions);
        dialog.setVisible(true);
        Set toRemove = dialog.getToRemove();

        if (toRemove == null) {

            return;
        }

        File dir = (new File(tex)).getParentFile();
        File[] toDelete = dir.listFiles(new ExtensionFilter(toRemove));
        StringBuffer sb = new StringBuffer(
                                  "<html><h3>About to delete the following files:</h3>");

        for (int i = 0; i < toDelete.length; i++) {
            sb.append(toDelete[i]);
            sb.append("<br>");
        }

        sb.append("<br><b>Erase Now?");
        int del = Macros.confirm(view, sb.toString(), 
                                 JOptionPane.YES_NO_OPTION);

        if (del == JOptionPane.YES_OPTION) {

            for (int i = 0; i < toDelete.length; i++) {
                toDelete[i].delete();
            }
        }
    }
    
    private static class CommandHistoryDialog
        extends JDialog
        implements ActionListener,
                   WindowListener {
        private HistoryTextField htf;
        private HistoryTextField extHtf;
        private String command;
        private String extension;

        CommandHistoryDialog(Frame owner, String historyProps, String dialogTitle) {
            super(owner, dialogTitle, true);
            JPanel panel = new JPanel();
            htf = new HistoryTextField(historyProps, false, 
                                       true);
            htf.setColumns(20);
            extHtf = new HistoryTextField(historyProps + ".ext", false, 
                                       true);
            extHtf.setColumns(8);

            if (htf.getModel().getSize() > 0) {
                htf.setText(htf.getModel().getItem(0));
            }
            if (extHtf.getModel().getSize() > 0) {
                extHtf.setText(htf.getModel().getItem(0));
            }

            htf.addActionListener(this);
            panel.setLayout(new GridLayout(0,1));
            panel.add((new JPanel()).add(htf));
            panel.add((new JPanel()).add(extHtf));
            JButton ok = new JButton("OK");
            JButton cancel = new JButton("Cancel");
            ok.addActionListener(this);
            cancel.addActionListener(this);
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(ok);
            buttonPanel.add(cancel);
            panel.add(buttonPanel);
            setContentPane(panel);
            addWindowListener(this);
            pack();
            setLocation(getCenter(owner, this));
        }

        public String getCommand() {

            return command;
        }
        
        public String getExtension() {

            return extension;
        }

        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand() == "OK") {
                command = htf.getText();
                htf.addCurrentToHistory();
                extension = extHtf.getText();
                extHtf.addCurrentToHistory();
                setVisible(false);
            } else if (e.getActionCommand() == "Cancel") {
                command = null;
                extension = null;
                setVisible(false);
            }
        }

        public void windowActivated(WindowEvent e) {
        }

        public void windowClosed(WindowEvent e) {
        }

        public void windowClosing(WindowEvent e) {
            command = null;
            setVisible(false);
        }

        public void windowDeactivated(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {
        }

        public void windowOpened(WindowEvent e) {
        }
    }

    private static class WorkingClassDialog
        extends JDialog
        implements ActionListener {
        private String[] extensions;
        private Set toRemove;
        private JCheckBox[] boxes;

        WorkingClassDialog(Frame owner, String[] extensions) {
            super(owner, "Erase Working Files", true);
            this.extensions = extensions;
            toRemove = new HashSet();
            boxes = new JCheckBox[extensions.length];
            JPanel boxPanel = new JPanel(new GridLayout(0, 2));

            for (int i = 0; i < boxes.length; i++) {
                boxes[i] = new JCheckBox(extensions[i]);
                boxes[i].setSelected(true);
                boxPanel.add(boxes[i]);
            }

            JButton ok = new JButton("OK");
            JButton cancel = new JButton("Cancel");
            ok.addActionListener(this);
            cancel.addActionListener(this);

            if (boxes.length % 2 == 1) {
                boxPanel.add(new JLabel(""));
            }

            boxPanel.add(ok);
            boxPanel.add(cancel);
            setContentPane(boxPanel);
            pack();
            setLocation(getCenter(owner, this));
        }

        public Set getToRemove() {

            return toRemove;
        }

        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand() == "OK") {

                for (int i = 0; i < extensions.length; i++) {

                    if (boxes[i].isSelected()) {
                        toRemove.add(boxes[i].getText());
                    }
                }

                setVisible(false);
            } else if (e.getActionCommand() == "Cancel") {
                toRemove = null;
                setVisible(false);
            }
        }
    }

    private static class ExtensionFilter
        implements FilenameFilter {
        StringBuffer sb;
        RE regEx;

        ExtensionFilter(Set accept) {
            Iterator it = accept.iterator();
            sb = new StringBuffer("(\\w+?\\");
            sb.append(it.next()).append(")");

            for (; it.hasNext();) {
                sb.append("|(\\w+?\\");
                sb.append(it.next()).append(")");
            }

            sb.append("\\b");

            try {
                regEx = new RE(sb.toString());
            } catch (REException e) {
                e.printStackTrace();
            }
        }

        public boolean accept(File dir, String name) {

            return regEx.getMatch(name) != null;
        }
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
        REMatch[] matches = findInDocument(buffer, IMPORT_REG_EX, startLine, endLine);
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

    
    private static Point getCenter(Component parent, Component dialog) {
        Dimension pd = parent.getSize();
        Dimension cd = dialog.getSize();
        Point pp = parent.getLocation();
        Point cp = new Point(pp);
        int x = (int)((pd.getWidth() - cd.getWidth()) / 2);
        int y = (int)((pd.getHeight() - cd.getHeight()) / 2);
        cp.translate(x, y);

        return cp;
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
