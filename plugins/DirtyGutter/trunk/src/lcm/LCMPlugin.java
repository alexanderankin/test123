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

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
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
	private HashMap<Buffer, BufferChangedLines> changes;
	private HashMap<EditPane, ChangeMarker> markers;
	private boolean isDebugging;

	public static LCMPlugin getInstance()
	{
		return instance;
	}

	public BufferChangedLines getBufferChangedLines(Buffer b)
	{
		synchronized(changes)
		{
			return changes.get(b);
		}
	}

	private BufferChangedLines createBufferChangedLines(Buffer b)
	{
		BufferChangedLines bcl = new BufferChangedLines(b);
		synchronized(changes)
		{
			changes.put(b, bcl);
		}
		return bcl;
	}

	private void removeBufferChangedLines(Buffer b)
	{
		synchronized(changes)
		{
			BufferChangedLines bcl = changes.remove(b);
			if (bcl != null)
				bcl.remove();
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
				BufferChangedLines bcl = getBufferChangedLines(b);
				if (bcl != null)
					bcl.clear();
			}
			else if (bu.getWhat() == BufferUpdate.CLOSED)
				removeBufferChangedLines(b);
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
		if (getBufferChangedLines(b) == null)
			createBufferChangedLines(b);
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

	@Override
	public void start()
	{
		instance = this;
		changes = new HashMap<Buffer, BufferChangedLines>();
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
		Vector<Buffer> buffers = new Vector<Buffer>(changes.keySet());
		for (Buffer b: buffers)
			removeBufferChangedLines(b);
		changes.clear();
		changes = null;
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
