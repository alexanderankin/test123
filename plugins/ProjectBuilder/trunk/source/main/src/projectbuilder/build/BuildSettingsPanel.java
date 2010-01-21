package projectbuilder.build;
// imports {{{
import projectbuilder.build.BuildCommand;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
import org.gjt.sp.jedit.View;
// }}} imports
/**
 * A dialog for editing build settings of a project
 */
public class BuildSettingsPanel extends JDialog implements ActionListener {
	private VPTProject proj;
	private ListPanel list;
	private JButton addBtn;
	private JButton removeBtn;
	private JButton modifyBtn;
	private JPanel optionsPanel;
	public BuildSettingsPanel(View view, String title, VPTProject proj) {
		super(view, title);
		JPanel panel = new JPanel(new BorderLayout());
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
		panel.add(BorderLayout.CENTER, list);
		panel.add(BorderLayout.SOUTH, optionsPanel);
		add(panel);
		
		KeyListener escape_listener = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				}
			}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		};
		addKeyListener(escape_listener);
		list.addKeyListener(escape_listener);
		addBtn.addKeyListener(escape_listener);
		removeBtn.addKeyListener(escape_listener);
		modifyBtn.addKeyListener(escape_listener);
		optionsPanel.addKeyListener(escape_listener);
		
		pack();
		setLocationRelativeTo(view);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
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
		if (source == addBtn) {
			// Add a build command
			AddModifyBuildCommandDialog dialog = new AddModifyBuildCommandDialog(jEdit.getActiveView());
			String cmd = dialog.getValue();
			if (cmd == null) return;
			list.addElement(cmd);
			updateProps();
		} else if (source == removeBtn) {
			// Remove a build command
			for (Object ob : list.getSelectedValues()) {
				list.removeElement(ob);
			}
			updateProps();
		}
		else if (source == modifyBtn) {
			// Modify a build command
			// NOTE: This moves the modified build command to the end of the list. Try and find a better way.
			String old = (String) list.getSelectedValues()[0];
			AddModifyBuildCommandDialog dialog = new AddModifyBuildCommandDialog(jEdit.getActiveView(), old);
			String cmd = dialog.getValue();
			if (cmd == null || cmd.length() == 0) return;
			list.removeElement(old);
			list.addElement(cmd);
			updateProps();
		}
	}
	
}
