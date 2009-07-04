/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.stdlib;

import java.util.ArrayList;
import java.util.List;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import superabbrevs.JEditInterface;

/**
 *
 * @author Sune Simonsen
 */
public class Tpg {
    
	private StringBuffer out;
	private final JEditInterface jedit;

    public Tpg(String indent, JEditInterface jedit) {
		this.jedit = jedit;
    }

    public StringBuffer getOut() {
        return out;
    }

    public void setOut(StringBuffer out) {
        this.out = out;
    }
    
    public void print(String s) {
        out.append(s);
    }

    public void println(String s){
        out.append(s+"\n");
    }
    
    public String getWord() {
        int line = jedit.getCaretLine();
        if(jedit.getLineLength(line) != 0) {
            String lineText = jedit.getLineText(line);
        
            int lineStart = jedit.getLineStartOffset(line);
            int offset = jedit.getCaretPosition() - lineStart;

            String noWordSep = jedit.getNoWordSep();

            if(offset == jedit.getLineLength(line)) offset--;

            int wordStart = TextUtilities.findWordStart(lineText,offset,
                    noWordSep,true,false,false);
            int wordEnd = TextUtilities.findWordEnd(lineText,offset+1,
                    noWordSep,true,false,false);
            
            return jedit.getText(lineStart+wordStart, wordEnd - wordStart);
        }
        return "";
    }
    
    public char getChar() {
        int caretPos = jedit.getCaretPosition();
        return jedit.getText().charAt(caretPos);
    }
    
    public String getBufferText() {
        return jedit.getText();
    }
    
    public String getBufferTextBeforeCaret() {
        return jedit.getText(0, jedit.getCaretPosition());
    }
    
    public String getBufferTextAfterCaret() {
        return jedit.getText(jedit.getCaretPosition(), jedit.getBufferLength());
    }
    
    public String getSelection() {
        String selection = jedit.getSelectedText();
        return selection == null ? "" : selection;
    }
    
    public String getFileName() {
        return jedit.getBufferName();
    }

    public String variable(int number, String value) {
        return "${" + number + ":" + value + "}";
    }

    public String reference(int number) {
        return "$" + number;
    }
    
    public String transformationField(int number, String code) {
        return "${" + number + "=" + code + "}";
    }

    public String completeField(int number, String words) {
        String[] wordArray = words.split(",");
        String value = wordArray.length == 0 ? "" : wordArray[0];
        String code = "complete(s,\"" + words + "\")";
        return variable(number, value) + transformationField(number, code);
    }
    
    private List<String> getSelectedLinesList() {
        int[] selectedLinesNumbers = jedit.getSelectedLines();
        List<String> selectedLines = new ArrayList<String>(
                selectedLinesNumbers.length);
        for (int lineNumber : selectedLinesNumbers) {
            selectedLines.add(jedit.getLineText(lineNumber));
        }
        return selectedLines;
    }
    
    public String getSelectedLines() {
        List<String> selectedLines = getSelectedLinesList();
        return TextUtilities.join(selectedLines, "\n");
    }
    
    public String getLine() {
        int line = jedit.getCaretLine();
        if(jedit.getLineLength(line) != 0) {
            return jedit.getLineText(line);
        }
        return "";
    }
    
    public String getLineBeforeCaret() {
        int line = jedit.getCaretLine();
        
        if(jedit.getLineLength(line) != 0) {
            int lineStart = jedit.getLineStartOffset(line);
            return jedit.getText(lineStart, jedit.getCaretPosition());
        }
        return "";
    }
    
    public String getLineAfterCaret() {
        int line = jedit.getCaretLine();
        int lineEnd = jedit.getLineEndOffset(line);
        if(jedit.getLineLength(line) != 0 && lineEnd < jedit.getBufferLength()) {
            return jedit.getText(jedit.getCaretPosition(), lineEnd);
        }
        return "";
    }
}
