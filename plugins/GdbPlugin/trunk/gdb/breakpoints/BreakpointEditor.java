package gdb.breakpoints;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class BreakpointEditor extends JDialog {

	Breakpoint bp;
	JTextField condition;
	JTextField skipCount;
	JCheckBox enabled;
	
	public BreakpointEditor(Breakpoint bp) {
		super(jEdit.getActiveView(), true);
		this.bp = bp;
		setLayout(new GridLayout(0, 1));
		String desc = "Breakpoint #" + bp.getNumber() + ": " +
		bp.getFile() + ":" + bp.getLine();
		getContentPane().add(new JLabel(desc));
		enabled = new JCheckBox("Enabled");
		getContentPane().add(enabled);
		JPanel conditionPanel = new JPanel();
		conditionPanel.add(new JLabel("Condition:"));
		condition = new JTextField(40);
		conditionPanel.add(condition);
		getContentPane().add(conditionPanel);
		JPanel skipCountPanel = new JPanel();
		skipCountPanel.add(new JLabel("Skip count:"));
		skipCount = new JTextField(40);
		skipCount.setInputVerifier(new InputVerifier() {
			public boolean verify(JComponent comp) {
				String text = ((JTextField)comp).getText();
				try {
					Integer.parseInt(text);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(BreakpointEditor.this,
							"Skip count must be a positive integer.");
					return false;
				}
				return true;
			}
		});
		skipCountPanel.add(skipCount);
		getContentPane().add(skipCountPanel);
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
		enabled.setSelected(bp.isEnabled());
		skipCount.setText(String.valueOf(bp.getSkipCount()));
		condition.setText(bp.getCondition());
	}
	protected void cancel() {
		dispose();
	}

	protected void commit() {
		bp.setSkipCount(Integer.parseInt(skipCount.getText()));
		bp.setCondition(condition.getText());
		bp.setEnabled(enabled.isSelected());
		dispose();
	}
}
