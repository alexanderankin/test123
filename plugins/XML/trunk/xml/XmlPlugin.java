/*
 * XmlPlugin.java
 * Copyright (C) 2000, 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
import java.util.Vector;

public class XmlPlugin extends EBPlugin
{
	public static final String TREE_NAME = "xml-tree";
	public static final String INSERT_NAME = "xml-insert";

	// We store the list of declared elements in this buffer-local
	// property
	public static final String DECLARED_ELEMENTS_PROPERTY = "xml.declared-elements";

	// We store the list of declared entities in this buffer-local
	// property
	public static final String DECLARED_ENTITIES_PROPERTY = "xml.declared-entities";

	public void start()
	{
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,TREE_NAME);
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,INSERT_NAME);
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("xml-menu"));
	}

	public void createOptionPanes(OptionsDialog dialog)
	{
		//OptionGroup grp = new OptionGroup("xml");
		//grp.addOptionPane(new GeneralOptionPane());
		//dialog.addOptionGroup(grp);

		dialog.addOptionPane(new GeneralOptionPane());
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof CreateDockableWindow)
		{
			CreateDockableWindow cmsg = (CreateDockableWindow)msg;
			if(cmsg.getDockableWindowName().equals(TREE_NAME))
			{
				cmsg.setDockableWindow(new XmlTree(cmsg.getView(),
					!cmsg.getPosition().equals(
					DockableWindowManager.FLOATING)));
			}
			else if(cmsg.getDockableWindowName().equals(INSERT_NAME))
			{
				cmsg.setDockableWindow(new XmlInsert(
					cmsg.getView()));
			}
		}
		else if(msg instanceof PropertiesChanged)
			EntityManager.propertiesChanged();
	}
}
