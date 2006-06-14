/*
 *  AntFarmOptionPane.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package antfarm;

import common.gui.pathbuilder.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class AntFarmOptionPane extends AbstractOptionPane implements ActionListener
{

	private JTextField _command;

	private JRadioButton _useSameJvm;

	private JRadioButton _useExternalScript;

	private JCheckBox _useProjectViewerIntegration;

	private JCheckBox _useEmacsOutput;

	private JCheckBox _saveOnExecute;

	private JCheckBox _supressSubTargets;

	private JButton _pickPath;

	private JButton _buildClasspath;

	private JTextField _classPath;

	private JComboBox _loggingLevel;

	private JCheckBox _singleClickOpens;
	
	public AntFarmOptionPane()
	{
		super("BuildOptions");
	}

	static String promptForAntScript(Component component)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX + "location"));
		int result = chooser.showDialog(component, jEdit
			.getProperty(AntFarmPlugin.OPTION_PREFIX + "file-dialog-approve"));
		if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null)
		{
			return chooser.getSelectedFile().toString();
		}
		return "";
	}

	public void _init()
	{
		_useSameJvm = new JRadioButton(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "use-same-jvm-button"));
		_useExternalScript = new JRadioButton(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "use-external-script-button"));
		_useSameJvm.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				_command.setEnabled(false);
				_classPath.setEnabled(true);
			}
		});

		_useExternalScript.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				_command.setEnabled(true);
				_classPath.setEnabled(false);
			}
		});

		_useProjectViewerIntegration = new JCheckBox(jEdit
			.getProperty(AntFarmPlugin.OPTION_PREFIX + "use-project-bridge-label"));
		_useProjectViewerIntegration.setSelected(jEdit
			.getBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "use-project-bridge"));
		_useEmacsOutput = new JCheckBox(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "output-emacs-label"));
		_useEmacsOutput.setSelected(jEdit.getBooleanProperty(AntFarmPlugin.OPTION_PREFIX
			+ "output-emacs"));
		_saveOnExecute = new JCheckBox(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "save-on-execute-label"));
		_saveOnExecute.setSelected(jEdit.getBooleanProperty(AntFarmPlugin.OPTION_PREFIX
			+ "save-on-execute"));
		_supressSubTargets = new JCheckBox(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "supress-sub-targets-label"));
		_supressSubTargets.setSelected(jEdit.getBooleanProperty(AntFarmPlugin.OPTION_PREFIX
			+ "supress-sub-targets"));

		boolean useSameJvmSelected = jEdit.getBooleanProperty(AntFarmPlugin.OPTION_PREFIX
			+ "use-same-jvm");
		if (useSameJvmSelected)
			_useSameJvm.setSelected(true);
		else
			_useExternalScript.setSelected(true);

		ButtonGroup group = new ButtonGroup();
		group.add(_useSameJvm);
		group.add(_useExternalScript);

		_classPath = new JTextField(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "classpath"), 30);
		_classPath.setEnabled(useSameJvmSelected);

		_buildClasspath = new JButton(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "build-classpath"));
		_buildClasspath.addActionListener(this);

		JPanel classPathPanel = new JPanel();
		classPathPanel.add(_classPath);
		classPathPanel.add(_buildClasspath);

		_command = new JTextField(jEdit
			.getProperty(AntFarmPlugin.OPTION_PREFIX + "command"), 30);
		_command.setEnabled(!useSameJvmSelected);

		_pickPath = new JButton(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "choose-antpath"));
		_pickPath.addActionListener(this);

		JPanel levelPanel = new JPanel();
		levelPanel.setLayout(new FlowLayout());
		levelPanel.add(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "logging-level-label")));
		_loggingLevel = new JComboBox(LogLevelEnum.getAll());
		_loggingLevel.setSelectedItem(LogLevelEnum.getLogLevel(jEdit
			.getIntegerProperty(AntFarmPlugin.OPTION_PREFIX + "logging-level",
				LogLevelEnum.INFO.getValue())));
		levelPanel.add(_loggingLevel);

		JPanel pathPanel = new JPanel();
		pathPanel.add(_command);
		pathPanel.add(_pickPath);

		addSeparator(AntFarmPlugin.OPTION_PREFIX + "build-method");
		addComponent(_useSameJvm);
		addComponent(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX
			+ "classpath-label")));
		addComponent(classPathPanel);

		addComponent(_useExternalScript);
		addComponent(new JLabel(jEdit.getProperty(AntFarmPlugin.OPTION_PREFIX + "location")));
		addComponent(pathPanel);

		addSeparator(AntFarmPlugin.OPTION_PREFIX + "general-options");
		addComponent(_useProjectViewerIntegration);
		addComponent(_useEmacsOutput);
		addComponent(_saveOnExecute);
		addComponent(_supressSubTargets);
		addComponent(levelPanel);
		_singleClickOpens = new JCheckBox( jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "open-on-singleclick-label" ) );
		_singleClickOpens.setSelected(jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "open-on-singleclick" ) );
		addComponent(_singleClickOpens);
	}

	public void _save()
	{
		jEdit.setBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "open-on-singleclick", _singleClickOpens.isSelected());
		jEdit.setProperty(AntFarmPlugin.OPTION_PREFIX + "classpath", _classPath.getText());
		jEdit.setProperty(AntFarmPlugin.OPTION_PREFIX + "command", _command.getText());
		jEdit.setBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "use-same-jvm", _useSameJvm
			.isSelected());
		jEdit.setBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "use-project-bridge",
			_useProjectViewerIntegration.isSelected());
		jEdit.setBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "output-emacs",
			_useEmacsOutput.isSelected());
		jEdit.setBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "save-on-execute",
			_saveOnExecute.isSelected());
		jEdit.setBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "supress-sub-targets",
			_supressSubTargets.isSelected());

		jEdit.setIntegerProperty(AntFarmPlugin.OPTION_PREFIX + "logging-level",
			((LogLevelEnum) _loggingLevel.getSelectedItem()).getValue());

		// make sure all of the jars specified in _classPath are loaded.
		AntFarmPlugin.loadCustomClasspath();

	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		PathBuilderDialog dialog;
		PathBuilder pathBuilder;
		Object parent = getTopLevelAncestor();
		Log.log(Log.DEBUG, this, "parent = " + parent.getClass().getName());
		Dialog parentDialog = null;
		Frame parentFrame = null;
		if (parent instanceof Dialog)
			parentDialog = (Dialog) parent;
		else if (parent instanceof Frame)
			parentFrame = (Frame) parent;

		if (source.equals(_buildClasspath))
		{
			if (parentFrame != null)
				dialog = new PathBuilderDialog(parentFrame, "Build Classpath");
			else
				dialog = new PathBuilderDialog(parentDialog, "Build Classpath");

			pathBuilder = dialog.getPathBuilder();
			pathBuilder.setPath(_classPath.getText());
			pathBuilder.setFileFilter(new ClasspathFilter());
			pathBuilder.setMultiSelectionEnabled(true);
			dialog.pack();
			if (parentFrame != null)
				dialog.setLocationRelativeTo(parentFrame);
			else
				dialog.setLocationRelativeTo(parentDialog);
			dialog.show();
			if (dialog.getResult())
			{
				_classPath.setText(pathBuilder.getPath());
			}
		}
		else if (source.equals(_pickPath))
		{
			String scriptPath = AntFarmOptionPane.promptForAntScript(this);
			if (scriptPath != "")
				_command.setText(scriptPath);
		}
	}

	private boolean isUseInternalJvmSelected()
	{
		return jEdit.getBooleanProperty(AntFarmPlugin.OPTION_PREFIX + "use-same-jvm");
	}
}
