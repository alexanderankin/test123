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

import java.util.Map;
import java.util.HashMap;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

public class MinimapPlugin extends EditPlugin {

	private static Map<EditPane, Minimap> maps;

	public void stop() {
		maps = null;
	}

	public void start() {
		maps = new HashMap<EditPane, Minimap>();
	}
	
	public static void show(View view) {
		EditPane editPane = view.getEditPane();
		if (maps.containsKey(editPane))
			return;
		Minimap map = new Minimap(editPane);
		map.start();
		maps.put(editPane, map);
	}
	
	public static void hide(View view) {
		EditPane editPane = view.getEditPane();
		if (! maps.containsKey(editPane))
			return;
		Minimap map = maps.get(editPane);
		map.stop();
	}

}
