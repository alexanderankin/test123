package gdb.breakpoints;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class WatchpointEditor extends JDialog {
	private JTextField what;
	private JCheckBox read;
	private JCheckBox write;
	
	public WatchpointEditor() {
		super(jEdit.getActiveView(), "Add Watchpoint", true);
		setLayout(new GridLayout(0, 1));
		JPanel whatPanel = new JPanel();
		whatPanel.add(new JLabel("What:"));
		what = new JTextField(40);
		whatPanel.add(what);
		getContentPane().add(whatPanel);
		read = new JCheckBox("Read");
		getContentPane().add(read);
		write = new JCheckBox("Write");
		getContentPane().add(write);
		JPanel buttonsPanel = new JPanel();
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				commit();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancel();
			}
		});
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		add(buttonsPanel);
		initialize();
		pack();
	}

	private void initialize() {
		read.setSelected(false);
		write.setSelected(true);
	}
	protected void cancel() {
		dispose();
	}

	protected void commit() {
		new Breakpoint(what.getText(), read.isSelected(), write.isSelected());
		dispose();
	}

}
