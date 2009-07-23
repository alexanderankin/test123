/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Marcelo Vanzin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package projectviewer.config;

import javax.swing.JCheckBox;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.AbstractContextOptionPane;

/**
 *	ProjectViewer context menu editor.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ContextOptionPane extends AbstractContextOptionPane
{

	JCheckBox showUserFirst;

	public ContextOptionPane(String name)
	{
		super(name, jEdit.getProperty("projectviewer.options.context.caption"));
	}

	protected void _init()
	{
		super._init();
		showUserFirst = new JCheckBox(jEdit.getProperty("options.projectviewer.contextmenu.userfirst"));
		showUserFirst.setSelected(ProjectViewerConfig.getInstance().getUserMenuFirst());
		addButton(showUserFirst);
	}

    /** Returns PV's context menu. */
    protected String getContextMenu()
    {
        return ProjectViewerConfig.getInstance().getUserContextMenu();
    }

    /** Saves PV's context menu. */
    protected void saveContextMenu(String menu)
    {
		ProjectViewerConfig.getInstance().setUserContextMenu(menu);
		ProjectViewerConfig.getInstance().setUserMenuFirst(showUserFirst.isSelected());
    }

}

