/*
Copyright (C) 2006  Shlomy Reinstein

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

package flexdock;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowFactory;


public class Plugin extends EditPlugin {
	public static final String NAME = "Flexdock";
	public static final String OPTION_PREFIX = "options.Flexdock.";
	private static FlexDockWindowManager fdwm;
	
	public static void doSave() {
		if (fdwm == null)
			return;
		fdwm.save();
	}
	public static void doStart(final View view) {
		fdwm = new FlexDockWindowManager();
		fdwm.construct(view, DockableWindowFactory.getInstance(),
				view.getViewConfig());
		view.setDockableWindowManager(fdwm);
	}

}
