/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package make;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.gui.StatusBar;
import org.gjt.sp.util.ThreadUtilities;
import org.gjt.sp.util.Log;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.action.Action;

public class PVMakeAction extends Action {
	public static final String BUILDFILE_PROP = "make.buildfile";
	
	private Buildfile file;
	
	public JComponent getMenuItem() {
		if (this.cmItem == null) {
			this.cmItem = new JMenu();
		}
		
		return this.cmItem;
	}
	
	public String getText() {
		return "Shit, you found me!";
	}
	
	public void prepareForNode(VPTNode node) {
		// if this node is a file, check if it's a valid buildfile
		if (node.isFile()) {
			VFSFile nodeFile = ((VPTFile)node).getFile();
			this.file = MakePlugin.getBuildfileForPath(MiscUtilities.getParentOfPath(nodeFile.getPath()), nodeFile.getName());
			if (this.file != null) {
				this.cmItem.setVisible(true);
				this.cmItem.setEnabled(true);
				((JMenu)this.cmItem).setText(this.file.getName());
				this.fillMenu(this.file);
			} else {
				this.cmItem.setVisible(false);
			}
		}
		// if this node is a project, scan its root for a valid buildfile
		else if (node.isProject()) {
			this.cmItem.setVisible(true);
			VPTProject project = (VPTProject)node;
			this.file = null;
			File root = new File(project.getRootPath());
			File[] files = root.listFiles();
			if (files != null) {
				for (int i = 0; i<files.length; i++) {
					this.file = MakePlugin.getBuildfileForPath(files[i].getParent(), files[i].getName());
					if (this.file != null)
						break;
				}
			}
			
			if (this.file != null) {
				this.cmItem.setEnabled(true);
				((JMenu)this.cmItem).setText(this.file.getName());
				fillMenu(this.file);
			} else {
				((JMenu)this.cmItem).setText(jEdit.getProperty("make.msg.no-buildfile", "No buildfile found"));
				this.cmItem.setEnabled(false);
			}
		} else {
			this.cmItem.setVisible(false);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		// do nothing
	}
	
	private void fillMenu(final Buildfile file) {
		this.cmItem.removeAll();
		for (final BuildTarget target : file.targets) {
			if (target.params.size() == 0) {
				JMenuItem item = new JMenuItem(target.name);
				if (target.desc != null) {
					item.setToolTipText(target.desc);
				}
				
				item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							file.runTarget(target);
						}
				});
				
				this.cmItem.add(item);
			} else {
				JMenu submenu = new JMenu(target.name);
				if (target.desc != null) {
					submenu.setToolTipText(target.desc);
				}
				
				JMenuItem runWithParams = new JMenuItem(jEdit.getProperty("make.msg.run-with-params", "Specify parameters..."));
				JMenuItem runWithoutParams = new JMenuItem(jEdit.getProperty("make.msg.run-without-params", "Run with defaults"));
				
				runWithParams.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							View view = jEdit.getActiveView();
							ParameterDialog dialog = new ParameterDialog(view,
								jEdit.getProperty("make.msg.parameter-dialog.title", "Specify Build Parameters"),
								target.params);
							dialog.setLocationRelativeTo(view);
							dialog.setVisible(true);
							
							if (dialog.ok) {
								file.runTargetWithParams(target, dialog.valueMap);
							}
						}
				});
				
				runWithoutParams.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							file.runTarget(target);
						}
				});
				
				submenu.add(runWithParams);
				submenu.add(runWithoutParams);
				this.cmItem.add(submenu);
			}
		}
		
		this.cmItem.add(new JSeparator());
		
		JMenuItem reloadItem = new JMenuItem(jEdit.getProperty("make.msg.reload", "Reload Targets"));
		this.cmItem.add(reloadItem);
		
		reloadItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final StatusBar status = jEdit.getActiveView().getStatus();
					status.setMessage(jEdit.getProperty("make.msg.reload.starting", "Reloading targets..."));
					ThreadUtilities.runInBackground(new Runnable() {
							public void run() {
								MakePlugin.clearCachedTargets(file.getPath());
								if (!file.parseTargets()) {
									GUIUtilities.error(jEdit.getActiveView(), "make.msg.parse-error", new String[] {});
								}
								status.setMessageAndClear(jEdit.getProperty("make.msg.reload.done", "Reloading targets... done."));
							}
					});
				}
		});
	}
}
