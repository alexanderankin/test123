package sidekick.html;

import sidekick.Asset;
import javax.swing.Icon;

public class HtmlAsset extends Asset {
    
    private String name = "";
    private String longString = null;
    
    public HtmlAsset(String name) {
        super(name);        
        this.name = name;
    }
	public Icon getIcon() { return null; }

    public String getShortString() { return name; }

	public String getLongString() {
        return longString == null ? name : longString;
    }
    
    public void setLongString(String s) {
        longString = s;   
    }

	
}
