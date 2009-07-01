/*
 * Copyright (c) 2009, Romain Francois <francoisromain@free.fr>
 *
 * This file is part of the InfoNodePlugin plugin for jedit
 *
 * The InfoNodePlugin plugin for jedit is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * The InfoNodePlugin plugin for jedit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the InfoNodePlugin plugin for jedit. If not, see <http://www.gnu.org/licenses/>.
 */

package com.addictedtor.infonode;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.DockingFrameworkProvider;
import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;

public class InfoNodeProvider implements DockingFrameworkProvider {

	public DockableWindowManager create(View view,
			DockableWindowFactory instance, ViewConfig config) {
		return new InfoNodeDockingWindowManager(view, instance, config);
	}

	public DockingLayout createDockingLayout() {
		return new InfoNodeDockingLayout();
	}
}
