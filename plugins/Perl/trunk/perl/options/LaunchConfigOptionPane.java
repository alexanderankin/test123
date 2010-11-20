/*
Copyright (C) 2007  Shlomy Reinstein

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

package perl.options;
import perl.launch.LaunchConfiguration;
import perl.launch.LaunchConfigurationManager;

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

import debugger.jedit.Plugin;


/** ************************************************************************** */
public class LaunchConfigOptionPane extends AbstractOptionPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***************************************************************************
	 * Vars
	 **************************************************************************/

	private LaunchConfigurationManager configs;
	
	private JList configurationsList;
	private DefaultListModel configurationsModel;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String CONFIGURATIONS_LABEL = PREFIX + "configurations_label";
	static final String CONFIGURATION_LABEL = PREFIX + "configuration_label";
	static final String PROGRAM_LABEL = PREFIX + "program_label";
	static final String ARGUMENTS_LABEL = PREFIX + "arguments_label";
	static final String DIRECTORY_LABEL = PREFIX + "directory_label";
	static final String ENVIRONMENT_LABEL = PREFIX + "environment_label";

	private LaunchConfiguration currentConfig = null;

	private JButton delete;

	private JButton add;

	private JButton makeDefault;

	private JButton edit;
	
	private JButton copy;

	/***************************************************************************
	 * Factory methods
	 **************************************************************************/
	@SuppressWarnings("serial")
	public LaunchConfigOptionPane()
	{
		super("debugger.programs");
		configs = LaunchConfigurationManager.getInstance();
		setBorder(new EmptyBorder(5, 5, 5, 5));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = gbc.gridy = 0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(new JLabel(jEdit.getProperty(CONFIGURATIONS_LABEL)), gbc);
		configurationsModel = new DefaultListModel();
		configurationsList = new JList(configurationsModel);
		configurationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		configurationsList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list,
						value, index, isSelected, cellHasFocus);
				String configuration = configs.getName(index);
				String append = (index == configs.getDefaultIndex()) ?
						" *" : "";
				label.setText(configuration + append);
				return label;
			}
		});
		gbc.gridy++;
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(configurationsList), gbc);

		JPanel buttons = new JPanel();
		add = new JButton("New");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewConfiguration();
			}
		});
		buttons.add(add);
		copy = new JButton("Duplicate");
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				duplicateSelectedConfiguration();
			}
		});
		buttons.add(copy);
		delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedConfiguration();
			}
		});
		buttons.add(delete);
		edit = new JButton("Edit");
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSelectedConfiguration();
			}
		});
		buttons.add(edit);
		makeDefault = new JButton("Make default");
		makeDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeSelectedConfigurationDefault();
			}
		});
		buttons.add(makeDefault);
		gbc.gridy++;
		gbc.weightx = gbc.weighty = 0.0;
		add(buttons, gbc);
		
		// Finally, initialize the list of configurations
		for (int i = 0; i < configs.size(); i++)
			configurationsModel.addElement(configs.getName(i));
		
		if (configs.size() > 0) {
			int index = configs.getDefaultIndex();
			if (index >= 0 && index < configs.size())
				configurationsList.setSelectedIndex(index);
		}
	}
	
	private void makeSelectedConfigurationDefault()
	{
		int prevDefault = configs.getDefaultIndex();
		int defaultIndex = configurationsList.getSelectedIndex();
		configs.setDefaultIndex(defaultIndex);
		// These are required for updating the display
		if (prevDefault >= 0 && prevDefault < configs.size())
			configurationsModel.set(prevDefault, configs.getName(prevDefault));
		if (defaultIndex >= 0 && defaultIndex < configs.size())
			configurationsModel.set(defaultIndex, configs.getName(defaultIndex));
	}
	private void deleteSelectedConfiguration()
	{
		int index = configurationsList.getSelectedIndex();
		configs.remove(index);
		Vector<String> configNames = configs.getNames();
		configurationsList.setListData(configNames);
		if (! configNames.isEmpty()) {
			if (index == configNames.size())
				index--;
			configurationsList.setSelectedIndex(index);
		}
	}
	private void editSelectedConfiguration()
	{
		int index = configurationsList.getSelectedIndex();
		if (index > -1) {
			currentConfig = configs.getByIndex(index);
			LaunchConfigEditor d = new LaunchConfigEditor(GUIUtilities.getParentDialog(this), currentConfig);
			d.setVisible(true);
		}
	}
	private void createConfiguration(LaunchConfiguration config)
	{
		LaunchConfigEditor d = new LaunchConfigEditor(GUIUtilities.getParentDialog(this), config);
		d.setVisible(true);
		if (! d.accepted())
			return;
		configs.add(config);
		configurationsModel.addElement(config);
		configurationsList.setSelectedIndex(configs.size() - 1);
	}
	private void duplicateSelectedConfiguration()
	{
		int index = configurationsList.getSelectedIndex();
		if (index > -1) {
			currentConfig = configs.getByIndex(index);
			LaunchConfiguration newConfig = currentConfig.createDuplicate();
			newConfig.setName(configs.getNewName(currentConfig.getName()));
			createConfiguration(newConfig);
		}
	}
	private void createNewConfiguration()
	{
		LaunchConfiguration newConfig =
			new LaunchConfiguration("Unnamed", "", "", "", "");
		createConfiguration(newConfig);
	}
	
	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void save()
	{
		configs.save();
	}
}
/** ***********************************************************************EOF */

