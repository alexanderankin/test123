/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.stdlib;

import java.util.ArrayList;
import java.util.List;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 *
 * @author Sune Simonsen
 */
public class Tpg {
    
    private View view;
    private JEditTextArea textArea;
    private Buffer buffer;
    private String indent;
    private StringBuffer out;

    public Tpg(String indent, View view, 
            JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.textArea = textArea;
        this.buffer = buffer;
        this.indent = indent;
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
        int line = textArea.getCaretLine();
        if(textArea.getLineLength(line) != 0) {
            String lineText = textArea.getLineText(line);
        
            int lineStart = textArea.getLineStartOffset(line);
            int offset = textArea.getCaretPosition() - lineStart;

            String noWordSep = buffer.getStringProperty("noWordSep");

            if(offset == textArea.getLineLength(line)) offset--;

            int wordStart = TextUtilities.findWordStart(lineText,offset,
                    noWordSep,true,false,false);
            int wordEnd = TextUtilities.findWordEnd(lineText,offset+1,
                    noWordSep,true,false,false);
            
            return textArea.getText(lineStart+wordStart, wordEnd - wordStart);
        }
        return "";
    }
    
    public char getChar() {
        int caretPos = textArea.getCaretPosition();
        return textArea.getText().charAt(caretPos);
    }
    
    public String getBufferText() {
        return textArea.getText();
    }
    
    public String getBufferTextBeforeCaret() {
        return textArea.getText(0, textArea.getCaretPosition());
    }
    
    public String getBufferTextAfterCaret() {
        return textArea.getText(textArea.getCaretPosition(), textArea.getBufferLength());
    }
    
    public String getSelection() {
        String selection = textArea.getSelectedText();
        return selection == null ? "" : selection;
    }
    
    public String getFileName() {
        return buffer.getName();
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
        int[] selectedLinesNumbers = textArea.getSelectedLines();
        List<String> selectedLines = new ArrayList<String>(
                selectedLinesNumbers.length);
        for (int lineNumber : selectedLinesNumbers) {
            selectedLines.add(textArea.getLineText(lineNumber));
        }
        return selectedLines;
    }
    
    public String getSelectedLines() {
        List<String> selectedLines = getSelectedLinesList();
        return TextUtilities.join(selectedLines, "\n");
    }
    
    public String getLine() {
        int line = textArea.getCaretLine();
        if(textArea.getLineLength(line) != 0) {
            return textArea.getLineText(line);
        }
        return "";
    }
    
    public String getLineBeforeCaret() {
        int line = textArea.getCaretLine();
        
        if(textArea.getLineLength(line) != 0) {
            int lineStart = textArea.getLineStartOffset(line);
            return textArea.getText(lineStart, textArea.getCaretPosition());
        }
        return "";
    }
    
    public String getLineAfterCaret() {
        int line = textArea.getCaretLine();
        int lineEnd = textArea.getLineEndOffset(line);
        if(textArea.getLineLength(line) != 0 && lineEnd < buffer.getLength()) {
            return textArea.getText(textArea.getCaretPosition(), lineEnd);
        }
        return "";
    }
}
