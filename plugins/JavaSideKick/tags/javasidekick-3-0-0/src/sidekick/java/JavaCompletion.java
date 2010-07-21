package sidekick.java;

import java.util.List;
import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;

import sidekick.java.node.*;

import sidekick.SideKickCompletion;
import sidekick.SideKickParsedData;
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
        else if (jEdit.getBooleanProperty("sidekick.java.importPackage")) {
        	insertAsImport(to_insert);
        	return;
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
    
    private void insertAsImport(String cls) {
    	JEditBuffer buffer = textArea.getBuffer();
    	String newImport = "import "+cls+";\n";
    	int startOfClass = -1;
    	SideKickParsedData data = SideKickParsedData.getParsedData(view);
    	for (int i = 0; i<data.root.getChildCount(); i++) {
    		DefaultMutableTreeNode node = (DefaultMutableTreeNode) data.root.getChildAt(i);
    		Object ob = node.getUserObject();
    		if (ob instanceof ClassNode) {
    			startOfClass = ((ClassNode) ob).getStart().getOffset();
    			break;
    		}
    	}
    	if (startOfClass == -1) return;
    	int classLine = buffer.getLineOfOffset(startOfClass);
    	int startOfImports = -1;
    	for (int i = 0; i<classLine; i++) {
    		if (buffer.getLineText(i).startsWith("import ")) {
    			startOfImports = i;
    			break;
    		}
    	}
    	if (startOfImports != -1) {
    		boolean inserted = false;
    		int endOfImports = startOfImports;
    		for (int i = startOfImports; i<classLine; i++) {
    			String importLine = buffer.getLineText(i);
    			if (!importLine.startsWith("import ")) continue;
    			endOfImports = i;
    			if (importLine.compareTo(newImport) > 0) {
    				buffer.insert(textArea.getLineStartOffset(i), newImport);
    				inserted = true;
    				break;
    			}
    		}
    		if (!inserted)
    			buffer.insert(textArea.getLineStartOffset(endOfImports+1), newImport);
    	}
    	else {
    		buffer.insert(startOfClass, "//{{{ Imports\n"+newImport+"//}}}\n");
    	}
    }
}