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

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class LCMOptions extends AbstractOptionPane
{
	static public final String PROP_PREFIX = "options.LCMPlugin.";
	public static final String PROVIDER_SERVICE_PROP = PROP_PREFIX + "provider";
	private JComboBox provider; 
	private DirtyLineProviderOptions [] providerOptions;
	private JPanel [] providerPanels;
	private JPanel currentPanel;

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
		providerOptions = new DirtyLineProviderOptions[services.length];
		providerPanels = new JPanel[services.length];
		providerChanged();
		provider.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				providerChanged();
			}
		});
	}

	private void providerChanged()
	{
		if (currentPanel != null)
			currentPanel.setVisible(false);
		int providerIndex = provider.getSelectedIndex();
		DirtyLineProviderOptions opts = providerOptions[providerIndex];
		if (opts == null)
		{
			String selectedName = provider.getSelectedItem().toString();
			DirtyLineProvider selProvider= (DirtyLineProvider)
				ServiceManager.getService(
					DirtyLineProvider.class.getCanonicalName(), selectedName);
			opts = providerOptions[providerIndex] = selProvider.getOptions();
			final Insets insets = new Insets(20,10,10,10);
			JPanel p = providerPanels[providerIndex] = new JPanel();
			TitledBorder border = new TitledBorder(jEdit.getProperty(
				"messages.LCMPlugin.providerSpecificOptions",
				new Object[] { selectedName }))
			{
				@Override
				public Insets getBorderInsets(Component c)
				{
					return insets;
				}
			};
			p.setBorder(border);
			opts.initOptions(p);
			addComponent(p);
		}
		currentPanel = providerPanels[providerIndex];
		currentPanel.setVisible(true);
	}

	@Override
	protected void _save()
	{
		jEdit.setProperty(PROVIDER_SERVICE_PROP,
			provider.getSelectedItem().toString());
		for (int i = 0; i < providerOptions.length; i++)
		{
			DirtyLineProviderOptions opts = providerOptions[i];
			if (opts != null)
				opts.saveOptions();
		}
	}

	public static String getProviderServiceName()
	{
		return jEdit.getProperty(PROVIDER_SERVICE_PROP);
	}
	public static void setProviderServiceName(String providerName)
	{
		jEdit.setProperty(PROVIDER_SERVICE_PROP, providerName);
	}
}
