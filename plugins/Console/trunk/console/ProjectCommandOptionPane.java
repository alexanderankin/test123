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
 
package console;

import java.util.Vector;

import javax.swing.*;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.jEdit;
import projectviewer.vpt.VPTProject;
import projectviewer.gui.OptionPaneBase;

/**
 * @author Damien Radtke
 * Projectviewer Console OptionPane
 * An option pane for configuring project commands
 */
public class ProjectCommandOptionPane extends OptionPaneBase {
	
	private VPTProject proj;
	private JComboBox shell;
	private String _shell;
	private HistoryTextField compile;
	private HistoryTextField run;
	
	/*
 	 * Constructor for ProjectCommandOptionPane
 	 */
	public ProjectCommandOptionPane(VPTProject proj) {
		super("pv.commands", "console");
		this.proj = proj;
	}
	
	protected void _init() {
		
		compile = new HistoryTextField("console.compile.project");
		run = new HistoryTextField("console.run.project");
		
		
		shell = new JComboBox(Shell.getShellNames());	
		
		_shell = proj.getProperty("console.shell");
		if (_shell == null) {
			_shell = Console.shellForVFS(proj.getRootPath());	
		}
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
		addComponent(jEdit.getProperty("options.pv.shell"), shell);

	}
	
	protected void _save() {
		String ccmd = compile.getText();
		if (!ccmd.equals("")) {
			proj.setProperty("console.compile", compile.getText());
			compile.getModel().addItem(ccmd);
		}
		String rcmd = run.getText();
		if (!rcmd.equals("")) {
			run.getModel().addItem(rcmd);
			proj.setProperty("console.run", rcmd);
		}
		String favoriteShell = shell.getSelectedItem().toString();
		if (!favoriteShell.equals(_shell))
			proj.setProperty("console.shell", favoriteShell);
	}
	
}
