//       \includegraphics*[width=7cm]{graphics\complexes.png}
//      :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools.macros;

import uk.co.antroy.latextools.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.DisplayManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;
                  
import org.gjt.sp.jedit.Macros;

public class TextMacros {
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
    
    public static REMatch[] findInDocument(Buffer buf, String regex) {

        return findInDocument(buf, regex, 0, buf.getLineCount() - 1);
    }

    public static REMatch[] findInDocument(Buffer buf, String regex, 
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

    
    public static void visitAsset(View view, LaTeXAsset asset) {
        Buffer goToBuff = jEdit.openFile(view, asset.getFile().toString());
        view.setBuffer(goToBuff);
        int line = goToBuff.getLineOfOffset(asset.start.getOffset());
        JEditTextArea textArea = view.getTextArea();
        DisplayManager fvm = textArea.getDisplayManager();
        fvm.expandFold(line, false);
        textArea.setFirstPhysicalLine(line);
        int lineStart = view.getTextArea().getLineStartOffset(line);
        int lineEnd = view.getTextArea().getLineEndOffset(line);
        Selection.Range sel = new Selection.Range(lineStart, lineEnd);
        view.getTextArea().setSelection(sel);
    }

    
}
