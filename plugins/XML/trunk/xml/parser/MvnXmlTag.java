package xml.parser;

import javax.swing.Icon;
import javax.swing.text.Position;
import org.xml.sax.Attributes;

import eclipseicons.EclipseIconsPlugin;

// This class provides special icons for Maven pom.xml files.
public class MvnXmlTag extends RenamedXmlTag {


    public MvnXmlTag(String name, String namespace, Position start, Attributes attributes) {
        super(name, namespace, start, attributes);
    }

    public Icon getIcon() {
        if ("properties".equals(name)) {
            return EclipseIconsPlugin.getIcon("properties_obj.gif"); 
        }
        if ("resources".equals(name)) {
            return EclipseIconsPlugin.getIcon("resources_obj.gif");
        }
        if ("resource".equals(name)) {
            return EclipseIconsPlugin.getIcon("resource_obj.gif");   
        }
        if ("plugins".equals(name)) {
            return EclipseIconsPlugin.getIcon("plugins_obj.gif");
        }
        if ("plugin".equals(name)) {
            return EclipseIconsPlugin.getIcon("plugin_obj.gif");
        }
        if ("dependencies".equals(name)) {
            return EclipseIconsPlugin.getIcon("jars_obj.gif");
        }
        if ("dependency".equals(name)) {
            return EclipseIconsPlugin.getIcon("jar_obj.gif");
        }
        if ("reporting".equals(name)) {
            return EclipseIconsPlugin.getIcon("report_obj.gif");
        }
        if ("pluginRepository".equals(name)) {
            return EclipseIconsPlugin.getIcon("repositories.gif");
        }
        if ("pluginRepository".equals(name)) {
            return EclipseIconsPlugin.getIcon("repository_obj.gif");
        }
        if ("repositories".equals(name)) {
            return EclipseIconsPlugin.getIcon("repositories.gif");
        }
        if ("repository".equals(name)) {
            return EclipseIconsPlugin.getIcon("repository_obj.gif");
        }
        if ("build".equals(name)) {
            return EclipseIconsPlugin.getIcon("build_obj.gif");
        }
        if ("developer".equals(name)) {
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