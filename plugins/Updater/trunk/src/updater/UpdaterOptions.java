/*
 * UpdaterOptions.java - The updater option pane.
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
package updater;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class UpdaterOptions extends AbstractOptionPane
{
	private static final String UPDATE_ON_STARTUP_PROP = "updater.values.updateOnStartup";
	private static final String UPDATE_EVERY_PROP = "updater.values.updateEvery";
	private static final String UPDATE_PERIOD_PROP = "updater.values.updatePeriod";
	private JCheckBox updateOnStartup;
	private JCheckBox updateEvery;
	private JSpinner updatePeriod;

	public UpdaterOptions()
	{
		super("updater");
	}

	@Override
	protected void _init()
	{
		updateOnStartup = new JCheckBox(jEdit.getProperty(
			"updater.options.updateOnStartup"), isUpdateOnStartup());
		addComponent(updateOnStartup);
		JPanel updateEveryPanel = new JPanel();
		updateEvery = new JCheckBox(jEdit.getProperty(
			"updater.options.updateEvery"), (getUpdateEvery() != 0));
		updateEveryPanel.add(updateEvery);
		SpinnerModel model = new SpinnerNumberModel(getUpdatePeriod(), 1, 30, 1);
		updatePeriod = new JSpinner(model);
		updateEveryPanel.add(updatePeriod);
		updateEveryPanel.add(new JLabel(jEdit.getProperty("updater.options.updatePeriod")));
		addComponent(updateEveryPanel);
		updateEvery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				updatePeriod.setEnabled(updateEvery.isSelected());
			}
		});
	}

	@Override
	protected void _save()
	{
		jEdit.setBooleanProperty(UPDATE_ON_STARTUP_PROP,
			updateOnStartup.isSelected());
		jEdit.setBooleanProperty(UPDATE_EVERY_PROP,
			updateEvery.isSelected());
		jEdit.setIntegerProperty(UPDATE_PERIOD_PROP,
			Integer.valueOf(updatePeriod.getValue().toString()));
	}

	public boolean isUpdateOnStartup()
	{
		return jEdit.getBooleanProperty(UPDATE_ON_STARTUP_PROP, false);
	}

	public int getUpdateEvery()
	{
		return jEdit.getIntegerProperty(UPDATE_EVERY_PROP, 0);
	}

	public int getUpdatePeriod()
	{
		return jEdit.getIntegerProperty(UPDATE_PERIOD_PROP, 0);
	}
}
