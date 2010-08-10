package xml.parser;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.Position;
import org.xml.sax.Attributes;

// A marker class for nodes in a TLD file.
public class TldXmlTag extends XmlTag {

    // icons for the main children of <taglib>
    public static final ImageIcon TAG_ICON = new ImageIcon(TldXmlTag.class.getResource("/icons/T.png"));
    public static final ImageIcon FUNCTION_ICON = new ImageIcon(TldXmlTag.class.getResource("/icons/F.png"));
    public static final ImageIcon LISTENER_ICON = new ImageIcon(TldXmlTag.class.getResource("/icons/L.png"));
    public static final ImageIcon EXTENSION_ICON = new ImageIcon(TldXmlTag.class.getResource("/icons/E.png"));
    public static final ImageIcon VALIDATOR_ICON = new ImageIcon(TldXmlTag.class.getResource("/icons/V.png"));
    public static final ImageIcon TAGFILE_ICON = new ImageIcon(TldXmlTag.class.getResource("/icons/tf.png"));
    
    private String originalName;

    public TldXmlTag(String name, String namespace, Position start, Attributes attributes) {
        super(name, namespace, start, attributes);
        originalName = name;
    }

    public String getShortString() {
        return name;
    }
    
    public String getOriginalName() {
        return originalName;   
    }

    public Icon getIcon() {
        if ("tag".equals(originalName)) {
            return TAG_ICON;
        }
        if ("function".equals(originalName)) {
            return FUNCTION_ICON;   
        }
        if ("listener".equals(originalName)) {
            return LISTENER_ICON; 
        }
        if ("validator".equals(originalName)) {
            return VALIDATOR_ICON;
        }
        if ("tag-file".equals(originalName)) {
            return TAGFILE_ICON;   
        }
        if ("tag-extension".equals(originalName)) {
            return EXTENSION_ICON;   
        }
        return super.getIcon();
    }


    public boolean canAddCharacters() {
        return "name".equals(name);
    }
}