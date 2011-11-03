/*
 * AncestorToolBar.java - The ancestor toolbar
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.ancestor;

import common.gui.itemfinder.AbstractItemFinder;
import common.gui.itemfinder.ItemFinder;
import common.gui.itemfinder.ItemFinderPanel;
import gatchan.jedit.smartopen.FileItemFinder;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class AncestorToolBar extends JPanel
{
	private JToolBar toolbar;
	private final View view;
	private final LinkedList<String> list = new LinkedList<String>();

	//{{{ AncestorToolBar constructor
	public AncestorToolBar(View view)
	{
//		setLayout(new FlowLayout(FlowLayout.LEADING));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		this.view = view;
		add(toolbar);
		add(Box.createGlue());
		add(new JLabel("Search for a file :"));
		ItemFinder itemFinder = new FileItemFinder();
		ItemFinderPanel itemFinderPanel = new ItemFinderPanel(view, itemFinder);
		Dimension maximumSize = itemFinderPanel.getMaximumSize();
		itemFinderPanel.setMaximumSize(new Dimension(500,maximumSize.height));
		Dimension minimumSize = itemFinderPanel.getMinimumSize();
		itemFinderPanel.setMinimumSize(new Dimension(500, minimumSize.height));
		add(itemFinderPanel);
	} //}}}

	//{{{ setBuffer() method
	void setBuffer(Buffer buffer)
	{
		String path = buffer.getPath();

		list.clear();
		while (true)
		{
			list.addFirst(path);
			VFS _vfs = VFSManager.getVFSForPath(path);
			String parent = _vfs.getParentOfPath(path);
			if (path == null || MiscUtilities.pathsEqual(path, parent))
				break;
			path = parent;
		}
		int count = toolbar.getComponentCount() - 1;

		int nbTokens = list.size();
		if (nbTokens < count)
		{
			for (int i = 0; i < count - nbTokens; i++)
			{
				toolbar.remove(0);
			}
		}
		else if (nbTokens > count)
		{
			for (int i = 0; i < nbTokens - count; i++)
			{
				toolbar.add(new AncestorButton(), 0);
			}
		}
		int i = 0;
		int nb = list.size();

		for (String fileName : list)
		{
			VFS _vfs = VFSManager.getVFSForPath(fileName);
			boolean browseable = (_vfs.getCapabilities() & VFS.BROWSE_CAP) != 0;
			AncestorButton button = (AncestorButton) toolbar.getComponent(i);
			button.setAncestor(new Ancestor(view, fileName, _vfs.getFileName(fileName)));
			i++;
			button.setEnabled(browseable && nb != i);
		}
	} //}}}
}
