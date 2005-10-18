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

import sidekick.SourceTree;

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
	
	//{{{ method gotoDockable
	public static void gotoDockable(View view) {
		DockableWindowManager wm = view.getDockableWindowManager();
		SourceTree tree = (SourceTree) (wm.getDockable("sidekick-source-tree"));
		if (tree == null) {
			wm.addDockableWindow("sidekick-source-tree");
			tree = (SourceTree) (wm.getDockable("sidekick-source-tree"));
			}
		wm.showDockableWindow("sidekick-source-tree");
		tree.requestFocus();
	} //}}}

}

