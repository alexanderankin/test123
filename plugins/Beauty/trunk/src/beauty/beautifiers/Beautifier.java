package beauty.beautifiers;

import beauty.parsers.ParserException;
import org.gjt.sp.jedit.Buffer;

/**

 * Base class for all beautifiers.  This class contains methods to get and set
 * various Buffer parameters.
 * <p>
 * Added a reference to the Buffer itself.  I did this for the DefaultBeautifier
 * since it needs to tokenize the buffer.
 * TODO: verify this is no longer necessary.  The DefaultBeautifier is now using
 * a temporary buffer for tokenization.
 */

public abstract class Beautifier {

    // the line separator, defaults to the system line separator, but will
    // be populated with the current line separator for the buffer being
    // beautified
    protected String lineSeparator = System.getProperty("line.separator");
    
    // buffer reference
    protected Buffer buffer = null;
    
    // buffer settings
    protected String editMode = null;
    protected int tabWidth = 4;
    protected int wrapMargin = 1024;
    protected String wrapMode = "none";
    protected int initialLevel = 0;
    protected int indentWidth = 4;
    protected boolean softTabs = true;
    protected String indent = "    ";
    protected String doubleIndent = indent + indent;
    
    public static final String SERVICE_NAME = "beauty.beautifiers.Beautifier";
    
    /**

     * Subclasses must have this method.
     * @param text The text to beautify.
     * @return The formatted text.
     */

    public abstract String beautify(String text) throws ParserException;
    
    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;   
    }
    
    public Buffer getBuffer() {
        return buffer;   
    }
    
    public void setTabWidth(int w) {
        tabWidth = w;   
    }
    
    public int getTabWidth() {
        return tabWidth;   
    }

    public void setIndentWidth(int w) {
        indentWidth = w;
        if (indentWidth <= 0) {
            indentWidth = 4;
        }
        indent = "";
        for (int i = 0; i < w; i++) {
            indent += " ";
        }
        doubleIndent = indent + indent;
    }
    
    public int getIndentWidth() {
        return indentWidth;   
    }
    
    public void setUseSoftTabs(boolean b) {
        softTabs = b;
        if (b) {
            indent = "\t";
            doubleIndent = "\t\t";
        }
        else {
            setIndentWidth(indentWidth);
        }
    }
    
    public boolean getUseSoftTabs() {
        return softTabs;   
    }
    
    public void setLineSeparator(String ls) {
        lineSeparator = ls;   
    }
    
    public String getLineSeparator() {
        return lineSeparator;   
    }
    
    public void setWrapMargin(int i) {
        wrapMargin = i;   
    }
    
    public int getWrapMargin() {
        return wrapMargin;   
    }
    
    public void setWrapMode(String s) {
        if (s != null && (s.equals("none") || s.equals("soft") || s.equals("hard")))
            wrapMode = s;   
    }
    
    public String getWrapMode() {
        return wrapMode;   
    }
    
    public void setEditMode(String m) {
        editMode = m;   
    }
    
    public String getEditMode() {
        return editMode;   
    }
    
    public void setInitialIndentLevel(int level) {
        initialLevel = level;   
    }
}
