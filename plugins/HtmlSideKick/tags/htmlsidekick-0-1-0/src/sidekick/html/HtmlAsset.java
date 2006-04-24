package sidekick.html;

import sidekick.Asset;
import javax.swing.Icon;

public class HtmlAsset extends Asset {
    
    private String name = "";
    
    public HtmlAsset(String name) {
        super(name);        
        this.name = name;
    }
	public Icon getIcon() { return null; }

    public String getShortString() { return name; }

	public String getLongString() { return name; }

	
}
