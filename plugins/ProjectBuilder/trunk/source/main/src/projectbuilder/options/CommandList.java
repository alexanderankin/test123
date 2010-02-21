package projectbuilder.options;
// imports {{{
import projectbuilder.command.Entry;
import projectbuilder.command.AddBuildSettingDialog;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.RolloverButton;
import projectviewer.vpt.VPTProject;
// }}} imports
public class CommandList extends JPanel {
	private String type;
	private DefaultListModel listModel;
	private JList list;
	private JButton add;
	private JButton remove;
	private JComboBox box;
	private VPTProject proj;
	public CommandList(final VPTProject proj, final String type) {
		super(new BorderLayout());
		this.proj = proj;
		this.type = type;
		JPanel listPanel = new JPanel(new BorderLayout());
		listModel = new DefaultListModel();
        list = new JList( listModel );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
        remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
        remove.setToolTipText(jEdit.getProperty("common.remove"));
        add.setToolTipText(jEdit.getProperty("common.add"));
        buttons.add(add);
        buttons.add(remove);
        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(250, 100));
        listPanel.add(BorderLayout.CENTER, pane);
        listPanel.add(BorderLayout.SOUTH, buttons);
        JPanel boxPanel = new JPanel(new BorderLayout());
        // TODO: Should probably not hardcode these in; use jEdit properties instead
        if (type.equals("build")) {
        	boxPanel.add(BorderLayout.WEST, new JLabel("Build with:  "));
        	boxPanel.add(BorderLayout.CENTER, box = new JComboBox());
        	add(BorderLayout.NORTH, new JLabel("Build"));
        } else if (type.equals("run")) {
        	boxPanel.add(BorderLayout.WEST, new JLabel("Run with:  "));
        	boxPanel.add(BorderLayout.CENTER, box = new JComboBox());
        	add(BorderLayout.NORTH, new JLabel("Run"));
        }
        add(BorderLayout.CENTER, listPanel);
        add(BorderLayout.SOUTH, boxPanel);
        add.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		AddBuildSettingDialog dialog = new AddBuildSettingDialog(jEdit.getActiveView(), proj);
        		if (dialog.data != null) {
        			/*
        			for (int i = 0; true; i++) {
        				if (proj.getProperty("projectBuilder.command."+type+"."+i) == null) {
        					proj.setProperty("projectBuilder.command."+type+"."+i, dialog.data);
        					break;
        				}
        			}
        			*/
        			listModel.addElement(new Entry(dialog.data));
        			updateBox();
        		}
        	}
        });
        remove.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int index = list.getSelectedIndex();
        		Entry setting = (Entry) listModel.get(index);
        		if (setting == null) return;
        		int choice = GUIUtilities.confirm(jEdit.getActiveView(),
        			"projectBuilder.msg.remove-setting", new String[] { type+": "+setting.toString() },
        			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        		if (choice == JOptionPane.YES_OPTION) {
        			// Remove it
        			listModel.remove(index);
        			updateBox();
        		}
        	}
        });
        populate();
	}
	private void updateBox() {
		box.removeAllItems();
		String saved = proj.getProperty("projectBuilder.command."+type);
		boolean boxIsValid = false;
		for (int i = 0; i < listModel.getSize(); i++) {
			Entry entry = (Entry) listModel.get(i);
			box.addItem(entry);
			if (entry.getProp().equals(saved)) {
				box.setSelectedItem(entry);
				boxIsValid = true;
			}
		}
		if (!boxIsValid) box.setSelectedItem(null);
	}
	private void populate() {
		listModel.clear();
		for (int i = 0; true; i++) {
			String prop = proj.getProperty("projectBuilder.command."+type+"."+i);
			if (prop == null) break;
			Entry cmd = new Entry(prop);
			listModel.addElement(cmd);
		}
		updateBox();
	}
	public void save() {
		for (int i = 0; i<listModel.getSize(); i++) {
			proj.setProperty("projectBuilder.command."+type+"."+i, ((Entry) listModel.get(i)).getProp());
		}
		for (int j=listModel.getSize(); true; j++) {
			if (proj.getProperty("projectBuilder.command."+type+"."+j) == null) break;
			proj.removeProperty("projectBuilder.command."+type+"."+j);
		}
		if (box.getSelectedItem() != null)
			proj.setProperty("projectBuilder.command."+type, ((Entry) box.getSelectedItem()).getProp());
		else
			proj.removeProperty("projectBuilder.command."+type);
		jEdit.saveSettings();
	}
}
