package beauty.beautifiers;

import beauty.parsers.ParserException;

/**
 * Base class for all beautifiers.  This class contains methods to get and set
 * various Buffer parameters.
 */
public abstract class Beautifier {

    // the line separator, defaults to the system line separator, but will
    // be populated with the current line separator for the buffer being
    // beautified
    protected String lineSeparator = System.getProperty("line.separator");
    
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
     * Subclasses may override this method to initialize themselves. The jEdit
     * ServiceManager only calls the constructor once, so this method is called
     * each time prior to the beautify method.
     */
    public void init() {}
    
    /**
     * Subclasses must have this method.
     * @param text The text to beautify.
     * @return The formatted text.
     */
    public abstract String beautify(String text) throws ParserException;
    
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
    
    /**
     * <code>setIndentWidth</code> should be called before this method.    
     */
    public void setUseSoftTabs(boolean b) {
        softTabs = b;
        if (b) {
            setIndentWidth(indentWidth);
        }
        else {
            indent = "\t";
            doubleIndent = "\t\t";
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
