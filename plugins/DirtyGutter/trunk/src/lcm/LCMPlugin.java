/*
 * LCMPlugin - A plugin for marking changed lines in the gutter.
 *
 * Copyright (C) 2009 Shlomy Reinstein
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

package lcm;

import java.util.HashMap;
import java.util.Vector;

import lcm.providers.simple.SimpleDirtyLineProvider;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;



public class LCMPlugin extends EBPlugin
{
	static public final String PROP_PREFIX = LCMOptions.PROP_PREFIX;
	static private final String DEBUGGING_PROP = PROP_PREFIX + "debug";
	static private LCMPlugin instance;
	private HashMap<Buffer, BufferHandler> handlers;
	private HashMap<EditPane, ChangeMarker> markers;
	private boolean isDebugging;
	private DirtyLineProvider provider;

	public static LCMPlugin getInstance()
	{
		return instance;
	}

	public BufferHandler getBufferHandler(Buffer b)
	{
		synchronized(handlers)
		{
			return handlers.get(b);
		}
	}

	private BufferHandler attachToBuffer(Buffer b)
	{
		BufferHandler bh = provider.attach(b);
		synchronized(handlers)
		{
			handlers.put(b, bh);
		}
		return bh;
	}

	private void detachFromBuffer(Buffer b)
	{
		synchronized(handlers)
		{
			BufferHandler bh = handlers.remove(b);
			if (bh != null)
				provider.detach(b, bh);
		}
	}
	
	@Override
	public void handleMessage(EBMessage message)
	{
		if (message instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate) message;
			EditPane ep = epu.getEditPane();
			if ((epu.getWhat() == EditPaneUpdate.CREATED) ||
				(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED))
			{
				initEditPane(ep);
			}
			else if (epu.getWhat() == EditPaneUpdate.DESTROYED)
				uninitEditPane(ep);
		}
		else if (message instanceof BufferUpdate)
		{
			BufferUpdate bu = (BufferUpdate) message;
			Buffer b = bu.getBuffer();
			if ((bu.getWhat() == BufferUpdate.SAVED) ||
				(bu.getWhat() == BufferUpdate.LOADED))
			{
				BufferHandler bh = getBufferHandler(b);
				if (bh != null)
					bh.bufferSaved(b);
			}
			else if (bu.getWhat() == BufferUpdate.CLOSED)
				detachFromBuffer(b);
		}
		else if (message instanceof PropertiesChanged)
			isDebugging = jEdit.getBooleanProperty(DEBUGGING_PROP, false);
	}

	public void repaintAllTextAreas()
	{
		jEdit.visit(new JEditVisitorAdapter() {
			@Override
			public void visit(JEditTextArea textArea) {
				textArea.getGutter().repaint();
			}
		});
	}

	public boolean isDebugging()
	{
		return isDebugging;
	}

	private class EditPaneVisitor extends JEditVisitorAdapter
	{
		boolean start;
		
		public EditPaneVisitor(boolean start)
		{
			this.start = start;
		}
		
		public void visit(EditPane editPane) {
			if (start)
				initEditPane(editPane);
			else
				uninitEditPane(editPane);
		}
	}
	
	private void initEditPane(EditPane ep)
	{
		Buffer b = ep.getBuffer();
		if (getBufferHandler(b) == null)
			attachToBuffer(b);
		ChangeMarker cm = markers.get(ep);
		if (cm == null)
		{
			cm = new ChangeMarker(ep);
			markers.put(ep, cm);
		}
	}

	private void uninitEditPane(EditPane ep)
	{
		ChangeMarker cm = markers.get(ep);
		if (cm != null)
		{
			cm.remove();
			markers.remove(ep);
		}
	}

	public DirtyLineProvider getDirtyLineProvider()
	{
		provider = null;
		String providerName = LCMOptions.getProviderServiceName();
		if (providerName != null)
		{
			provider = (DirtyLineProvider) ServiceManager.getService(
				DirtyLineProvider.class.getCanonicalName(), providerName);
		}
		if (provider == null)
			provider = new SimpleDirtyLineProvider();
		return provider;
	}

	@Override
	public void start()
	{
		instance = this;
		provider = getDirtyLineProvider();
		handlers = new HashMap<Buffer, BufferHandler>();
		markers = new HashMap<EditPane, ChangeMarker>();
		isDebugging = jEdit.getBooleanProperty(DEBUGGING_PROP, false);
		jEdit.visit(new EditPaneVisitor(true));
	}

	@Override
	public void stop()
	{
		Vector<EditPane> editPanes = new Vector<EditPane>(markers.keySet());
		for (EditPane ep: editPanes)
			uninitEditPane(ep);
		markers.clear();
		markers = null;
		Vector<Buffer> buffers = new Vector<Buffer>(handlers.keySet());
		for (Buffer b: buffers)
			detachFromBuffer(b);
		handlers.clear();
		handlers = null;
		instance = null;
	}

	public void toggle(EditPane ep)
	{
		if (markers.containsKey(ep))
			uninitEditPane(ep);
		else
			initEditPane(ep);
	}
	public boolean isEnabled(EditPane ep)
	{
		return markers.containsKey(ep);
	}
}
