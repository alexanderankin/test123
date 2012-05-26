package xml.parser;

import javax.swing.Icon;
import javax.swing.text.Position;
import org.xml.sax.Attributes;

import eclipseicons.EclipseIconsPlugin;

// This class provides special icons for Maven pom.xml files.
public class MvnXmlTag extends XmlTag {

    private String originalName;

    public MvnXmlTag(String name, String namespace, Position start, Attributes attributes) {
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
        if ("properties".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("properties_obj.gif"); 
        }
        if ("resources".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("resources_obj.gif");
        }
        if ("resource".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("resource_obj.gif");   
        }
        if ("plugins".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("plugins_obj.gif");
        }
        if ("plugin".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("plugin_obj.gif");
        }
        if ("dependencies".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("jars_obj.gif");
        }
        if ("dependency".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("jar_obj.gif");
        }
        if ("reporting".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("report_obj.gif");
        }
        if ("pluginRepository".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("repositories.gif");
        }
        if ("pluginRepository".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("repository_obj.gif");
        }
        if ("repositories".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("repositories.gif");
        }
        if ("repository".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("repository_obj.gif");
        }
        if ("build".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("build_obj.gif");
        }
        if ("developer".equals(originalName)) {
            return EclipseIconsPlugin.getIcon("person_obj.gif");
        }
        return EclipseIconsPlugin.getIcon("tag.gif");
    }

	/**
 	 * @return <code>true</code> if this tag should accumulate the characters from 
 	 * the body of a tag. This method returns true if the tag name is "name" or
 	 * "artivactId".
 	 */
    public boolean canAddCharacters() {
        return "name".equals(name) || "artifactId".equals(name) || "id".equals(name);
    }
}