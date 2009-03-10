/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

*/

package ctags.sidekick.options;

import sidekick.ModeOptionPane;


@SuppressWarnings("serial")
public class TreeStyleOptionPane extends AbstractModeOptionPane {

	public TreeStyleOptionPane() {
		super("CtagsSideKick-tree-style");
	}

	@Override
	protected ModeOptionPane addOptionPane() {
		ModeOptionsPane pane = new ModeOptionsPane();
		addComponent(pane);
		return pane;
	}
}
/** ***********************************************************************EOF */

