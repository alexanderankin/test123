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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import common.gui.FileTextField;

@SuppressWarnings("serial")
public class UpdaterOptions extends AbstractOptionPane
{
	private static final String DEFAULT_LOG_FILE = "updaterPlugin.log";
	private static final String UPDATE_LOG_FILE_PROP = "updater.values.updateLogFile";
	private static final String INSTALL_DIR_PROP = "updater.values.installDir";
	private static final String UPDATE_SOURCE_CLASS_PROP = "updater.values.updateSourceClassName";
	private static final String UPDATE_ON_STARTUP_PROP = "updater.values.updateOnStartup";
	private static final String UPDATE_PERIOD_PROP = "updater.values.updatePeriod";
	private static final String INTERACTIVE_INSTALL_PROP = "updater.values.interactiveInstall";
	private static final String UNIX_SCRIPT_DIR_PROP = "updater.values.unixScriptDir";
	private static final String UNIX_MAN_DIR_PROP = "updater.values.unixManDir";
	private FileTextField logFile;
	private JTextField installDir;
	private JRadioButton releaseUpdateSource;
	private JRadioButton dailyBuildUpdateSource;
	private JCheckBox updateOnStartup;
	private JSpinner updatePeriod;
	private JCheckBox interactiveInstall;
	private JTextField unixScriptDir;
	private JTextField unixManDir; 

	public UpdaterOptions()
	{
		super("updater");
	}

	@Override
	protected void _init()
	{
		logFile = new FileTextField(getUpdateLogFile(), false);
		addComponent(jEdit.getProperty("updater.options.updateLogFile"),
			logFile);
		JPanel installDirPanel = new JPanel(new BorderLayout());
		installDir = new JTextField(getInstallDir(), 40);
		installDirPanel.add(installDir, BorderLayout.CENTER);
		JButton installDirBrowse = new JButton(jEdit.getProperty(
			"common.gui.filetextfield.choose"));
		installDirPanel.add(installDirBrowse, BorderLayout.EAST);
		installDirBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser jfc = new JFileChooser(installDir.getText());
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (jfc.showOpenDialog(installDir) == JFileChooser.APPROVE_OPTION)
					installDir.setText(jfc.getSelectedFile().getAbsolutePath());
			}
		});
		addComponent(jEdit.getProperty("updater.options.installDir"),
			installDirPanel);
		JPanel updateSourcePanel = new JPanel();
		updateSourcePanel.setBorder(BorderFactory.createTitledBorder(
			jEdit.getProperty("updater.options.defaultUpdateSource")));
		ButtonGroup updateSourceGroup = new ButtonGroup();
		releaseUpdateSource = new JRadioButton(jEdit.getProperty(
			"updater.options.releaseUpdateSource"));
		updateSourcePanel.add(releaseUpdateSource);
		updateSourceGroup.add(releaseUpdateSource);
		dailyBuildUpdateSource = new JRadioButton(jEdit.getProperty(
			"updater.options.dailyBuildUpdateSource"));
		updateSourcePanel.add(dailyBuildUpdateSource);
		updateSourceGroup.add(dailyBuildUpdateSource);
		addComponent(updateSourcePanel);
		String updateSourceClass = getUpdateSourceClassName();
		if (DailyBuildUpdateSource.class.getCanonicalName().equals(updateSourceClass))
			dailyBuildUpdateSource.setSelected(true);
		else
			releaseUpdateSource.setSelected(true);

		updateOnStartup = new JCheckBox(jEdit.getProperty(
			"updater.options.updateOnStartup"), isUpdateOnStartup());
		addComponent(updateOnStartup);
		JPanel updateEveryPanel = new JPanel();
		updateEveryPanel.add(new JLabel(jEdit.getProperty(
			"updater.options.updateEvery")));
		SpinnerModel model = new SpinnerNumberModel(getUpdatePeriod(), 0, 30, 1);
		updatePeriod = new JSpinner(model);
		updateEveryPanel.add(updatePeriod);
		updateEveryPanel.add(new JLabel(jEdit.getProperty(
			"updater.options.updatePeriod")));
		addComponent(updateEveryPanel);
		interactiveInstall = new JCheckBox(jEdit.getProperty(
			"updater.options.interactiveInstall"), isInteractiveInstall());
		addComponent(interactiveInstall);
		
		// Unix-specific
		unixScriptDir = new JTextField(getUnixScriptDir());
		addComponent(jEdit.getProperty("updater.options.unixScriptDir"),
			unixScriptDir);
		unixManDir = new JTextField(getUnixManDir());
		addComponent(jEdit.getProperty("updater.options.unixManDir"),
			unixManDir);
	}

	@Override
	protected void _save()
	{
		jEdit.setProperty(UPDATE_LOG_FILE_PROP,
			logFile.getTextField().getText());
		jEdit.setProperty(INSTALL_DIR_PROP, installDir.getText());
		jEdit.setProperty(UPDATE_SOURCE_CLASS_PROP,
			(dailyBuildUpdateSource.isSelected() ?
				DailyBuildUpdateSource.class.getCanonicalName() :
				ReleasedUpdateSource.class.getCanonicalName()));
		jEdit.setBooleanProperty(UPDATE_ON_STARTUP_PROP,
			updateOnStartup.isSelected());
		jEdit.setIntegerProperty(UPDATE_PERIOD_PROP,
			Integer.valueOf(updatePeriod.getValue().toString()));
		jEdit.setBooleanProperty(INTERACTIVE_INSTALL_PROP,
			interactiveInstall.isSelected());
		jEdit.setProperty(UNIX_SCRIPT_DIR_PROP, unixScriptDir.getText());
		jEdit.setProperty(UNIX_MAN_DIR_PROP, unixManDir.getText());
	}

	public static boolean isUpdateOnStartup()
	{
		return jEdit.getBooleanProperty(UPDATE_ON_STARTUP_PROP, false);
	}

	public static int getUpdatePeriod()
	{
		return jEdit.getIntegerProperty(UPDATE_PERIOD_PROP, 0);
	}

	public static String getUpdateSourceClassName()
	{
		return jEdit.getProperty(UPDATE_SOURCE_CLASS_PROP,
			ReleasedUpdateSource.class.getCanonicalName());
	}

	public static String getUpdateLogFile()
	{
		return jEdit.getProperty(UPDATE_LOG_FILE_PROP,
			jEdit.getSettingsDirectory() + File.separator + DEFAULT_LOG_FILE);
	}

	public static String getInstallDir()
	{
		return jEdit.getProperty(INSTALL_DIR_PROP, jEdit.getJEditHome());
	}
	public static boolean isInteractiveInstall()
	{
		return jEdit.getBooleanProperty(INTERACTIVE_INSTALL_PROP, false);
	}

	public static String getUnixScriptDir()
	{
		return jEdit.getProperty(UNIX_SCRIPT_DIR_PROP, "");
	}

	public static String getUnixManDir()
	{
		return jEdit.getProperty(UNIX_MAN_DIR_PROP, "");
	}
}
