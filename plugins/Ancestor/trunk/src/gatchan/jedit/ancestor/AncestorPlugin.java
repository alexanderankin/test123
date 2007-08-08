/*
 * AncestorPlugin.java - The Ancestor plugin
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class AncestorPlugin extends EBPlugin
{
	private final Map<View, AncestorToolBar> viewAncestorToolBar = new HashMap<View, AncestorToolBar>();

	public void start()
	{
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
		{
			addAncestorToolBar(views[i]);
		}
	}

	private void addAncestorToolBar(View view)
	{
		AncestorToolBar ancestorToolBar = new AncestorToolBar(view);
		EditPane editPane = view.getEditPane();
		ancestorToolBar.setBuffer(editPane.getBuffer());
		view.addToolBar(ancestorToolBar);
		viewAncestorToolBar.put(view, ancestorToolBar);
	}


	private void removeAncestorToolBar(View view)
	{
		AncestorToolBar toolBar = viewAncestorToolBar.get(view);
		view.removeToolBar(toolBar);
		viewAncestorToolBar.remove(view);
	}
	/**
	 * Handles a message sent on the EditBus.
	 */
	// next version: remove this
	public void handleMessage(EBMessage message)
	{
		if (message instanceof ViewUpdate)
		{
			ViewUpdate viewUpdate = (ViewUpdate) message;
			if (viewUpdate.getWhat() == ViewUpdate.CREATED)
			{
				final View view = viewUpdate.getView();
				addAncestorToolBar(view);
			}
			else if (viewUpdate.getWhat() == ViewUpdate.CLOSED)
			{
				viewAncestorToolBar.remove(viewUpdate.getView());
			}
		}
		if (message instanceof EditPaneUpdate)
		{
			EditPaneUpdate editPaneUpdate = (EditPaneUpdate) message;
			if (editPaneUpdate.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
			{
				EditPane editPane = editPaneUpdate.getEditPane();
				View view = editPane.getView();
				AncestorToolBar bar = viewAncestorToolBar.get(view);
				bar.setBuffer(editPane.getBuffer());
			}
		}
	}


	public void stop()
	{
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
		{
			removeAncestorToolBar(views[i]);
		}
	}
}
