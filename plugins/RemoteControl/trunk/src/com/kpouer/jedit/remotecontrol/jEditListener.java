/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
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

package com.kpouer.jedit.remotecontrol;

import com.kpouer.jedit.remotecontrol.xstream.BufferSetMessage;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.bufferset.BufferSet;
import org.gjt.sp.jedit.bufferset.BufferSetListener;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class jEditListener implements EBComponent
{
	private final Map<View, String> views;
	private final Map<EditPane, String> editPanes;
	private final Map<String, View> viewById;
	private final Map<String, EditPane> editPaneById;
	private final Map<EditPane, BufferSetListener> editPanesListeners;
	private int indexCounter;
	private final RemoteServer server;


	jEditListener(RemoteServer remoteServer)
	{
		this.server = remoteServer;
		views = new HashMap<View, String>();
		editPanes = new HashMap<EditPane, String>();
		editPanesListeners = new HashMap<EditPane, BufferSetListener>();
		viewById = new HashMap<String, View>();
		editPaneById = new HashMap<String, EditPane>();
	}

	void start()
	{
		jEdit.visit(new JEditVisitorAdapter()
		{
			@Override
			public void visit(View view)
			{
				addView(view);
			}

			@Override
			public void visit(EditPane editPane)
			{
				addEditPane(editPane);
			}
		});
		EditBus.addToBus(this);
	}

	void stop()
	{
		EditBus.removeFromBus(this);
		views.clear();
		editPanes.clear();
		viewById.clear();
		editPaneById.clear();
		for (Map.Entry<EditPane, BufferSetListener> entry : editPanesListeners.entrySet())
		{
			EditPane editPane = entry.getKey();
			BufferSet bufferSet = editPane.getBufferSet();
			bufferSet.removeBufferSetListener(entry.getValue());
		}
		editPanesListeners.clear();
	}

	private void addView(View view)
	{
		String id = getNextViewId();
		views.put(view, id);
		viewById.put(id, view);
	}

	private void removeView(View view)
	{
		String id = views.remove(view);
		viewById.remove(id);
	}

	private void addEditPane(EditPane editPane)
	{
		String id = getNextEditPaneId();
		editPaneById.put(id, editPane);
		editPanes.put(editPane, id);
		BufferSetListener listener = new MyBufferSetListener(editPane);
		BufferSet bufferSet = editPane.getBufferSet();
		bufferSet.addBufferSetListener(listener);
		editPanesListeners.put(editPane, listener);
	}

	private void removeEditPane(EditPane editPane)
	{
		BufferSetListener removed = editPanesListeners.remove(editPane);
		BufferSet bufferSet = editPane.getBufferSet();
		bufferSet.removeBufferSetListener(removed);
		String id = editPanes.remove(editPane);
		editPaneById.remove(id);
	}

	@Override
	public void handleMessage(EBMessage message)
	{
		if (message instanceof ViewUpdate)
		{
			handleViewUpdate((ViewUpdate) message);
		}
		else if (message instanceof EditPaneUpdate)
		{
			handleEditPaneUpdate((EditPaneUpdate) message);
		}
		else
		{
			server.dispatchMessage(message);
		}
	}

	private void handleEditPaneUpdate(EditPaneUpdate message)
	{
		Object what = message.getWhat();
		EditPane editPane = message.getEditPane();
		if (what == EditPaneUpdate.CREATED)
		{
			addEditPane(editPane);
			server.dispatchMessage(message);
		}
		else if (what == EditPaneUpdate.DESTROYED)
		{
			server.dispatchMessage(message);
			removeEditPane(editPane);
		}
		else
		{
			server.dispatchMessage(message);
		}
	}

	private void handleViewUpdate(ViewUpdate viewUpdate)
	{
		Object what = viewUpdate.getWhat();
		View view = viewUpdate.getView();
		if (what == ViewUpdate.CREATED)
		{
			addView(view);
			server.dispatchMessage(viewUpdate);
		}
		else if (what == ViewUpdate.CLOSED)
		{
			server.dispatchMessage(viewUpdate);
			removeView(view);
		}
		else
		{
			server.dispatchMessage(viewUpdate);
		}
	}

	private String getNextViewId()
	{
		return "view-" + indexCounter++;
	}

	private String getNextEditPaneId()
	{
		return "editpane-" + indexCounter++;
	}

	public String getViewId(Object obj)
	{
		return views.get(obj);
	}

	public String getEditPaneId(Object obj)
	{
		return editPanes.get(obj);
	}

	public EditPane getEditPane(String id)
	{
		return editPaneById.get(id);
	}

	public View getView(String id)
	{
		return viewById.get(id);
	}

	private class MyBufferSetListener implements BufferSetListener
	{
		private final EditPane editPane;

		private MyBufferSetListener(EditPane editPane)
		{
			this.editPane = editPane;
		}

		@Override
		public void bufferAdded(Buffer buffer, int index)
		{
			server.dispatchMessage(new BufferSetMessage(editPane, buffer, index, BufferSetMessage.BUFFER_ADDED));
		}

		@Override
		public void bufferRemoved(Buffer buffer, int index)
		{
			server.dispatchMessage(new BufferSetMessage(editPane, buffer, index, BufferSetMessage.BUFFER_REMOVED));
		}

		@Override
		public void bufferMoved(Buffer buffer, int oldIndex, int newIndex)
		{
			server.dispatchMessage(new BufferSetMessage(editPane, buffer, oldIndex, newIndex, BufferSetMessage.BUFFER_MOVED));
		}

		@Override
		public void bufferSetSorted()
		{
			server.dispatchMessage(new BufferSetMessage(editPane, BufferSetMessage.BUFFERSET_SORTED));
		}
	}
}
