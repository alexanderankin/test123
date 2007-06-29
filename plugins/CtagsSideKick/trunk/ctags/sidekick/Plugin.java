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

package ctags.sidekick;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import sidekick.SideKickPlugin;


public class Plugin extends EditPlugin {
	public static final String NAME = "CtagsSideKick";
	public static final String OPTION_PREFIX = "options.CtagsSideKick.";
	static final String CTAGS_MODE_OPTIONS = "options.CtagsSideKick.mode.ctags_options";

	public static void groupBy(View view, String mapper)
	{
		jEdit.setProperty(OptionPane.MAPPER, mapper);
		String mode = view.getBuffer().getMode().getName();
		ModeOptionsPane.setProperty(mode, OptionPane.MAPPER, mapper);
		SideKickPlugin.parse(view, true);
	}
	public static void setSorting(View view, boolean sort, boolean foldsFirst)
	{
		jEdit.setBooleanProperty(OptionPane.SORT, sort);
		if (sort)
			jEdit.setBooleanProperty(OptionPane.FOLDS_BEFORE_LEAFS, foldsFirst);
		SideKickPlugin.parse(view, true);
	}
}
