package xml.parser;

import javax.swing.Icon;
import javax.swing.text.Position;
import org.xml.sax.Attributes;

import eclipseicons.EclipseIconsPlugin;

// A marker class for nodes in a TLD file.
public class TldXmlTag extends RenamedXmlTag {

    public TldXmlTag(String name, String namespace, Position start, Attributes attributes) {
        super(name, namespace, start, attributes);
    }

    public Icon getIcon() {
        if ("tag".equals(name)) {
            return EclipseIconsPlugin.getIcon("tag-html.gif"); 
        }
        if ("function".equals(name)) {
            return EclipseIconsPlugin.getIcon("methpub_obj.gif");
        }
        if ("tag-file".equals(name)) {
            return EclipseIconsPlugin.getIcon("file_obj.gif");   
        }
        if ("listener".equals(name)) {
            return EclipseIconsPlugin.getIcon("activity.gif");
        }
        if ("validator".equals(name)) {
            return EclipseIconsPlugin.getIcon("complete_task.gif");
        }
        if ("tag-extension".equals(name)) {
            return EclipseIconsPlugin.getIcon("tag_generic_emphasized_obj.gif");
        }
        return EclipseIconsPlugin.getIcon("tag.gif");
    }

	/**
 	 * @return <code>true</code> if this tag should accumulate the characters from 
 	 * the body of a tag. This method returns true if the tag name is "name".
 	 */
    public boolean canAddCharacters() {
        return "name".equals(name);
    }
}