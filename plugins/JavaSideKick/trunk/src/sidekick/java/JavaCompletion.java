package sidekick.java;

import java.util.List;
import java.util.StringTokenizer;

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

    // CONSTRUCTOR means insert everything after the first parenthese
    public static final int CONSTRUCTOR = 4;

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

        // Trim the return type
        if (to_insert.lastIndexOf(':') != -1) {
            to_insert = to_insert.substring(0, to_insert.lastIndexOf(':')).trim();
        }

        if (insertionType == DOT) {
            int dot_index = text.lastIndexOf('.');
            if (dot_index > 0)
                to_replace = text.substring(dot_index + 1);
        }
        else if (insertionType == CONSTRUCTOR) {
            to_replace = "(";
            to_insert = to_insert.substring(to_insert.indexOf('('));
        }

        int caret = textArea.getCaretPosition();
        Selection s = textArea.getSelectionAtOffset( caret );
        int start = ( s == null ? caret : s.getStart() );
        JEditBuffer buffer = textArea.getBuffer();

        try {
            buffer.beginCompoundEdit();
            buffer.remove( start - to_replace.length(), to_replace.length() );
            // If the text to insert has parameters and superabbrevs is installed, use it
            // Otherwise, insert up to the first parenthese (if any)
            int popen, pclose;
            if ((popen = to_insert.indexOf('(')) != -1 && (pclose = to_insert.indexOf(')')) != -1
                    && jEdit.getPlugin("SuperAbbrevsPlugin") != null) {
                String params = to_insert.substring(popen + 1, pclose);
                to_insert = to_insert.substring(0, popen);
                StringTokenizer tokenizer = new StringTokenizer(params, ",");
                int token = 1;
                StringBuilder new_params = new StringBuilder();
                while (tokenizer.hasMoreTokens()) {
                    new_params.append("${").append(token).append(":").append(tokenizer.nextToken()).append("}");
                    if (tokenizer.hasMoreTokens())
                        new_params.append(", ");
                    token++;
                }
                to_insert += "(" + new_params.toString() + ")";
                superabbrevs.SuperAbbrevs.expandAbbrev(textArea.getView(), to_insert, null);
            }
            else {
                if (popen != -1) {
                    to_insert = to_insert.substring(0, popen);
                }
                buffer.insert( start - to_replace.length(), to_insert );
            }
        }
        finally {
            buffer.endCompoundEdit();
        }
    }
}