/*
 * PerlSideKickPlugin.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 by Martin Raspe
 * (hertzhaft@biblhertz.it)
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
package sidekick.perl;

import java.util.*;
import org.gjt.sp.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;

import sidekick.PerlSideKickTree;

/**
 * Description of the Class
 *
 * @author     Martin Raspe
 * @created    Nov 20, 2004
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */
public class PerlSideKickPlugin extends EditPlugin {
	public final static String NAME = "sidekick.perl";
	public final static String OPTION_PREFIX = "options.sidekick.perl.";
	public final static String PROPERTY_PREFIX = "plugin.sidekick.perl.";
	private static boolean _showMarkers = true;
	
	//{{{ method getDockable
	public static void gotoDockable(View view) {
		DockableWindowManager wm = view.getDockableWindowManager();
		PerlSideKickTree tree = (PerlSideKickTree) (wm.getDockable("perlsidekick-tree"));
		if (tree == null) {
			wm.addDockableWindow("perlsidekick-tree");
			tree = (PerlSideKickTree) (wm.getDockable("perlsidekick-tree"));
			}
		wm.showDockableWindow("perlsidekick-tree");
		tree.requestFocus();
	} //}}}

	//{{{ method toggleMarkersFlag
	public static void toggleMarkersFlag() {
		_showMarkers = ! _showMarkers;
	} //}}}

	//{{{ method isRegisteredDockable
	public static boolean isRegisteredDockable(String name) {
		// see if the dockable "name" is registered
		String[] dockables = DockableWindowFactory.getInstance()
			.getRegisteredDockableWindows();
		boolean _found = false;
		for(int i = 0; i < dockables.length; i++) {
		if (dockables[i].equals(name)) {
			_found = true;
			break;
			}
		}	
		return _found;
	} //}}}

	//{{{ method isMarkersFlagSet
	public static boolean isMarkersFlagSet() {
		// should marked routines be shown in structure tree?
		return _showMarkers;
	} //}}}

	//{{{ method unloadSideKickTree
	public static void unloadSideKickTree(View view) {
		// unload the SideKick structure browser - we are the substitute
		EditPlugin _sk = jEdit.getPlugin("sidekick.SideKickPlugin");
		if (_sk == null) {
			view.getStatus().setMessageAndClear("SideKick plugin not loaded");
			return;
			}
		DockableWindowFactory.getInstance().unloadDockableWindows(_sk.getPluginJAR());
		view.getStatus().setMessageAndClear("SideKick browser unloaded");
		gotoDockable(view);
	} //}}}
	
}

