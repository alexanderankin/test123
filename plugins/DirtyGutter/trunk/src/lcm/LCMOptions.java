/*
 * LCMOptions - The plugin's option pane.
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

package lcm;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.ColorWellButton;

@SuppressWarnings("serial")
public class LCMOptions extends AbstractOptionPane
{
	static public final String PROP_PREFIX = "options.LCMPlugin.";
	public static final String BG_COLOR_PROP = PROP_PREFIX + "bgColor";
	public static final String PROVIDER_SERVICE_PROP = PROP_PREFIX + "provider";
	private JComboBox provider; 
	private ColorWellButton bgColor;

	public LCMOptions()
	{
		super("DirtyGutter");
	}

	@Override
	protected void _init()
	{
		String [] services = ServiceManager.getServiceNames(
			DirtyLineProvider.class.getCanonicalName());
		provider = new JComboBox(services);
		String selected = getProviderServiceName();
		for (int i = 0; i < services.length; i++)
		{
			if (services[i].equals(selected))
			{
				provider.setSelectedIndex(i);
				break;
			}
		}
		addComponent(jEdit.getProperty("messages.LCMPlugin.providers"),
			provider);
		bgColor = new ColorWellButton(getBgColor());
		addComponent(jEdit.getProperty("messages.LCMPlugin.bgColor"), bgColor,
			GridBagConstraints.VERTICAL);
	}

	@Override
	protected void _save()
	{
		jEdit.setProperty(PROVIDER_SERVICE_PROP,
			provider.getSelectedItem().toString());
		jEdit.setColorProperty(BG_COLOR_PROP, bgColor.getSelectedColor());
	}

	public static Color getBgColor()
	{
		return jEdit.getColorProperty(BG_COLOR_PROP, Color.YELLOW);
	}

	public static String getProviderServiceName()
	{
		return jEdit.getProperty(PROVIDER_SERVICE_PROP);
	}
}
