package beauty.beautifiers;

import beauty.parsers.ParserException;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;

public abstract class Beautifier {

    // the line separator, defaults to the system line separator, but will
    // be populated with the current line separator for the buffer being
    // beautified
    protected String lineSeparator = System.getProperty("line.separator");
    
    protected String editMode = null;
    protected int tabWidth = 4;
    protected int indentWidth = 4;
    protected boolean softTabs = true;
    
    protected int wrapMargin = 1024;
    protected String wrapMode = "none";
    
    public static final String SERVICE_NAME = "beauty.beautifiers.Beautifier";
    
    public abstract String beautify(String text) throws ParserException;
    
    public void setTabWidth(int w) {
        tabWidth = w;   
    }
    
    public int getTabWidth() {
        return tabWidth;   
    }
    
    public void setIndentWidth(int w) {
        indentWidth = w;   
    }
    
    public int getIndentWidth() {
        return indentWidth;   
    }
    
    public void setUseSoftTabs(boolean b) {
        softTabs = b;
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
}
