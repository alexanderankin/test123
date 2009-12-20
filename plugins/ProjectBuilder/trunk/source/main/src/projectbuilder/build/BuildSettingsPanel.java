package projectbuilder.build;
// imports {{{
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.Box;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import projectviewer.vpt.VPTProject;

import common.gui.ListPanel;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.jEdit;
// }}} imports
// NOTE: This is more of a Java-style way of doing this, so it should probably be rewritten to be more "Groovy"-like
/**
 * A dialog for editing build settings of a project
 */
public class BuildSettingsPanel extends JPanel implements ActionListener {
	private VPTProject proj;
	private ListPanel list;
	private JButton addBtn;
	private JButton removeBtn;
	private JPanel buttonPanel;
	public BuildSettingsPanel(VPTProject proj) {
		super(new BorderLayout());
		setPreferredSize(new Dimension(400, 200));
		this.proj = proj;
		list = new ListPanel("Build commands:");
		list.setReorderable(true);
		String[] commands = BuildCommand.getCommandList(proj);
		if (commands != null) {
			for (int i = 0; i<commands.length; i++) {
				list.addElement(commands[i]);
			}
		}
		addBtn = new JButton(GUIUtilities.loadIcon("16x16/actions/list-add.png"));
		removeBtn = new JButton(GUIUtilities.loadIcon("16x16/actions/list-remove.png"));
		addBtn.addActionListener(this);
		removeBtn.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(addBtn);
		buttonPanel.add(removeBtn);
		buttonPanel.add(Box.createHorizontalGlue());
		add(BorderLayout.CENTER, list);
		add(BorderLayout.SOUTH, buttonPanel);
	}
	
	/**
	 * Reads through the list and updates the project's build command property accordingly
	 */
	private void updateProps() {
		Object[] l = list.toArray();
		String p = "";
		for (int i = 0; i<l.length; i++) {
			p += l[i];
			if (i<(l.length-1))
				p += "|";
		}
		proj.setProperty("projectBuilder.command.build", p);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(addBtn)) {
			// Add a build command
			String cmd = GUIUtilities.input(jEdit.getActiveView(), "projectBuilder.msg.add-build-command", null);
			if (cmd != null && cmd.length() > 0) {
				list.addElement(cmd);
				updateProps();
			}
		} else if (source.equals(removeBtn)) {
			// Remove a build command
			for (Object ob : list.getSelectedValues()) {
				list.removeElement(ob);
			}
			updateProps();
		}
	}
}
