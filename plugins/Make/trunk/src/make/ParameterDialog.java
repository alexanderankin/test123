package make;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.EnhancedDialog;

/*
 * TODO: try implementing this with GridBagLayout to get the labels
 * to align.
 */
 
public class ParameterDialog extends EnhancedDialog {
	public HashMap<String, String> valueMap;
	public boolean ok;
	private HashMap<String, JTextField> fieldMap;
	
	public ParameterDialog(View view, String title, LinkedList<String> params) {
		super(view, title, true);
		super.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.ok = false;
		this.valueMap = new HashMap<String, String>();
		this.fieldMap = new HashMap<String, JTextField>();
		
		for (String p : params) {
			JTextField field = new JTextField(20);
			this.fieldMap.put(p, field);
			
			Box fieldBox = Box.createHorizontalBox();
			fieldBox.add(new JLabel(p + ":"));
			fieldBox.add(Box.createRigidArea(new Dimension(6, 0)));
			fieldBox.add(Box.createHorizontalGlue());
			fieldBox.add(field);
			content.add(fieldBox);
			content.add(Box.createRigidArea(new Dimension(0, 2)));
		}
		
		content.add(Box.createRigidArea(new Dimension(0, 6)));
		content.add(new JSeparator());
		content.add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		
		JButton okBtn = new JButton(jEdit.getProperty("common.ok", "OK"));
		JButton cancelBtn = new JButton(jEdit.getProperty("common.cancel", "Cancel"));
		okBtn.setMnemonic(KeyEvent.VK_O);
		cancelBtn.setMnemonic(KeyEvent.VK_C);
		
		okBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ok();
				}
		});
		
		cancelBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
		});
		
		buttonPanel.add(okBtn);
		buttonPanel.add(Box.createRigidArea(new Dimension(6, 0)));
		buttonPanel.add(cancelBtn);
		content.add(buttonPanel);
		
		pane.add(BorderLayout.CENTER, content);
		this.setContentPane(pane);
		this.pack();
		//this.setResizable(false);
	}
	
	protected void save() {
		for (String key : fieldMap.keySet()) {
			valueMap.put(key, fieldMap.get(key).getText());
		}
	}
	
	public void ok() {
		save();
		this.ok = true;
		dispose();
	}
	
	public void cancel() {
		this.ok = false;
		dispose();
	}
}
