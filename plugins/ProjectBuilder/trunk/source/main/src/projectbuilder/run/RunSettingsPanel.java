package projectbuilder.run;
// imports {{{
import projectbuilder.build.BuildCommand;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import javax.swing.Box;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import projectviewer.vpt.VPTProject;

import common.gui.ListPanel;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.jEdit;
// }}} imports
/**
 * A dialog for editing run settings of a project
 */
public class RunSettingsPanel extends JPanel implements ActionListener {
	private VPTProject proj;
	private ListPanel list;
	private JButton addBtn;
	private JButton removeBtn;
	private JButton modifyBtn;
	private JPanel optionsPanel;
	public RunSettingsPanel(VPTProject proj) {
		super(new BorderLayout());
		setPreferredSize(new Dimension(400, 200));
		this.proj = proj;
		list = new ListPanel("Run commands:");
		list.setReorderable(true);
		String[] commands = RunCommand.getCommandList(proj);
		if (commands != null) {
			for (int i = 0; i<commands.length; i++) {
				list.addElement(commands[i]);
			}
		}
		addBtn = new JButton(GUIUtilities.loadIcon("Plus.png"));
		removeBtn = new JButton(GUIUtilities.loadIcon("Minus.png"));
		modifyBtn = new JButton(GUIUtilities.loadIcon("ButtonProperties.png"));
		addBtn.addActionListener(this);
		removeBtn.addActionListener(this);
		modifyBtn.addActionListener(this);
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(addBtn);
		buttonPanel.add(removeBtn);
		buttonPanel.add(modifyBtn);
		buttonPanel.add(Box.createHorizontalGlue());
		optionsPanel.add(buttonPanel);
		add(BorderLayout.CENTER, list);
		add(BorderLayout.SOUTH, optionsPanel);
	}
	
	/**
	 * Reads through the list and updates the project's run command property accordingly
	 */
	private void updateProps() {
		Object[] l = list.toArray();
		String p = "";
		for (int i = 0; i<l.length; i++) {
			p += l[i];
			if (i<(l.length-1))
				p += "|";
		}
		proj.setProperty("projectBuilder.command.run", p);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addBtn) {
			// Add a run command
			String cmd = GUIUtilities.input(jEdit.getActiveView(), "projectBuilder.msg.add-run-command", null);
			if (cmd == null || cmd.length() == 0) return;
			list.addElement(cmd);
			updateProps();
		} else if (source == removeBtn) {
			// Remove a run command
			for (Object ob : list.getSelectedValues()) {
				list.removeElement(ob);
			}
			updateProps();
		}
		else if (source == modifyBtn) {
			// Modify a run command
			String old = (String) list.getSelectedValues()[0];
			String cmd = GUIUtilities.input(jEdit.getActiveView(), "projectBuilder.msg.modify-run-command", old);
			if (cmd == null || cmd.length() == 0) return;
			list.removeElement(old);
			list.addElement(cmd);
			updateProps();
		}
	}
	
}
