/*
 * ProjectCommandOptionsService.java
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2010-2013 Damien Radtke, Alan Ezust
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
 
package console.options;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.jEdit;

import common.gui.FileTextField;

import console.Console;
import console.Shell;
import projectviewer.vpt.VPTProject;
import projectviewer.gui.OptionPaneBase;

/** ProjectViewer Console OptionPane
 * 
 * An option pane for configuring project commands
 * 
 * @author Damien Radtke 
 * @author Alan Ezust 
 * 
 */
@SuppressWarnings("serial")
public class ProjectCommandOptionPane extends OptionPaneBase implements ActionListener {
	
	private VPTProject proj;
	private JComboBox<String> shell;
	private String _shell;
	private HistoryTextField compile;
		
	private HistoryTextField run;
	private JCheckBox useRunDir;
	private FileTextField runDir;
	private String _runDir;
	
	/*
 	 * Constructor for ProjectCommandOptionPane
 	 */
	public ProjectCommandOptionPane(VPTProject proj) {
		super("pv.commands", "console");
		this.proj = proj;
	}
	
	protected void _init() {
		
		compile 			= new HistoryTextField("console.compile.project");
		
		run 				= new HistoryTextField("console.run.project");
		useRunDir 			= new JCheckBox(jEdit.getProperty("options.pv.useRunDir"));
		runDir 				= new FileTextField();
		shell 				= new JComboBox<String>(Shell.getShellNames());
		
		runDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
//		compile.setToolTipText(jEdit.getProperty("options.pv.commands.tooltip"));
//		run.setToolTipText(jEdit.getProperty("options.pv.commands.tooltip"));
		shell.setToolTipText(jEdit.getProperty("options.pv.shell.tooltip"));
				
		_runDir = proj.getProperty("console.runDir");
		useRunDir.setSelected(_runDir != null);
		useRunDir.addActionListener(this);
		if (_runDir == null) 
			_runDir = proj.getRootPath();
		runDir.getTextField().setText(_runDir);	
		
		
		_shell = proj.getProperty("console.shell");		
		// check that the previously chosen shell was not uninstalled:
		if ((_shell != null) &&	(console.Shell.getShell(_shell) == null))
			_shell = null;
		
		if (_shell == null) _shell = Console.shellForVFS(proj.getRootPath());
		shell.setSelectedItem(_shell);
		
		String _compile = proj.getProperty("console.compile");
		if (_compile != null)
			compile.setText(_compile);
		
		String _run = proj.getProperty("console.run");
		if (_run != null)
			run.setText(_run);
		
		addComponent(new JLabel(jEdit.getProperty("options.pv.commands.help")));
		addComponent(jEdit.getProperty("options.pv.commands.compile"), compile);
		addComponent(jEdit.getProperty("options.pv.commands.run"), run);
		addComponent(useRunDir, runDir);
		addComponent(jEdit.getProperty("options.pv.shell"), shell);
		actionPerformed(null);

	}
	
	protected void _save() {
		String ccmd = compile.getText();
		if (!ccmd.equals("")) {
			proj.setProperty("console.compile", ccmd);
			compile.getModel().addItem(ccmd);
		}
		else proj.removeProperty("console.compile");
		
		String rcmd = run.getText();
		if (!rcmd.equals("")) {
			run.getModel().addItem(rcmd);
			proj.setProperty("console.run", rcmd);
		}
		else proj.removeProperty("console.run");
				
		String favoriteShell = shell.getSelectedItem().toString();
		if (!favoriteShell.equals(_shell))
			proj.setProperty("console.shell", favoriteShell);
		String rDir = runDir.getTextField().getText();		
		if (useRunDir.isSelected() && (rDir != _runDir)) 
			proj.setProperty("console.runDir", rDir);		
		else proj.removeProperty("console.runDir");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		runDir.setEnabled(useRunDir.isSelected());
	}
	
}
