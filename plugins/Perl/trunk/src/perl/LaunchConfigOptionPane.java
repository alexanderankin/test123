/*
Copyright (C) 2010  Shlomy Reinstein

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

package perl;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class LaunchConfigOptionPane extends AbstractOptionPane
{
	static final String MSG = Plugin.MESSAGE_PREFIX;
	private LaunchConfigManager manager;
	private JList list;
	private DefaultListModel model;
	private JButton delete;
	private JButton add;
	private JButton makeDefault;
	private JButton edit;
	private JButton copy;

	/***************************************************************************
	 * Factory methods
	 **************************************************************************/
	public LaunchConfigOptionPane()
	{
		super("perl-dbg-configs");
		manager = LaunchConfigManager.getInstance();
		setBorder(new EmptyBorder(5, 5, 5, 5));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = gbc.gridy = 0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(new JLabel(jEdit.getProperty(MSG + "configurations")), gbc);
		model = new DefaultListModel();
		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
				LaunchConfig config = (LaunchConfig) value;
				String append = (config == manager.getDefault()) ?
						" *" : "";
				setText(config.name + append);
				return this;
			}
		});
		gbc.gridy++;
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(list), gbc);

		JPanel buttons = new JPanel();
		add = new JButton("New");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNew();
			}
		});
		buttons.add(add);
		copy = new JButton("Duplicate");
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				duplicateSelected();
			}
		});
		buttons.add(copy);
		delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelected();
			}
		});
		buttons.add(delete);
		edit = new JButton("Edit");
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSelected();
			}
		});
		buttons.add(edit);
		makeDefault = new JButton("Make default");
		makeDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeSelectedDefault();
			}
		});
		buttons.add(makeDefault);
		gbc.gridy++;
		gbc.weightx = gbc.weighty = 0.0;
		add(buttons, gbc);
		
		// Finally, initialize the list of configurations
		Vector<LaunchConfig> configs = manager.get();
		for (LaunchConfig config: configs)
			model.addElement(config);
		
		LaunchConfig def = manager.getDefault();
		if (def != null)
			list.setSelectedValue(def, true);
	}
	
	private void makeSelectedDefault()
	{
		LaunchConfig sel = (LaunchConfig) list.getSelectedValue();
		if (sel == null)
			return;
		manager.setDefault(sel);
		// The following is needed for refreshing the list
		int index = list.getSelectedIndex();
		model.remove(index);
		model.add(index, sel);
	}
	private void deleteSelected()
	{
		int index = list.getSelectedIndex();
		if (index == -1)
			return;
		LaunchConfig sel = (LaunchConfig) list.getSelectedValue();
		manager.remove(sel);
		model.removeElement(sel);
		if (index < model.getSize())
			list.setSelectedIndex(index);
	}
	private void editSelected()
	{
		int index = list.getSelectedIndex();
		if (index == -1)
			return;
		LaunchConfig sel = (LaunchConfig) list.getSelectedValue();
		LaunchConfigEditor d = new LaunchConfigEditor(GUIUtilities.getParentDialog(this), sel);
		d.setVisible(true);
	}
	private void createConfiguration(LaunchConfig config)
	{
		LaunchConfigEditor d = new LaunchConfigEditor(GUIUtilities.getParentDialog(this), config);
		d.setVisible(true);
		if (! d.accepted())
			return;
		manager.add(config);
		model.addElement(config);
		list.setSelectedIndex(model.size() - 1);
	}
	private void duplicateSelected()
	{
		int index = list.getSelectedIndex();
		if (index == -1)
			return;
		LaunchConfig sel = (LaunchConfig) list.getSelectedValue();
		LaunchConfig newConfig = sel.duplicate();
		newConfig.name = manager.getNewName(sel.name);
		createConfiguration(newConfig);
	}
	private void createNew()
	{
		LaunchConfig newConfig = new LaunchConfig("Unnamed", "", "");
		createConfiguration(newConfig);
	}
	
	public void save()
	{
		manager.save();
	}
}

