/*
 * SimpleOptions - Options for the "Simple" dirty line provider.
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
package lcm.providers.simple;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import lcm.DirtyLineProviderOptions;
import lcm.LCMOptions;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.ColorWellButton;

public class SimpleOptions implements DirtyLineProviderOptions
{
	public static final String PROP_PREFIX = LCMOptions.PROP_PREFIX;
	public static final String BG_COLOR_PROP = PROP_PREFIX + "bgColor";
	private ColorWellButton bgColor;

	public void initOptions(JPanel pane)
	{
		bgColor = new ColorWellButton(getBgColor());
		JPanel optionPanel = new JPanel();
		optionPanel.add(new JLabel(jEdit.getProperty("messages.LCMPlugin.bgColor")));
		optionPanel.add(bgColor);
		pane.add(optionPanel);
	}

	public void saveOptions()
	{
		jEdit.setColorProperty(BG_COLOR_PROP, bgColor.getSelectedColor());
	}

	public static Color getBgColor()
	{
		return jEdit.getColorProperty(BG_COLOR_PROP, Color.YELLOW);
	}

}
