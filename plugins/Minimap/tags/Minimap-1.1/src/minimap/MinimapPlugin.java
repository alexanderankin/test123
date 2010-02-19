/*
Copyright (C) 2009  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package minimap;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Timer;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;

public class MinimapPlugin extends EBPlugin {

	private Timer foldCheckTimer;

	@Override
	public void handleMessage(EBMessage message) {
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate msg = (EditPaneUpdate) message;
			if (msg.getWhat() == EditPaneUpdate.CREATED) {
				if (Options.getAutoProp())
					show((EditPane) msg.getSource());
			} else if (msg.getWhat() == EditPaneUpdate.DESTROYED)
				hide((EditPane) msg.getSource(), false);
		} else if (message instanceof PropertiesChanged) {
			if (Options.getAutoProp())
				showAll();
			foldCheckTimer.setDelay(Options.getTimeProp());
			for (Minimap map: maps.values())
				map.propertiesChanged();
		}
	}

	private static Map<EditPane, Minimap> maps;

	// restore - whether to restore the previous edit pane child
	private static void hide(EditPane editPane, boolean restore) {
		if (! maps.containsKey(editPane))
			return;
		Minimap map = maps.get(editPane);
		map.stop(restore);
		maps.remove(editPane);
	}

	@Override
	public void stop() {
		foldCheckTimer.stop();
		foldCheckTimer = null;
		hideAll();
		maps = null;
	}

	@SuppressWarnings("serial")
	@Override
	public void start() {
		maps = new HashMap<EditPane, Minimap>();
		foldCheckTimer = new Timer(Options.getTimeProp(), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				for (Minimap map: maps.values())
					map.updateFolds();
			}
		});
		foldCheckTimer.setRepeats(true);
		foldCheckTimer.start();
	}

	// Action interface (for actions.xml)

	public static void showAll() {
		jEdit.visit(new JEditVisitorAdapter() {
			@Override
			public void visit(EditPane editPane) {
				show(editPane);
			}
		});
	}

	public static void show(final EditPane editPane) {
		if (maps.containsKey(editPane))
			return;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Minimap map = new Minimap(editPane);
				map.start();
				maps.put(editPane, map);
			}
		});
	}

	public static void hideAll() {
		Set<EditPane> editPanes = new HashSet<EditPane>();
		for (EditPane ep: maps.keySet())
			editPanes.add(ep);
		for (EditPane ep: editPanes)
			hide(ep);
	}

	public static void hide(EditPane editPane) {
		hide(editPane, true);
	}

	public static void toggle(EditPane editPane) {
		if (maps.containsKey(editPane))
			hide(editPane);
		else
			show(editPane);
	}
}
