//          \includegraphics*[width=7cm]{graphics\complexes.png}
//      :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools;

import console.Console;
import console.Shell;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class LaTeXMacros {
    public static final String MAIN_TEX_FILE_KEY = "latex.root";

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
        REMatch match = findInDocument(buffer, regex.toString(), 0, 5);

        if (match != null) {
            path = match.toString(1);
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

    private static REMatch findInDocument(Buffer buf, String regex) {

        return findInDocument(buf, regex, 0, buf.getLineCount());
    }

    private static REMatch findInDocument(Buffer buf, String regex, 
                                          int startLine, int endLine) {

        for (int i = startLine;
             i < (buf.getLineCount() < endLine ? buf.getLineCount() : endLine);
             i++) {
            String s = buf.getLineText(i);
            RE exp = null;

            try {
                exp = new RE(regex, RE.REG_ICASE | RE.REG_MULTILINE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            REMatch match = exp.getMatch(s);

            if (match != null) {

                return match;
            }
        }

        return null;
    }

    public static void compile(View view, Buffer buffer, boolean prompt) {
        String tex = getMainTeXPath(buffer);
        String command;

        if (prompt) {
           command = showCompileHistoryDialog(view);
           if (command == null){
              Macros.message(view, "Compile Aborted...");
              return;
           }
        } else{
           command = jEdit.getProperty("latex.compile.command");
        }
        
        if (tex.substring(tex.length() - 3, tex.length()).equals("tex")) {
            jEdit.saveAllBuffers(view, false);
            String texRoot = new File(tex).getParent().toString();
            StringBuffer str = new StringBuffer(command);
            str.append(" '").append(tex).append("'");
            command = str.toString();
            runCommand(view, texRoot, command);
            
        } else {
            Macros.error(view, tex + " is not a TeX file.");
        }
    }
    
    private static void runCommand(View view, String dir, String command){
      Console console = (Console) view.getDockableWindowManager().getDockable("console");
      Shell _shell = Shell.getShell("System");
      console.setShell(_shell);
      console.run(_shell, console, "%kill");
      console.run(_shell, console, "cd " + '"' + dir + '"');
      console.run(_shell, console, command);  
    }
    
    public static void bibtex(View view, Buffer buffer){
      String tex = getMainTeXPath(buffer);
	
      if (!new File(tex).exists()) {
         Macros.error(view,tex + " is not a TeX file.");
         return;
      }
      
      if (tex.substring(tex.length()-3,tex.length()).equals("tex")){
       String texRoot = new File(tex).getParent().toString();
       tex = tex.substring(0,tex.length()-4);
         String str = "bibtex '" + tex +"'";
         
         runCommand(view, texRoot, str);
      }
      else {
         Macros.error(view,tex + " is not a TeX file.");
      }

    }
    
    private static String showCompileHistoryDialog(final View view){
        final JDialog dialog = new JDialog(view, "Enter Compilation Command", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter(){
           public void windowClosing(WindowEvent e){
              setWindowClosed(true);
              dialog.setVisible(false);
           }
        });
        JPanel panel = new JPanel();
        HistoryTextField htf = new HistoryTextField("latextools.compile.history", false, true);
        htf.setColumns(20);
        htf.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              dialog.setVisible(false);
           }
        });
        panel.add(htf);
        
        if (htf.getModel().getSize() > 0){
           htf.setText(htf.getModel().getItem(0));
        }
        
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setVisible(true);
        String out = windowClosed ? null : htf.getText();        
        
        //Macros.message(view, out);
        setWindowClosed(false);
        
        return out;
    }
    
    
    private static boolean windowClosed = false;
    
    private static void setWindowClosed(boolean value){
       windowClosed = value;
    }
    
}
