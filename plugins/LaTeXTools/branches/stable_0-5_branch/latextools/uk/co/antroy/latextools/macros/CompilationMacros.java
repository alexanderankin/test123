//       \includegraphics*[width=7cm]{graphics\complexes.png}
//      :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools.macros;

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

public class CompilationMacros {

    private static void historyCommand(View view, Buffer buffer, boolean prompt, 
                                       String commandProps, 
                                       String historyProps, String dialogTitle){
                                          
        String tex = ProjectMacros.getMainTeXPath(buffer);

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
        
        String fff = "command: " + commandProps.equals("latex.compile") + " Parse Errors: " + jEdit.getBooleanProperty("latex.compile.parse-errors");
        
        
        Log.log(Log.DEBUG, null, fff);
        
        if (commandProps.equals("latex.compile") && jEdit.getBooleanProperty("latex.compile.parse-errors")){
           str.append(" ").append(jEdit.getProperty("latex.compile.c-errors"));
        }
        
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
        String tex = ProjectMacros.getMainTeXPath(buffer);

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
 
}
