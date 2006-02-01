package sidekick.java;

import java.util.List;
import sidekick.SideKickCompletion;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.*;

public class JavaCompletion extends SideKickCompletion {
    
    private boolean withThis = false;
    
    public JavaCompletion(View view, String text, List choices) {
        super(view, text, choices);   
    }
    
    public JavaCompletion(View view, String text, boolean withThis, List choices) {
        super(view, text, choices);
        this.withThis = withThis;
    }
    
    public void insert( int index ) {
        if (!text.endsWith("."))
            text = text + ".";
        String selected = (withThis ? "this." : "" ) + text + String.valueOf( get( index ) );
        int caret = textArea.getCaretPosition();
        Selection s = textArea.getSelectionAtOffset( caret );
        int start = ( s == null ? caret : s.getStart() );
        int end = ( s == null ? caret : s.getEnd() );
        JEditBuffer buffer = textArea.getBuffer();
        
        try {
            buffer.beginCompoundEdit();
            buffer.remove( start - text.length(), text.length() );
            buffer.insert( start - text.length(), selected );
        }
        finally {
            buffer.endCompoundEdit();
        }
    }
}
