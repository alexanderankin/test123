package make;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.ThreadUtilities;
import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.action.Action;

public class PVMakeAction extends Action {
	private Buildfile file;
	
	public JComponent getMenuItem() {
		this.cmItem = new JMenu("Make");
		return this.cmItem;
	}
	
	public String getText() {
		return "Shit, you found me!";
	}
	
	public void prepareForNode(VPTNode node) {
		if (node.isFile()) {
			VFSFile nodeFile = ((VPTFile)node).getFile();
			this.file = MakePlugin.getBuildfileForPath(MiscUtilities.getParentOfPath(nodeFile.getPath()), nodeFile.getName());
			if (this.file != null) {
				this.cmItem.setVisible(true);
				((JMenu)this.cmItem).setText(this.file.getName());
				this.fillMenu(this.file);
			} else {
				this.cmItem.setVisible(false);
			}
		} else if (node.isProject()) {
			this.file = null;
			File root = new File(((VPTProject)node).getRootPath());
			File[] files = root.listFiles();
			if (files != null) {
				for (int i = 0; i<files.length; i++) {
					this.file = MakePlugin.getBuildfileForPath(files[i].getParent(), files[i].getName());
					if (this.file != null)
						break;
				}
			}
			
			if (this.file != null) {
				this.cmItem.setVisible(true);
				((JMenu)this.cmItem).setText(this.file.getName());
				fillMenu(this.file);
			} else {
				this.cmItem.setVisible(false);
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
		}
	}
}
