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

Note: The code for the ctags path and the ctags version notice
was taken from the CodeBrowser plugin by Gerd Knops. 
*/

package gdb.options;
import gdb.launch.LaunchConfiguration;
import gdb.launch.LaunchConfigurationManager;

import java.awt.Component;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import common.gui.FileTextField;

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
	private JTextField configurationTF;
	private FileTextField programTF;
	private JTextField argumentsTF;
	private JTextField directoryTF;
	private JTextField environmentTF;
	
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

	private JButton update;

	/***************************************************************************
	 * Factory methods
	 **************************************************************************/
	@SuppressWarnings("serial")
	public LaunchConfigOptionPane()
	{
		super("debugger.programs");
		configs = LaunchConfigurationManager.getInstance();
		setBorder(new EmptyBorder(5, 5, 5, 5));

		configurationsModel = new DefaultListModel();
		configurationsList = new JList(configurationsModel);
		configurationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		configurationsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				updateTextFields();
			}
		});
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
		addComponent(jEdit.getProperty(CONFIGURATIONS_LABEL),
				new JScrollPane(configurationsList));

		JPanel buttons = new JPanel();
		add = new JButton("New");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewConfiguration();
			}
		});
		buttons.add(add);
		delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedConfiguration();
			}
		});
		buttons.add(delete);
		makeDefault = new JButton("Make default");
		makeDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeSelectedConfigurationDefault();
			}
		});
		buttons.add(makeDefault);
		addComponent(buttons);
		
		addSeparator();
		configurationTF = new JTextField(40);
		addComponent(jEdit.getProperty(CONFIGURATION_LABEL), configurationTF);
		programTF = new FileTextField(true);
		addComponent(jEdit.getProperty(PROGRAM_LABEL), programTF);
		argumentsTF = new JTextField(40);
		addComponent(jEdit.getProperty(ARGUMENTS_LABEL), argumentsTF);
		directoryTF = new JTextField(40);
		addComponent(jEdit.getProperty(DIRECTORY_LABEL), directoryTF);
		environmentTF = new JTextField(40);
		addComponent(jEdit.getProperty(ENVIRONMENT_LABEL), environmentTF);
		
		update = new JButton("Update");
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSelectedConfiguration();
			}
		});
		addComponent(update);
		
		// Finally, initialize the list of configurations
		for (int i = 0; i < configs.size(); i++)
			configurationsModel.addElement(configs.getName(i));
		
		if (configs.size() > 0) {
			configurationsList.setSelectedIndex(configs.getDefaultIndex());
		} else {
			update.setEnabled(false);
		}
	}
	
	private void makeSelectedConfigurationDefault()
	{
		int prevDefault = configs.getDefaultIndex();
		int defaultIndex = configurationsList.getSelectedIndex();
		configs.setDefaultIndex(defaultIndex);
		// These are required for updating the display
		configurationsModel.set(prevDefault, configs.getName(prevDefault));
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
		updateTextFields();
	}
	private void createNewConfiguration()
	{
		LaunchConfiguration newConfig =
			new LaunchConfiguration("Unnamed", "", "", "", ""); 
		configs.add(newConfig);
		configurationsModel.addElement(newConfig);
		configurationsList.setSelectedIndex(configs.size() - 1);
		updateTextFields();
	}
	private void updateTextFields()
	// Update the text fields to the selected configuration
	{
		int index = configurationsList.getSelectedIndex();
		if (index > -1) {
			currentConfig = configs.getByIndex(index);
			configurationTF.setText(currentConfig.getName());
			programTF.getTextField().setText(currentConfig.getProgram());
			argumentsTF.setText(currentConfig.getArguments());
			directoryTF.setText(currentConfig.getDirectory());
			environmentTF.setText(currentConfig.getEnvironment());
			update.setEnabled(true);
		} else {
			currentConfig = null;
			configurationTF.setText("");
			programTF.getTextField().setText("");
			argumentsTF.setText("");
			directoryTF.setText("");
			environmentTF.setText("");
			update.setEnabled(false);
		}
	}
	private void updateSelectedConfiguration()
	{
		int index = configurationsList.getSelectedIndex();
		String name = configurationTF.getText();
		currentConfig.setName(name);
		currentConfig.setProgram(programTF.getTextField().getText());
		currentConfig.setArguments(argumentsTF.getText());
		currentConfig.setDirectory(directoryTF.getText());
		currentConfig.setEnvironment(environmentTF.getText());
		configurationsModel.set(index, name);
	}
	
	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void save()
	{
		updateSelectedConfiguration();
		configs.save();
	}
}
/** ***********************************************************************EOF */

