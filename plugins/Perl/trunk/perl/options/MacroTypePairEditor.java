package perl.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.gjt.sp.jedit.GUIUtilities;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class MacroTypePairEditor extends JDialog {

	static final String PREFIX = Plugin.OPTION_PREFIX;
	private static final String GEOMETRY = "macro.type.pair.editor.geometry";
	private DefaultTableModel model;
	private int row;
	private JTextField typeTF;
	private JTextField macroTF;
	
	public MacroTypePairEditor(DefaultTableModel model, int row, JDialog parent) {
		super(parent, "Type -> GDB-macro Association Editor", true);
		addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
        		saveGeometry();
            }
        });
		this.model = model;
		this.row = row;
		JPanel fields = new JPanel();
		fields.setLayout(new GridLayout(0, 1));
		JPanel typePanel = new JPanel();
		typePanel.add(new JLabel("Value type:"));
		typeTF = new JTextField((String) model.getValueAt(row, 0), 40);
		typePanel.add(typeTF);
		fields.add(typePanel);
		JPanel macroPanel = new JPanel();
		macroPanel.add(new JLabel("GDB macro:"));
		macroTF = new JTextField((String) model.getValueAt(row, 1), 40);
		macroPanel.add(macroTF);
		fields.add(macroPanel);
		JPanel buttons = new JPanel();
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(true);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close(false);
			}
		});
		buttons.add(ok);
		buttons.add(cancel);
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = gc.gridy = 0;
		gc.weighty = 1;
		getContentPane().add(fields, gc);
		gc.gridy++;
		gc.weighty = 0;
		getContentPane().add(buttons, gc);
		pack();
		loadGeometry();
	}

	private void close(boolean accepted) {
		if (accepted) {
			model.setValueAt(typeTF.getText(), row, 0);
			model.setValueAt(macroTF.getText(), row, 1);
		}
		saveGeometry();
		setVisible(false);		
	}
	private void loadGeometry() {
		GUIUtilities.saveGeometry(this, GEOMETRY);
	}
	private void saveGeometry() {
		GUIUtilities.saveGeometry(this, GEOMETRY);
	}

}
