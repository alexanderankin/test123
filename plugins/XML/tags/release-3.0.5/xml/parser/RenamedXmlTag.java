/*
 * RenamedXmlTag.java - an XMLTag for pretty sidekick tree
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * copyright (C) 2012 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

import javax.swing.text.Position;
import org.xml.sax.Attributes;


/**
 * an XMLTag for pretty sidekick tree.
 * It retains its original name as name.
 */
public class RenamedXmlTag extends XmlTag {

    protected String newName;

    public RenamedXmlTag(String name, String namespace, Position start, Attributes attributes) {
        super(name, namespace, start, attributes);
        newName = name;
    }

    public String getShortString() {
        return newName;
    }
    
    public String getNewName() {
        return newName;   
    }

    public void setNewName(String newName) {
        this.newName = newName;   
    }

}