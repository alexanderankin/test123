/*:folding=indent:
* TextMacros.java - Macros for working with text.
* Copyright (C) 2003 Anthony Roy
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
package uk.co.antroy.latextools.macros;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.DisplayManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import uk.co.antroy.latextools.parsers.LaTeXAsset;


public class TextMacros {

    //~ Methods ...............................................................

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

    public static void newCommand(View view) {

        String command = Macros.input(view, "Enter command");

        if (command == null) {

            return;
        }

        surround(view, "\\" + command + "{", "}");
    }

    public static void newEnvironment(View view) {

        String env = Macros.input(view, "Enter environment name");

        if (env == null) {

            return;
        }

        surround(view, "\\begin{" + env + "}\n", "\n\\end{" + env + "}");
    }

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

        if (expression == null) {

            return;
        }

        String noString = Macros.input(view, "Enter number of iterations");

        if (noString == null) {

            return;
        }

        int no = Integer.parseInt(noString);
        int start;

        if (startDialog) {

            String startString = Macros.input(view, "Enter start number");

            if (startString == null) {

                return;
            }

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
        if (prefix == null || prefix.length() == 0) {

            return;
        }

        if (suffix == null || suffix.length() == 0) {
            suffix = prefix;
        }

        String text = textArea.getSelectedText();

        if (text == null) {
            text = "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(text);
        sb.append(suffix);
        textArea.setSelectedText(sb.toString());

        //if no selected text, put the caret between the tags
        if (text.length() == 0) {
            textArea.setCaretPosition(caret + prefix.length());
        }
    }

    public static void surround(View view) {

        String prefix = Macros.input(view, "Enter prefix");

        if (prefix == null) {

            return;
        }

        String suffix = Macros.input(view, "Enter suffix");

        if (suffix == null) {

            return;
        }

        surround(view, prefix, suffix);
    }

    public static void visitAsset(View view, LaTeXAsset asset) {

        Buffer goToBuff = jEdit.openFile(view, asset.getFile().toString());
        view.setBuffer(goToBuff);

        int line = goToBuff.getLineOfOffset(asset.getStart().getOffset());
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
