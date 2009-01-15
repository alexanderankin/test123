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

import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;

public class MinimapPlugin extends EBPlugin {

	@Override
	public void handleMessage(EBMessage message) {
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate msg = (EditPaneUpdate) message;
			if (msg.getWhat() == EditPaneUpdate.CREATED) {
				if (Options.getAutoProp())
					show((EditPane) msg.getSource());
			} else if (msg.getWhat() == EditPaneUpdate.DESTROYED)
				hide((EditPane) msg.getSource());
		} else if (message instanceof PropertiesChanged) {
			if (Options.getAutoProp())
				showAll();
		}
	}

	private static Map<EditPane, Minimap> maps;

	public void stop() {
		maps = null;
	}

	public void start() {
		maps = new HashMap<EditPane, Minimap>();
	}
	
	public static void showAll() {
		jEdit.visit(new JEditVisitorAdapter() {
			@Override
			public void visit(EditPane editPane) {
				show(editPane);
			}
		});
	}

	public static void show(EditPane editPane) {
		if (maps.containsKey(editPane))
			return;
		Minimap map = new Minimap(editPane);
		map.start();
		maps.put(editPane, map);
	}
	
	public static void hideAll() {
		for (EditPane ep: maps.keySet())
			hide(ep);
	}
	
	public static void hide(EditPane editPane) {
		if (! maps.containsKey(editPane))
			return;
		Minimap map = maps.get(editPane);
		map.stop();
		maps.remove(editPane);
	}

}
