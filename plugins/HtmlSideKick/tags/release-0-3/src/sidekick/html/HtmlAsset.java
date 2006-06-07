package sidekick.html;

import sidekick.html.parser.html.HtmlDocument;
import sidekick.Asset;
import javax.swing.Icon;

public class HtmlAsset extends Asset {
    
    private HtmlDocument.HtmlElement element = null;
    private String name = "";
    private String longString = null;
    
    public HtmlAsset(HtmlDocument.HtmlElement element) {
        super(element.toString());    
        this.element = element;
        this.name = element.toString();
    }
    
    public HtmlDocument.HtmlElement getHtmlElement() {
        return element;   
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
