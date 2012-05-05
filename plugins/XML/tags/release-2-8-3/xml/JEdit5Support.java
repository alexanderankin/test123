/*
 * JEdit5Support.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;

import org.gjt.sp.jedit.jEdit;
import org.jedit.keymap.Keymap;
import org.jedit.keymap.KeymapManager;

/**
 * methods making use of the new jEdit 5 APIs
 */
public class JEdit5Support {
	
	/**
	 * use the org.jedit.keymap API to set a shortcut programatically.
	 * Used to implement "Insert closing tag" when opening tag is typed. 
	 * @param name the action name  (e.g. "xml-insert-closing-tag"), or <code>null</code> to delete a shortcut
	 * @param shortcut	shortcut to activate the action
	 * 
	 * @see	Keymap#setShortcut(String, String)
	 */
	public static void setShortcut(String name, String shortcut){
		KeymapManager mgr = jEdit.getKeymapManager();
		Keymap km = mgr.getKeymap();
		km.setShortcut(name+".shortcut", shortcut);
		// must call this to refresh key bindings
		jEdit.propertiesChanged();
	}

}
