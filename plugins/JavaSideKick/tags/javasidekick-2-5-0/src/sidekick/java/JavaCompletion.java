package sidekick.java;

import java.util.List;

import sidekick.java.node.*;

import sidekick.SideKickCompletion;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.*;

public class JavaCompletion extends SideKickCompletion {
    
    // PARTIAL means replace all of the text
    public static final int PARTIAL = 2;
    
    // DOT type means to replace everything after the last dot in the text
    public static final int DOT = 3;
    
    private int insertionType = PARTIAL;
    
    public JavaCompletion(View view, String text, List choices) {
        super(view, text, choices);  
        determineInsertionType();
    }
    
    public JavaCompletion(View view, String text, int type, List choices) {
        super(view, text, choices);
        this.insertionType = type;
    }
    
    public List getChoices() {
        return super.items;  
    }
    
    private void determineInsertionType() {
        if (text.endsWith("."))
            insertionType = DOT;
        else
            insertionType = PARTIAL;
    }
    
    public void setInsertionType(int type) {
        insertionType = type;   
    }
    
    public void insert( int index ) {
        String to_replace = text;
        String to_insert = String.valueOf(get(index));

        if (insertionType == DOT) {
            int dot_index = text.lastIndexOf(".");
            if (dot_index > 0) 
                to_replace = text.substring(dot_index + 1);
        }
        
        int caret = textArea.getCaretPosition();
        Selection s = textArea.getSelectionAtOffset( caret );
        int start = ( s == null ? caret : s.getStart() );
        int end = ( s == null ? caret : s.getEnd() );
        JEditBuffer buffer = textArea.getBuffer();
        
        try {
            buffer.beginCompoundEdit();
            buffer.remove( start - to_replace.length(), to_replace.length() );
            buffer.insert( start - to_replace.length(), to_insert );
        }
        finally {
            buffer.endCompoundEdit();
        }
    }
}
