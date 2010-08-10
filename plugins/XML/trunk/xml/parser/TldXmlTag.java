package xml.parser;

import javax.swing.text.Position;
import org.xml.sax.Attributes;


// A marker class for nodes in a TLD file.
public class TldXmlTag extends XmlTag {
    public TldXmlTag(String name, String namespace, Position start, Attributes attributes) {
        super(name, namespace, start, attributes);   
    }
    
    public String getShortString() {
        return name;   
    }
    
    public boolean canAddCharacters() {
        return "name".equals(name);   
    }
}