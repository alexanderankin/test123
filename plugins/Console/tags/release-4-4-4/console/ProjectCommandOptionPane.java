/*
 * ProjectCommandOptionsService.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Damien Radtke
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

import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import projectviewer.vpt.VPTProject;
import projectviewer.gui.OptionPaneBase;

/**
 * @author Damien Radtke
 * class ProjectCommandOptionPane
 * An option pane for configuring project commands
 */
public class ProjectCommandOptionPane extends OptionPaneBase {
	
	private VPTProject proj;
	private JTextField compile;
	private JTextField run;
	
	/*
 	 * Constructor for ProjectCommandOptionPane
 	 */
	public ProjectCommandOptionPane(VPTProject proj) {
		super("pv.commands", "console");
		this.proj = proj;
	}
	
	protected void _init() {
		compile = new JTextField();
		run = new JTextField();
		
		String _compile = proj.getProperty("console.compile");
		if (_compile != null)
			compile.setText(_compile);
		
		String _run = proj.getProperty("console.run");
		if (_run != null)
			run.setText(_run);
		
		addComponent(new JLabel(jEdit.getProperty("options.pv.commands.help")));
		addComponent(jEdit.getProperty("options.pv.commands.compile"), compile);
		addComponent(jEdit.getProperty("options.pv.commands.run"), run);
	}
	
	protected void _save() {
		proj.setProperty("console.compile", compile.getText());
		proj.setProperty("console.run", run.getText());
	}
	
}
